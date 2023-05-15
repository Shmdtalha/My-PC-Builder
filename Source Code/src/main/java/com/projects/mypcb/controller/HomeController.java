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

import com.projects.mypcb.dto.components.BuildCaseDTO;
import com.projects.mypcb.dto.components.CPUDTO;
import com.projects.mypcb.dto.components.CoolerDTO;
import com.projects.mypcb.dto.components.GPUDTO;
import com.projects.mypcb.dto.components.MotherboardDTO;
import com.projects.mypcb.dto.components.PSUDTO;
import com.projects.mypcb.dto.components.RAMDTO;
import com.projects.mypcb.dto.components.StorageDTO;
import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.BuildCase;
import com.projects.mypcb.entity.components.CPU;
import com.projects.mypcb.entity.components.Cooler;
import com.projects.mypcb.entity.components.GPU;
import com.projects.mypcb.entity.components.Motherboard;
import com.projects.mypcb.entity.components.PSU;
import com.projects.mypcb.entity.components.RAM;
import com.projects.mypcb.entity.components.Storage;
import com.projects.mypcb.global.ComputerBuild;
import com.projects.mypcb.service.ComponentComparer;
import com.projects.mypcb.service.ComponentService;
import com.projects.mypcb.service.ComponentTypeService;
import com.projects.mypcb.service.components.BuildCaseService;
import com.projects.mypcb.service.components.CPUService;
import com.projects.mypcb.service.components.CoolerService;
import com.projects.mypcb.service.components.GPUService;
import com.projects.mypcb.service.components.MotherboardService;
import com.projects.mypcb.service.components.PSUService;
import com.projects.mypcb.service.components.RAMService;
import com.projects.mypcb.service.components.StorageService;

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

    @Autowired
    StorageService storageService;

    @Autowired
    GPUService gpuService;

    @Autowired
    CoolerService coolerService;

    @Autowired
    BuildCaseService buildCaseService;

    @GetMapping("/error")
    public String errorGet() {
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
        if (name.equals("Storage"))
            model.addAttribute("component", storageService.getStorageById(id).get());
        if (name.equals("GPU"))
            model.addAttribute("component", gpuService.getGPUById(id).get());
 if (name.equals("Cooler"))
            model.addAttribute("component", coolerService.getCoolerById(id).get());
        if (name.equals("BuildCase"))
            model.addAttribute("component", buildCaseService.getBuildCaseById(id).get());

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
        if (name.equals("Storage"))
            ComputerBuild.storages.remove(index);
        if (name.equals("GPU"))
            ComputerBuild.gpus.remove(index);
        if (name.equals("Cooler"))
            ComputerBuild.coolers.remove(index);
        if (name.equals("BuildCase"))
            ComputerBuild.buildCases.remove(index);

        return "redirect:/build";

    }

    private String idToName(int id) {
        return componentService.getComponentById(id).get().getComponentType().getName();
    }

    private String idToType(int id) {
        return componentTypeService.getComponentTypeById(id).get().getName();
    }

    @GetMapping("/shop/compare/{type}")
    public String comparisonGet(@PathVariable String type, Model model) {
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
        if (type.equals("Storage")) {
            model.addAttribute("storageDTO", new StorageDTO());
        }
        if (type.equals("GPU")) {
            model.addAttribute("gpuDTO", new GPUDTO());
        }
        if (type.equals("Cooler")) {
            model.addAttribute("coolerDTO", new CoolerDTO());
        }
        if (type.equals("BuildCase")) {
            model.addAttribute("buildCaseDTO", new BuildCaseDTO());
        }


        return "compare";
    }

    @PostMapping("/shop/compare/RAM")
    public String compareCheckPost(@ModelAttribute("ramDTO") RAMDTO ramDTO, Model model) throws IOException {
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


    @PostMapping("/shop/compare/CPU")
    public String compareCheckPost(@ModelAttribute("cpuDTO") CPUDTO cpuDTO, Model model) throws IOException {
        if (cpuDTO.getSeries() == null)
            cpuDTO.setSeries("");
        if (cpuDTO.getGeneration() == null)
            cpuDTO.setGeneration(0);
        if (cpuDTO.getModel() == null)
            cpuDTO.setModel("");
        if (cpuDTO.getCores() == null)
            cpuDTO.setCores(0);
        if (cpuDTO.getCoreSpeed() == null)
            cpuDTO.setCoreSpeed(0.0);

        List<CPU> availableCPUs = cpuService.getAllCPUs(cpuDTO.getName(), cpuDTO.getManufacturerName(),
                cpuDTO.getSeries(), cpuDTO.getGeneration(), cpuDTO.getModel(), cpuDTO.getCores(),
                cpuDTO.getCoreSpeed());
        model.addAttribute("availableCPUs", availableCPUs);
        model.addAttribute("type", "CPU");

        System.out.println(cpuDTO.getSeries() + " " + cpuDTO.getGeneration() + " " + cpuDTO.getModel() + " "
                + cpuDTO.getCores() + " " + cpuDTO.getCoreSpeed());
        System.out.println(availableCPUs.size());

        return "compare";
    }

    @PostMapping("/shop/compare/PSU")
    public String compareCheckPost(@ModelAttribute("psuDTO") PSUDTO psuDTO, Model model) throws IOException {
        if (psuDTO.getType() == null)
            psuDTO.setType("");
        if (psuDTO.getPowerSupply() == null)
            psuDTO.setPowerSupply(0);

        List<PSU> availablePSUs = psuService.getAllPSUs(psuDTO.getName(), psuDTO.getManufacturerName(),
                psuDTO.getType(), psuDTO.getPowerSupply());
        model.addAttribute("availablePSUs", availablePSUs);
        model.addAttribute("type", "PSU");

        System.out.println("  - " + psuDTO.getPowerSupply());
        System.out.println(availablePSUs.size());

        return "compare";
    }

    @PostMapping("/shop/compare/Motherboard")
    public String compareCheckPost(@ModelAttribute("motherboardDTO") MotherboardDTO motherboardDTO, Model model)
            throws IOException {
        if (motherboardDTO.getFormFactor() == null)
            motherboardDTO.setFormFactor("");
        if (motherboardDTO.getSocket() == null)
            motherboardDTO.setSocket("");
        if (motherboardDTO.getMaxMemory() == null)
            motherboardDTO.setMaxMemory(0);
        if (motherboardDTO.getMemorySlots() == null)
            motherboardDTO.setMemorySlots(0);

        List<Motherboard> availableMotherboards = motherboardService.getAllMotherboards(motherboardDTO.getName(),
                motherboardDTO.getManufacturerName(), motherboardDTO.getSocket(),
                motherboardDTO.getFormFactor(), motherboardDTO.getMaxMemory(), motherboardDTO.getMemorySlots());
        model.addAttribute("availableMotherboards", availableMotherboards);
        model.addAttribute("type", "Motherboard");

        System.out.println(availableMotherboards.size());

        return "compare";
    }

    @PostMapping("/shop/compare/Storage")
    public String compareCheckPost(@ModelAttribute("storageDTO") StorageDTO storageDTO, Model model)
            throws IOException {
        if (storageDTO.getCapacity() == null)
            storageDTO.setCapacity(0);
        if (storageDTO.getCache() == null)
            storageDTO.setCache(0);
        if (storageDTO.getType() == null)
            storageDTO.setType("");

        List<Storage> availableStorages = storageService.getAllStorages(storageDTO.getName(),
                storageDTO.getManufacturerName(), storageDTO.getType(), storageDTO.getCapacity(),
                storageDTO.getCache());
        model.addAttribute("availableStorages", availableStorages);
        model.addAttribute("type", "Storage");

        System.out.println(availableStorages.size());

        return "compare";
    }

    @PostMapping("/shop/compare/GPU")
    public String compareCheckPost(@ModelAttribute("gpuDTO") GPUDTO gpuDTO, Model model)
            throws IOException {
        if (gpuDTO.getChipset() == null)
            gpuDTO.setChipset("");
        if (gpuDTO.getMemory() == null)
            gpuDTO.setMemory(0);
        if (gpuDTO.getCoreSpeed() == null)
            gpuDTO.setCoreSpeed(0);

        List<GPU> availableGPUs = gpuService.getAllGPUs(gpuDTO.getName(),
                gpuDTO.getManufacturerName(), gpuDTO.getChipset(), gpuDTO.getMemory(),
                gpuDTO.getCoreSpeed());
        model.addAttribute("availableGPUs", availableGPUs);
        model.addAttribute("type", "GPU");

        System.out.println(availableGPUs.size());

        return "compare";
    }


    @PostMapping("/shop/compare/Cooler")
    public String availabilityCheckPost(@ModelAttribute("coolerDTO") CoolerDTO coolerDTO, Model model)
            throws IOException {
        if (coolerDTO.getFanRPM() == null)
            coolerDTO.setFanRPM(0.0);
        if (coolerDTO.getMinNoiseLevel() == null)
            coolerDTO.setMinNoiseLevel(0.0);
        if (coolerDTO.getMaxNoiseLevel() == null)
            coolerDTO.setMaxNoiseLevel(0.0);
    
        List<Cooler> availableCoolers = coolerService.getAllCoolers(coolerDTO.getName(),
                coolerDTO.getManufacturerName(), coolerDTO.getFanRPM(), coolerDTO.getMinNoiseLevel(),
                coolerDTO.getMaxNoiseLevel());
        model.addAttribute("availableCoolers", availableCoolers);
        model.addAttribute("type", "Cooler");
    
        System.out.println(availableCoolers.size());
    
        return "compare";
    }
    
@PostMapping("/shop/compare/BuildCase")
public String availabilityCheckPost(@ModelAttribute("buildCaseDTO") BuildCaseDTO buildCaseDTO, Model model)
        throws IOException {
    if (buildCaseDTO.getType() == null)
        buildCaseDTO.setType("");
    if (buildCaseDTO.getPanel() == null)
        buildCaseDTO.setPanel("");
    if (buildCaseDTO.getColour() == null)
        buildCaseDTO.setColour("");

    List<BuildCase> availableBuildCases = buildCaseService.getAllBuildCases(buildCaseDTO.getName(),
            buildCaseDTO.getManufacturerName(), buildCaseDTO.getType(), buildCaseDTO.getPanel(),
            buildCaseDTO.getColour());
    model.addAttribute("availableBuildCases", availableBuildCases);
    model.addAttribute("type", "BuildCase");

    System.out.println(availableBuildCases.size());

    return "compare";
}



    @PostMapping("/shop/compare/submit")
    public String compareSelectedComponents(HttpServletRequest request, Model model,
            RedirectAttributes redirectAttributes) {
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
        model.addAttribute("componentType", selectedComponents.get(0).getComponentType().getName());

        // Redirect to the desired page or process the comparison here
        // ...

        return "compareResult";

    }

}
