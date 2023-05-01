package com.projects.mypcb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projects.mypcb.dto.components.CPUDTO;
import com.projects.mypcb.dto.components.MotherboardDTO;
import com.projects.mypcb.dto.components.PSUDTO;
import com.projects.mypcb.dto.components.RAMDTO;
import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.CPU;
import com.projects.mypcb.entity.components.PSU;
import com.projects.mypcb.entity.components.RAM;
import com.projects.mypcb.global.ComputerBuild;
import com.projects.mypcb.service.ComponentComparer;
import com.projects.mypcb.service.ComponentService;
import com.projects.mypcb.service.ComponentTypeService;
import com.projects.mypcb.service.components.CPUService;
import com.projects.mypcb.service.components.MotherboardService;
import com.projects.mypcb.service.components.PSUService;
import com.projects.mypcb.service.components.RAMService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
    @Autowired
    ComponentTypeService componentTypeService;

    @Autowired
    ComponentService componentService;

    @Autowired
    RAMService ramService;

    @Autowired
    CPUService cpuService;

    @Autowired
    PSUService psuService;

    @Autowired
    MotherboardService motherboardService;

    @GetMapping("/error")
    public String errorGet(){
        return "access-denied";
    }

    @GetMapping({ "/", "/home" })
    public String home(Model model) {
        model.addAttribute("buildCount", ComputerBuild.getCount());

        return "index";
    }

    @GetMapping("/shop")
    public String shop(Model model,
            @RequestParam(defaultValue = "") String searchKey) {
        model.addAttribute("buildCount", ComputerBuild.getCount());

        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        List<Component> searchResult = componentService.getAllComponents(searchKey);
        model.addAttribute("components", searchResult);
        System.out.println(searchResult.size());
        return "shop";
    }

    @GetMapping("/shop/componentType/{id}")
    public String shopByComponentType(Model model, @PathVariable int id) {
        model.addAttribute("buildCount", ComputerBuild.getCount());

        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        model.addAttribute("components", componentService.getAllComponentsByTypeId(id));
        return "shop";
    }

    @GetMapping("/shop/viewcomponent/{id}")
    public String viewProduct(Model model, @PathVariable int id) {
        model.addAttribute("buildCount", ComputerBuild.getCount());

        String name = idToName(id);
        if (name.equals("RAM"))
            model.addAttribute("component", ramService.getRAMById(id).get());
        if (name.equals("CPU"))
            model.addAttribute("component", cpuService.getCPUById(id).get());
        if (name.equals("PSU"))
            model.addAttribute("component", psuService.getPSUById(id).get());
        if (name.equals("Motherboard"))
            model.addAttribute("component", motherboardService.getMotherboardById(id).get());

        return "viewProduct";
    }

    @GetMapping("build/removeItem/{index}/{type}")
    public String buildItemRemove(@PathVariable int index, @PathVariable int type) {
        String name = idToType(type);
        if (name.equals("RAM"))
            ComputerBuild.rams.remove(index);
        if (name.equals("CPU"))
            ComputerBuild.cpus.remove(index);
        if (name.equals("PSU"))
            ComputerBuild.psus.remove(index);
            if (name.equals("Motherboard"))
            ComputerBuild.motherboards.remove(index);
        return "redirect:/build";

    }

    private String idToName(int id) {
        return componentService.getComponentById(id).get().getComponentType().getName();
    }

    private String idToType(int id) {
        return componentTypeService.getComponentTypeById(id).get().getName();
    }

    
    @GetMapping("/shop/compare/{type}")
    public String comparisonGet(@PathVariable String type, Model model){
        if (type.equals("RAM")) {
            model.addAttribute("ramDTO", new RAMDTO());
        }
        if (type.equals("CPU")) {
            model.addAttribute("cpuDTO", new CPUDTO());
        }
        if (type.equals("PSU")) {
            model.addAttribute("psuDTO", new PSUDTO());
        }
        if (type.equals("Motherboard")) {
            model.addAttribute("motherboardDTO", new MotherboardDTO());
        }

        return "compare";
    }

    @PostMapping("/shop/compare/{type}")
    public String availabilityCheckPost(@ModelAttribute("ramDTO") RAMDTO ramDTO, Model model) throws IOException {
        if (ramDTO.getMemory() == null)
            ramDTO.setMemory(0);
        if (ramDTO.getSpeed() == null)
            ramDTO.setSpeed(0);

        List<RAM> availableRAMs = ramService.getAllRAMs(ramDTO.getName(), ramDTO.getManufacturerName(),
                ramDTO.getSpeed(), ramDTO.getMemory());
        model.addAttribute("availableRAMs", availableRAMs);
        model.addAttribute("type", "RAM");

        System.out.println(ramDTO.getName() + " " + ramDTO.getSpeed() + " " + ramDTO.getMemory() + " ");
        System.out.println(availableRAMs.size());

        return "compare";
    }

    @PostMapping("/shop/compare/submit")
    public String compareSelectedComponents(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        Enumeration<String> parameterNames = request.getParameterNames();
        List<Component> selectedComponents = new ArrayList<>();
    
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            int componentId = Integer.parseInt(paramValue);
            Component component = componentService.getComponentById(componentId).orElse(null);
            if (component != null) {
                selectedComponents.add(component);
            }
        }
    
        if (selectedComponents.size() != 2) {
            redirectAttributes.addFlashAttribute("error", "Please select exactly 2 components for comparison.");
            return "redirect:/shop/compare/" + selectedComponents.get(0).getComponentType().getName();
        }
        System.out.println(selectedComponents.get(0));
        System.out.println(selectedComponents.get(1));
        // Save the selected components for further processing
        model.addAttribute("selectedComponent1", selectedComponents.get(0));
        model.addAttribute("selectedComponent2", selectedComponents.get(1));
    
        // Redirect to the desired page or process the comparison here
        // ...
    
        return "compareResult";
        
    }
    
    

}
