package com.projects.mypcb.controller;

import java.io.IOException;
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

    // @GetMapping("/shop/compare/{type}")
    // public String showRamComparisonForm(Model model) {
    // ComponentComparer ramComparisonWrapper = new ComponentComparer();
    // model.addAttribute("ramComparisonWrapper", ramComparisonWrapper);
    // return "ram_comparison";
    // }

    @GetMapping("/shop/compare/{type}")
    public String compareGet(Model model, @PathVariable String type, HttpServletRequest request) {

        if (type.equals("RAM")) {
            model.addAttribute("ramDTO1", new RAMDTO());
            model.addAttribute("ramDTO2", new RAMDTO());

            if (request.getSession().getAttribute("ramDTO1") != null) {
                model.addAttribute("ramDTO1", request.getSession().getAttribute("ramDTO1"));
            } else {
                model.addAttribute("ramDTO1", new RAMDTO());
            }
        
            if (request.getSession().getAttribute("ramDTO2") != null) {
                model.addAttribute("ramDTO2", request.getSession().getAttribute("ramDTO2"));
            } else {
                model.addAttribute("ramDTO2", new RAMDTO());
            }
            
        }
        if (type.equals("CPU")) {
            model.addAttribute("cpuDTO1", new CPUDTO());
            model.addAttribute("cpuDTO2", new CPUDTO());
        }
        if (type.equals("PSU")) {
            model.addAttribute("psuDTO1", new PSUDTO());
            model.addAttribute("psuDTO2", new PSUDTO());
        }
        model.addAttribute("type", type);

        if (request.getSession().getAttribute("component1Results") != null) {
            model.addAttribute("component1Results", request.getSession().getAttribute("component1Results"));
        }
        if (request.getSession().getAttribute("component2Results") != null) {
            model.addAttribute("component2Results", request.getSession().getAttribute("component2Results"));
        }

        // Long id1, id2;
        // model.addAttribute("savedId1", id1);
        // model.addAttribute("savedId2", id2);

        
        return "compare";

    }

    @PostMapping("/shop/compare/{type}/component1")
    public String compareComponent1Post(@PathVariable String type,
            @ModelAttribute("ramDTO1") RAMDTO ramDTO1,
            Model model, HttpServletRequest request) {
        if (ramDTO1.getMemory() == null)
            ramDTO1.setMemory(0);
        if (ramDTO1.getSpeed() == null)
            ramDTO1.setSpeed(0);
        List<RAM> availableRAMs = ramService.getAllRAMs(ramDTO1.getName(), ramDTO1.getManufacturerName(),
                ramDTO1.getSpeed(), ramDTO1.getMemory());

        model.addAttribute("type", "RAM");

        System.out.println("DTO1: " + ramDTO1.getName() + " " + ramDTO1.getSpeed() + " " + ramDTO1.getMemory() + " ");
        System.out.println(availableRAMs.size());

        request.getSession().setAttribute("component1Results", availableRAMs);
        request.getSession().setAttribute("ramDTO1", ramDTO1);

        return "redirect:/shop/compare/{type}";
    }

    @PostMapping("/shop/compare/{type}/component2")
    public String compareComponent2Post(@PathVariable String type,
            @ModelAttribute("ramDTO2") RAMDTO ramDTO2,
            Model model, HttpServletRequest request) {
        if (ramDTO2.getMemory() == null)
            ramDTO2.setMemory(0);
        if (ramDTO2.getSpeed() == null)
            ramDTO2.setSpeed(0);
        List<RAM> availableRAMs = ramService.getAllRAMs(ramDTO2.getName(), ramDTO2.getManufacturerName(),
                ramDTO2.getSpeed(), ramDTO2.getMemory());

        model.addAttribute("type", "RAM");

        System.out.println("DTO2: " + ramDTO2.getName() + " " + ramDTO2.getSpeed() + " " + ramDTO2.getMemory() + " ");
        System.out.println(availableRAMs.size());

        request.getSession().setAttribute("component2Results", availableRAMs);
request.getSession().setAttribute("ramDTO2", ramDTO2);
        return "redirect:/shop/compare/{type}";
    }

    // @PostMapping("/shop/compare/RAM1")
    // public String availabilityCheckPostRam1(@ModelAttribute("ramDTO1") RAMDTO
    // ramDTO1, Model model) throws IOException {
    // if (ramDTO1.getMemory() == null)
    // ramDTO1.setMemory(0);
    // if (ramDTO1.getSpeed() == null)
    // ramDTO1.setSpeed(0);

    // List<RAM> availableRAMs1 = ramService.getAllRAMs(ramDTO1.getName(),
    // ramDTO1.getManufacturerName(),
    // ramDTO1.getSpeed(), ramDTO1.getMemory());

    // model.addAttribute("availableRAMs1", availableRAMs1);

    // model.addAttribute("type", "RAM");

    // System.out.println("DTO1: " + ramDTO1.getName() + " " + ramDTO1.getSpeed() +
    // " " + ramDTO1.getMemory() + " ");
    // System.out.println(availableRAMs1.size());

    // return "compare";
    // }

    // @PostMapping("/shop/compare/RAM2")
    // public String availabilityCheckPostRam2(@ModelAttribute("ramDTO2")RAMDTO
    // ramDTO2, Model model) throws IOException {

    // if (ramDTO2.getMemory() == null)
    // ramDTO2.setMemory(0);
    // if (ramDTO2.getSpeed() == null)
    // ramDTO2.setSpeed(0);

    // List<RAM> availableRAMs2 = ramService.getAllRAMs(ramDTO2.getName(),
    // ramDTO2.getManufacturerName(),
    // ramDTO2.getSpeed(), ramDTO2.getMemory());

    // model.addAttribute("availableRAMs2", availableRAMs2);
    // model.addAttribute("type", "RAM");

    // System.out.println("DTO2: " +ramDTO2.getName() + " " + ramDTO2.getSpeed() + "
    // " + ramDTO2.getMemory() + " ");
    // System.out.println(availableRAMs2.size());

    // return "compare";
    // }

    // @PostMapping("/manager/availability/CPU")
    // public String availabilityCheckPost(@ModelAttribute("cpuDTO") CPUDTO cpuDTO,
    // Model model) throws IOException {
    // if (cpuDTO.getSeries() == null)
    // cpuDTO.setSeries("");
    // if (cpuDTO.getGeneration() == null)
    // cpuDTO.setGeneration(0);
    // if (cpuDTO.getModel() == null)
    // cpuDTO.setModel("");
    // if (cpuDTO.getCores() == null)
    // cpuDTO.setCores(0);
    // if (cpuDTO.getCoreSpeed() == null)
    // cpuDTO.setCoreSpeed(0.0);

    // List<CPU> availableCPUs = cpuService.getAllCPUs(cpuDTO.getName(),
    // cpuDTO.getManufacturerName(),
    // cpuDTO.getSeries(), cpuDTO.getGeneration(), cpuDTO.getModel(),
    // cpuDTO.getCores(),
    // cpuDTO.getCoreSpeed());
    // model.addAttribute("availableCPUs", availableCPUs);
    // model.addAttribute("type", "CPU");

    // System.out.println(cpuDTO.getSeries() + " " + cpuDTO.getGeneration() + " " +
    // cpuDTO.getModel() + " "
    // + cpuDTO.getCores() + " " + cpuDTO.getCoreSpeed());
    // System.out.println(availableCPUs.size());

    // return "availability";
    // }

    // @PostMapping("/manager/availability/PSU")
    // public String availabilityCheckPost(@ModelAttribute("psuDTO") PSUDTO psuDTO,
    // Model model) throws IOException {
    // if (psuDTO.getType() == null)
    // psuDTO.setType("");
    // if (psuDTO.getPowerSupply() == null)
    // psuDTO.setPowerSupply(0);

    // List<PSU> availablePSUs = psuService.getAllPSUs(psuDTO.getName(),
    // psuDTO.getManufacturerName(),
    // psuDTO.getType(), psuDTO.getPowerSupply());
    // model.addAttribute("availablePSUs", availablePSUs);
    // model.addAttribute("type", "PSU");

    // System.out.println(" - " + psuDTO.getPowerSupply());
    // System.out.println(availablePSUs.size());

    // return "availability";
    // }

}
