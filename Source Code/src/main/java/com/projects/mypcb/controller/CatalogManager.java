package com.projects.mypcb.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.projects.mypcb.dto.ComponentDTO;
import com.projects.mypcb.dto.components.CPUDTO;
import com.projects.mypcb.dto.components.BuildCaseDTO;
import com.projects.mypcb.dto.components.CoolerDTO;
import com.projects.mypcb.dto.components.GPUDTO;
import com.projects.mypcb.dto.components.MotherboardDTO;
import com.projects.mypcb.dto.components.PSUDTO;
import com.projects.mypcb.dto.components.RAMDTO;
import com.projects.mypcb.dto.components.StorageDTO;
import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.ComponentType;
import com.projects.mypcb.entity.Order;
import com.projects.mypcb.entity.User;
import com.projects.mypcb.entity.components.CPU;
import com.projects.mypcb.entity.components.Cooler;
import com.projects.mypcb.entity.components.BuildCase;
import com.projects.mypcb.entity.components.GPU;
import com.projects.mypcb.entity.components.Motherboard;
import com.projects.mypcb.entity.components.PSU;
import com.projects.mypcb.entity.components.RAM;
import com.projects.mypcb.entity.components.Storage;
import com.projects.mypcb.global.OrderStatus;
import com.projects.mypcb.repository.OrderRepository;
import com.projects.mypcb.repository.components.CPURepository;
import com.projects.mypcb.service.ComponentService;
import com.projects.mypcb.service.ComponentTypeService;
import com.projects.mypcb.service.OrderService;
import com.projects.mypcb.service.UserService;
import com.projects.mypcb.service.components.CPUService;
import com.projects.mypcb.service.components.CoolerService;
import com.projects.mypcb.service.components.BuildCaseService;
import com.projects.mypcb.service.components.GPUService;
import com.projects.mypcb.service.components.MotherboardService;
import com.projects.mypcb.service.components.PSUService;
import com.projects.mypcb.service.components.RAMService;
import com.projects.mypcb.service.components.StorageService;

import jakarta.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class CatalogManager {
    @Autowired
    ComponentTypeService componentTypeService;

    @Autowired
    ComponentService componentService;

    @Autowired
    RAMService ramService;

    @Autowired
    UserService userService;

    @GetMapping("/manager")
    public String managerHome(Model model, Authentication authentication) {      
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "managerHome";
    }

    @GetMapping("/manager/componentTypes")
    public String getCompType(Model model) {
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentTypes";
    }

    @GetMapping("/manager/componentTypes/add")
    public String getCompTypeAdd(Model model) {
        model.addAttribute("componentType", new ComponentType());
        return "componentTypesAdd";
    }

    @PostMapping("/manager/componentTypes/add")
    public String postCompTypeAdd(@ModelAttribute("componentType") ComponentType componentType, Model error) {
        if(componentTypeService.addComponentTypes(componentType) == false)
            error.addAttribute("error", "Invalid type of component!");

         return "redirect:/manager/componentTypes";
    }

    @GetMapping("/manager/componentTypes/delete/{id}")
    public String deleteCompType(@PathVariable int id) {
        componentTypeService.deleteComponentTypeByID(id);
        return "redirect:/manager/componentTypes";
    }

    @GetMapping("/manager/componentTypes/update/{id}")
    public String updateCompType(@PathVariable int id, Model model) {
        Optional<ComponentType> componentType = componentTypeService.getComponentTypeById(id);
        if (componentType.isPresent()) {
            model.addAttribute("componentType", componentType.get());
            return "componentTypesAdd";
        }

        return "404";

    }

    // Component Section
    @GetMapping("/manager/components")
    public String componentSelector(Model model) {
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentSelector";
    }

    @GetMapping("/manager/components/RAM")
    public String RAMs(Model model) {
        model.addAttribute("RAMs", ramService.getAllRAMs(""));
        return "componentPages/RAMs";
    }

    @GetMapping("/manager/components/RAM/add")
    public String RAMAddGet(Model model) {
        model.addAttribute("RAMDTO", new RAMDTO());
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentPages/RAMsAdd";
    }

    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/componentImages";

    // public static String uploadDir = System.getProperty("user.dir")+
    // "/componentImages";
    @PostMapping("/manager/components/RAM/add")
    public String componentAddPost(
            @ModelAttribute("RAMDTO") RAMDTO componentDTO,
            @RequestParam("componentImage") MultipartFile file,
            @RequestParam("imgName") String imgName)
            throws IOException {
        RAM component = new RAM();
        component.setId(componentDTO.getId());
        component.setName(componentDTO.getName());
        component.setManufacturerName(componentDTO.getManufacturerName());
        component.setPrice(componentDTO.getPrice());
        component.setWattageConsumption(componentDTO.getWattageConsumption());
        component.setComponentType(componentTypeService.getComponentTypeByName("RAM"));
        component.setSpeed(componentDTO.getSpeed());
        component.setMemory(componentDTO.getMemory());
        component.setQuantity(componentDTO.getQuantity());
        component.setOfficialSiteLink(componentDTO.getOfficialSiteLink());
        String imageUUID;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }

        component.setImageName(imageUUID);

        ramService.addRAM(component);
        return "redirect:/manager/components/RAM";
    }

    @GetMapping("/manager/components/RAM/delete/{id}")
    public String deleteRAM(@PathVariable long id) {
        ramService.removeRAM(id);
        return "redirect:/manager/components/RAM";
    }

    @GetMapping("/manager/components/RAM/update/{id}")
    public String updateRAMGet(@PathVariable long id, Model model) {

        RAM component = ramService.getRAMById(id).get();
        RAMDTO componentDTO = new RAMDTO();

        componentDTO.setId(component.getId());
        componentDTO.setName(component.getName());
        componentDTO.setComponentTypeId(component.getComponentType().getId());
        componentDTO.setPrice(component.getPrice());
        componentDTO.setManufacturerName(component.getManufacturerName());
        componentDTO.setWattageConsumption(component.getWattageConsumption());
        componentDTO.setSpeed(component.getSpeed());
        componentDTO.setMemory(component.getMemory());
        componentDTO.setImageName(component.getImageName());
        componentDTO.setQuantity(component.getQuantity());
        componentDTO.setOfficialSiteLink(component.getOfficialSiteLink());

        // model.addAttribute("componentTypes",
        // componentTypeService.getAllComponentTypes());
        model.addAttribute("RAMDTO", componentDTO);

        return "componentpages/RAMsAdd";

    }

    // CPURepository cpuRepository;

    @Autowired
    CPUService cpuService;

    @GetMapping("/manager/components/CPU")
    public String CPUs(Model model) {
        model.addAttribute("CPUs", cpuService.getAllCPUs(""));
        return "componentPages/CPUs";
    }

    @GetMapping("/manager/components/CPU/add")
    public String CPUAddGet(Model model) {
        model.addAttribute("CPUDTO", new CPUDTO());
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentPages/CPUsAdd";
    }

    // public static String uploadDir = System.getProperty("user.dir")+
    // "/componentImages";
    @PostMapping("/manager/components/CPU/add")
    public String componentAddPost(
            @ModelAttribute("CPUDTO") CPUDTO cpuDTO,
            @RequestParam("componentImage") MultipartFile file,
            @RequestParam("imgName") String imgName)
            throws IOException {
        CPU cpu = new CPU();
        cpu.setId(cpuDTO.getId());
        cpu.setName(cpuDTO.getName());
        cpu.setManufacturerName(cpuDTO.getManufacturerName());
        cpu.setPrice(cpuDTO.getPrice());
        cpu.setWattageConsumption(cpuDTO.getWattageConsumption());
        cpu.setComponentType(componentTypeService.getComponentTypeByName("CPU"));
        cpu.setQuantity(cpuDTO.getQuantity());
        cpu.setOfficialSiteLink(cpuDTO.getOfficialSiteLink());

        cpu.setSeries(cpuDTO.getSeries());
        cpu.setGeneration(cpuDTO.getGeneration());
        cpu.setModel(cpuDTO.getModel());
        cpu.setCoreSpeed(cpuDTO.getCoreSpeed());
        cpu.setCores(cpuDTO.getCores());

        String imageUUID;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }

        cpu.setImageName(imageUUID);

        cpuService.addCPU(cpu);
        return "redirect:/manager/components/CPU";
    }

    @GetMapping("/manager/components/CPU/delete/{id}")
    public String deleteCPU(@PathVariable long id) {
        cpuService.removeCPU(id);
        return "redirect:/manager/components/CPU";
    }

    @GetMapping("/manager/components/CPU/update/{id}")
    public String updateCPUGet(@PathVariable long id, Model model) {

        CPU cpu = cpuService.getCPUById(id).get();
        CPUDTO cpuDTO = new CPUDTO();

        cpuDTO.setId(cpu.getId());
        cpuDTO.setName(cpu.getName());
        cpuDTO.setComponentTypeId(cpu.getComponentType().getId());
        cpuDTO.setPrice(cpu.getPrice());
        cpuDTO.setManufacturerName(cpu.getManufacturerName());
        cpuDTO.setWattageConsumption(cpu.getWattageConsumption());
        cpuDTO.setImageName(cpu.getImageName());
        cpuDTO.setQuantity(cpu.getQuantity());
        cpuDTO.setOfficialSiteLink(cpu.getOfficialSiteLink());

        cpuDTO.setSeries(cpu.getSeries());
        cpuDTO.setGeneration(cpu.getGeneration());
        cpuDTO.setModel(cpu.getModel());
        cpuDTO.setCoreSpeed(cpu.getCoreSpeed());
        cpuDTO.setCores(cpu.getCores());

        model.addAttribute("CPUDTO", cpuDTO);

        return "componentpages/CPUsAdd";

    }

    // PSU!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Autowired
    PSUService psuService;

    @GetMapping("/manager/components/PSU")
    public String PSUs(Model model) {
        model.addAttribute("PSUs", psuService.getAllPSUs(""));
        return "componentPages/PSUs";
    }

    @GetMapping("/manager/components/PSU/add")
    public String PSUAddGet(Model model) {
        model.addAttribute("PSUDTO", new PSUDTO());
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentPages/PSUsAdd";
    }

    @PostMapping("/manager/components/PSU/add")
    public String componentAddPost(
            @ModelAttribute("PSUDTO") PSUDTO psuDTO,
            @RequestParam("componentImage") MultipartFile file,
            @RequestParam("imgName") String imgName)
            throws IOException {
        PSU psu = new PSU();
        psu.setId(psuDTO.getId());
        psu.setName(psuDTO.getName());
        psu.setManufacturerName(psuDTO.getManufacturerName());
        psu.setPrice(psuDTO.getPrice());
        psu.setWattageConsumption(-psuDTO.getPowerSupply());
        psu.setComponentType(componentTypeService.getComponentTypeByName("PSU"));
        psu.setQuantity(psuDTO.getQuantity());
        psu.setOfficialSiteLink(psuDTO.getOfficialSiteLink());

        psu.setType(psuDTO.getType());
        psu.setPowerSupply(psuDTO.getPowerSupply());

        String imageUUID;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }

        psu.setImageName(imageUUID);

        psuService.addPSU(psu);
        return "redirect:/manager/components/PSU";
    }

    @GetMapping("/manager/components/PSU/delete/{id}")
    public String deletePSU(@PathVariable long id) {
        psuService.removePSU(id);
        return "redirect:/manager/components/PSU";
    }

    @GetMapping("/manager/components/PSU/update/{id}")
    public String updatePSUGet(@PathVariable long id, Model model) {

        PSU psu = psuService.getPSUById(id).get();
        PSUDTO psuDTO = new PSUDTO();

        psuDTO.setId(psu.getId());
        psuDTO.setName(psu.getName());
        psuDTO.setComponentTypeId(psu.getComponentType().getId());
        psuDTO.setPrice(psu.getPrice());
        psuDTO.setManufacturerName(psu.getManufacturerName());
        psuDTO.setWattageConsumption(psu.getWattageConsumption());
        psuDTO.setImageName(psu.getImageName());
        psuDTO.setQuantity(psu.getQuantity());
        psuDTO.setOfficialSiteLink(psu.getOfficialSiteLink());

        psuDTO.setType(psu.getType());
        psuDTO.setPowerSupply(psu.getPowerSupply());

        model.addAttribute("PSUDTO", psuDTO);

        return "componentpages/PSUsAdd";
    }

    // MOThERBAORDS!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Autowired
    MotherboardService motherboardService;

    @GetMapping("/manager/components/Motherboard")
    public String motherboards(Model model) {
        model.addAttribute("motherboards", motherboardService.getAllMotherboards(""));
        return "componentPages/Motherboards";
    }

    @GetMapping("/manager/components/Motherboard/add")
    public String motherboardAddGet(Model model) {
        model.addAttribute("motherboardDTO", new MotherboardDTO());
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentPages/MotherboardsAdd";
    }

    @PostMapping("/manager/components/Motherboard/add")
    public String componentAddPost(
            @ModelAttribute("motherboardDTO") MotherboardDTO componentDTO,
            @RequestParam("componentImage") MultipartFile file,
            @RequestParam("imgName") String imgName)
            throws IOException {
        Motherboard component = new Motherboard();
        component.setId(componentDTO.getId());
        component.setName(componentDTO.getName());
        component.setManufacturerName(componentDTO.getManufacturerName());
        component.setPrice(componentDTO.getPrice());
        component.setWattageConsumption(componentDTO.getWattageConsumption());
        component.setComponentType(componentTypeService.getComponentTypeByName("Motherboard"));
        component.setSocket(componentDTO.getSocket());
        component.setMaxMemory(componentDTO.getMaxMemory());
        component.setMemorySlots(componentDTO.getMemorySlots());
        component.setFormFactor(componentDTO.getFormFactor());
        component.setQuantity(componentDTO.getQuantity());
        component.setOfficialSiteLink(componentDTO.getOfficialSiteLink());
        String imageUUID;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }

        component.setImageName(imageUUID);

        motherboardService.addMotherboard(component);
        return "redirect:/manager/components/Motherboard";
    }

    @GetMapping("/manager/components/Motherboard/delete/{id}")
    public String deleteMotherboard(@PathVariable long id) {
        motherboardService.removeMotherboard(id);
        return "redirect:/manager/components/Motherboard";
    }

    @GetMapping("/manager/components/Motherboard/update/{id}")
    public String updateMotherboardGet(@PathVariable long id, Model model) {

        Motherboard component = motherboardService.getMotherboardById(id).get();
        MotherboardDTO componentDTO = new MotherboardDTO();

        componentDTO.setId(component.getId());
        componentDTO.setName(component.getName());
        componentDTO.setComponentTypeId(component.getComponentType().getId());
        componentDTO.setPrice(component.getPrice());
        componentDTO.setManufacturerName(component.getManufacturerName());
        componentDTO.setWattageConsumption(component.getWattageConsumption());
        componentDTO.setMaxMemory(component.getMaxMemory());
        componentDTO.setSocket(component.getSocket());
        componentDTO.setMemorySlots(component.getMemorySlots());
        componentDTO.setFormFactor(component.getFormFactor());
        componentDTO.setImageName(component.getImageName());
        componentDTO.setQuantity(component.getQuantity());
        componentDTO.setOfficialSiteLink(component.getOfficialSiteLink());

        model.addAttribute("motherboardDTO", componentDTO);

        return "componentpages/MotherboardsAdd";

    }

    // Storage!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Autowired
    StorageService storageService;

    @GetMapping("/manager/components/Storage")
    public String Storages(Model model) {
        model.addAttribute("Storages", storageService.getAllStorages(""));
        return "componentPages/Storages";
    }

    @GetMapping("/manager/components/Storage/add")
    public String StorageAddGet(Model model) {
        model.addAttribute("StorageDTO", new StorageDTO());
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentPages/StoragesAdd";
    }

    @PostMapping("/manager/components/Storage/add")
    public String componentAddPost(
            @ModelAttribute("StorageDTO") StorageDTO storageDTO,
            @RequestParam("componentImage") MultipartFile file,
            @RequestParam("imgName") String imgName)
            throws IOException {
        Storage storage = new Storage();
        storage.setId(storageDTO.getId());
        storage.setName(storageDTO.getName());
        storage.setManufacturerName(storageDTO.getManufacturerName());
        storage.setPrice(storageDTO.getPrice());
        storage.setWattageConsumption(storageDTO.getWattageConsumption());
        storage.setComponentType(componentTypeService.getComponentTypeByName("Storage"));
        storage.setQuantity(storageDTO.getQuantity());
        storage.setOfficialSiteLink(storageDTO.getOfficialSiteLink());

        storage.setCapacity(storageDTO.getCapacity());
        storage.setCache(storageDTO.getCache());
        storage.setType(storageDTO.getType());

        String imageUUID;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }

        storage.setImageName(imageUUID);

        storageService.addStorage(storage);
        return "redirect:/manager/components/Storage";
    }

    @GetMapping("/manager/components/Storage/delete/{id}")
    public String deleteStorage(@PathVariable long id) {
        storageService.removeStorage(id);
        return "redirect:/manager/components/Storage";
    }

    @GetMapping("/manager/components/Storage/update/{id}")
    public String updateStorageGet(@PathVariable long id, Model model) {

        Storage storage = storageService.getStorageById(id).get();
        StorageDTO storageDTO = new StorageDTO();

        storageDTO.setId(storage.getId());
        storageDTO.setName(storage.getName());
        storageDTO.setComponentTypeId(storage.getComponentType().getId());
        storageDTO.setPrice(storage.getPrice());
        storageDTO.setManufacturerName(storage.getManufacturerName());
        storageDTO.setWattageConsumption(storage.getWattageConsumption());
        storageDTO.setImageName(storage.getImageName());
        storageDTO.setQuantity(storage.getQuantity());
        storageDTO.setOfficialSiteLink(storage.getOfficialSiteLink());

        storageDTO.setCapacity(storage.getCapacity());
        storageDTO.setCache(storage.getCache());
        storageDTO.setType(storage.getType());

        model.addAttribute("StorageDTO", storageDTO);

        return "componentpages/StoragesAdd";

    }

    // GPU!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Autowired
    GPUService gpuService;

    @GetMapping("/manager/components/GPU")
    public String GPUs(Model model) {
        model.addAttribute("GPUs", gpuService.getAllGPUs(""));
        return "componentPages/GPUs";
    }

    @GetMapping("/manager/components/GPU/add")
    public String GPUAddGet(Model model) {
        model.addAttribute("GPUDTO", new GPUDTO());
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentPages/GPUsAdd";
    }

    @PostMapping("/manager/components/GPU/add")
    public String componentAddPost(
            @ModelAttribute("GPUDTO") GPUDTO gpuDTO,
            @RequestParam("componentImage") MultipartFile file,
            @RequestParam("imgName") String imgName)
            throws IOException {
        GPU gpu = new GPU();
        gpu.setId(gpuDTO.getId());
        gpu.setName(gpuDTO.getName());
        gpu.setManufacturerName(gpuDTO.getManufacturerName());
        gpu.setPrice(gpuDTO.getPrice());
        gpu.setWattageConsumption(gpuDTO.getWattageConsumption());
        gpu.setComponentType(componentTypeService.getComponentTypeByName("GPU"));
        gpu.setQuantity(gpuDTO.getQuantity());
        gpu.setOfficialSiteLink(gpuDTO.getOfficialSiteLink());
        gpu.setUtilitySiteLink(gpuDTO.getUtilitySiteLink());

        gpu.setChipset(gpuDTO.getChipset());
        gpu.setMemory(gpuDTO.getMemory());
        gpu.setCoreSpeed(gpuDTO.getCoreSpeed());

        String imageUUID;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }

        gpu.setImageName(imageUUID);

        gpuService.addGPU(gpu);
        return "redirect:/manager/components/GPU";
    }

    @GetMapping("/manager/components/GPU/delete/{id}")
    public String deleteGPU(@PathVariable long id) {
        gpuService.removeGPU(id);
        return "redirect:/manager/components/GPU";
    }

    @GetMapping("/manager/components/GPU/update/{id}")
    public String updateGPUGet(@PathVariable long id, Model model) {

        GPU gpu = gpuService.getGPUById(id).get();
        GPUDTO gpuDTO = new GPUDTO();

        gpuDTO.setId(gpu.getId());
        gpuDTO.setName(gpu.getName());
        gpuDTO.setComponentTypeId(gpu.getComponentType().getId());
        gpuDTO.setPrice(gpu.getPrice());
        gpuDTO.setManufacturerName(gpu.getManufacturerName());
        gpuDTO.setWattageConsumption(gpu.getWattageConsumption());
        gpuDTO.setImageName(gpu.getImageName());
        gpuDTO.setQuantity(gpu.getQuantity());
        gpuDTO.setOfficialSiteLink(gpu.getOfficialSiteLink());
        gpuDTO.setUtilitySiteLink(gpu.getUtilitySiteLink());

        gpuDTO.setChipset(gpu.getChipset());
        gpuDTO.setMemory(gpu.getMemory());
        gpuDTO.setCoreSpeed(gpu.getCoreSpeed());

        model.addAttribute("GPUDTO", gpuDTO);

        return "componentpages/GPUsAdd";

    }






    // BuildCase!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

   
    @Autowired
BuildCaseService buildCaseService;

@GetMapping("/manager/components/BuildCase")
public String buildCases(Model model) {
    model.addAttribute("buildCases", buildCaseService.getAllBuildCases(""));
    return "componentPages/BuildCases";
}

@GetMapping("/manager/components/BuildCase/add")
public String buildCaseAddGet(Model model) {
    model.addAttribute("buildCaseDTO", new BuildCaseDTO());
    model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
    return "componentPages/BuildCasesAdd";
}

@PostMapping("/manager/components/BuildCase/add")
public String buildCaseAddPost(
        @ModelAttribute("buildCaseDTO") BuildCaseDTO buildCaseDTO,
        @RequestParam("componentImage") MultipartFile file,
        @RequestParam("imgName") String imgName)
        throws IOException {
    BuildCase buildCaseComponent = new BuildCase();
    buildCaseComponent.setId(buildCaseDTO.getId());
    buildCaseComponent.setName(buildCaseDTO.getName());
    buildCaseComponent.setComponentType(componentTypeService.getComponentTypeByName("BuildCase"));
    buildCaseComponent.setPrice(buildCaseDTO.getPrice());
    buildCaseComponent.setQuantity(buildCaseDTO.getQuantity());
    buildCaseComponent.setWattageConsumption(buildCaseDTO.getWattageConsumption());
    buildCaseComponent.setManufacturerName(buildCaseDTO.getManufacturerName());
    buildCaseComponent.setImageName(buildCaseDTO.getImageName());
    buildCaseComponent.setOfficialSiteLink(buildCaseDTO.getOfficialSiteLink());
    buildCaseComponent.setUtilitySiteLink(buildCaseDTO.getUtilitySiteLink());
    buildCaseComponent.setType(buildCaseDTO.getType());
    buildCaseComponent.setPanel(buildCaseDTO.getPanel());
    buildCaseComponent.setColour(buildCaseDTO.getColour());

    String imageUUID;
    if (!file.isEmpty()) {
        imageUUID = file.getOriginalFilename();
        Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
        Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
        Files.write(fileNameAndPath, file.getBytes());
    } else {
        imageUUID = imgName;
    }

    buildCaseComponent.setImageName(imageUUID);

    buildCaseService.addBuildCase(buildCaseComponent);
    return "redirect:/manager/components/BuildCase";
}

@GetMapping("/manager/components/BuildCase/delete/{id}")
public String deleteBuildCase(@PathVariable long id) {
    buildCaseService.removeBuildCase(id);
    return "redirect:/manager/components/BuildCase";
}

@GetMapping("/manager/components/BuildCase/update/{id}")
public String updateBuildCaseGet(@PathVariable long id, Model model) {

    BuildCase buildCaseComponent = buildCaseService.getBuildCaseById(id).get();
    BuildCaseDTO buildCaseDTO = new BuildCaseDTO();

    buildCaseDTO.setId(buildCaseComponent.getId());
    buildCaseDTO.setName(buildCaseComponent.getName());
    buildCaseDTO.setComponentTypeId(buildCaseComponent.getComponentType().getId());
    buildCaseDTO.setPrice(buildCaseComponent.getPrice());
    buildCaseDTO.setQuantity(buildCaseComponent.getQuantity());
    buildCaseDTO.setWattageConsumption(buildCaseComponent.getWattageConsumption());
    buildCaseDTO.setManufacturerName(buildCaseComponent.getManufacturerName());
    buildCaseDTO.setImageName(buildCaseComponent.getImageName());
    buildCaseDTO.setOfficialSiteLink(buildCaseComponent.getOfficialSiteLink());
    buildCaseDTO.setUtilitySiteLink(buildCaseComponent.getUtilitySiteLink());

    buildCaseDTO.setType(buildCaseComponent.getType());
    buildCaseDTO.setPanel(buildCaseComponent.getPanel());
    buildCaseDTO.setColour(buildCaseComponent.getColour());

    model.addAttribute("buildCaseDTO", buildCaseDTO);

    return "componentpages/BuildCasesAdd";
}

    
    // Cooler!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
@Autowired
CoolerService coolerService;

@GetMapping("/manager/components/Cooler")
public String Coolers(Model model) {
    model.addAttribute("Coolers", coolerService.getAllCoolers(""));
    return "componentPages/Coolers";
}

@GetMapping("/manager/components/Cooler/add")
public String CoolerAddGet(Model model) {
    model.addAttribute("CoolerDTO", new CoolerDTO());
    model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
    return "componentPages/CoolersAdd";
}

@PostMapping("/manager/components/Cooler/add")
public String componentAddPost(
        @ModelAttribute("CoolerDTO") CoolerDTO coolerDTO,
        @RequestParam("componentImage") MultipartFile file,
        @RequestParam("imgName") String imgName)
        throws IOException {
    Cooler cooler = new Cooler();
    cooler.setId(coolerDTO.getId());
    cooler.setName(coolerDTO.getName());
    cooler.setManufacturerName(coolerDTO.getManufacturerName());
    cooler.setPrice(coolerDTO.getPrice());
    cooler.setWattageConsumption(coolerDTO.getWattageConsumption());
    cooler.setComponentType(componentTypeService.getComponentTypeByName("Cooler"));
    cooler.setQuantity(coolerDTO.getQuantity());
    cooler.setOfficialSiteLink(coolerDTO.getOfficialSiteLink());

    cooler.setFanRPM(coolerDTO.getFanRPM());
    cooler.setMaxNoiseLevel(coolerDTO.getMaxNoiseLevel());
    cooler.setMinNoiseLevel(coolerDTO.getMinNoiseLevel());



    // Add any additional properties specific to the Cooler entity here

    String imageUUID;
    if (!file.isEmpty()) {
        imageUUID = file.getOriginalFilename();
        Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
        Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
        Files.write(fileNameAndPath, file.getBytes());
    } else {
        imageUUID = imgName;
    }

    cooler.setImageName(imageUUID);

    coolerService.addCooler(cooler);
    return "redirect:/manager/components/Cooler";
}

@GetMapping("/manager/components/Cooler/delete/{id}")
public String deleteCooler(@PathVariable long id) {
    coolerService.removeCooler(id);
    return "redirect:/manager/components/Cooler";
}

@GetMapping("/manager/components/Cooler/update/{id}")
public String updateCoolerGet(@PathVariable long id, Model model) {

    Cooler cooler = coolerService.getCoolerById(id).get();
    CoolerDTO coolerDTO = new CoolerDTO();

    coolerDTO.setId(cooler.getId());
    coolerDTO.setName(cooler.getName());
    coolerDTO.setComponentTypeId(cooler.getComponentType().getId());
    coolerDTO.setPrice(cooler.getPrice());
    coolerDTO.setManufacturerName(cooler.getManufacturerName());
    coolerDTO.setWattageConsumption(cooler.getWattageConsumption());
    coolerDTO.setImageName(cooler.getImageName());
    coolerDTO.setQuantity(cooler.getQuantity());
    coolerDTO.setOfficialSiteLink(cooler.getOfficialSiteLink());

    coolerDTO.setFanRPM(cooler.getFanRPM());
    coolerDTO.setMaxNoiseLevel(cooler.getMaxNoiseLevel());
    coolerDTO.setMinNoiseLevel(cooler.getMinNoiseLevel());

    // Add any additional properties specific to the CoolerDTO here

    model.addAttribute("CoolerDTO", coolerDTO);

    return "componentpages/CoolersAdd";
}










    // @GetMapping("/manager/components/add")
    // public String componentAddGet(Model model){
    // model.addAttribute("componentDTO", new ComponentDTO());
    // model.addAttribute("componentTypes",
    // componentTypeService.getAllComponentTypes());
    // return "componentsAdd";
    // }

    // public static String uploadDir = System.getProperty("user.dir")+
    // "/src/main/resources/static/componentImages";
    // //public static String uploadDir = System.getProperty("user.dir")+
    // "/componentImages";
    // @PostMapping("/manager/components/add")
    // public String componentAddPost(
    // @ModelAttribute("componentDTO")
    // ComponentDTO componentDTO,
    // @RequestParam("componentImage")
    // MultipartFile file,
    // @RequestParam("imgName")
    // String imgName)
    // throws IOException
    // {
    // Component component = new Component();
    // component.setId(componentDTO.getId());
    // component.setName(componentDTO.getName());
    // component.setManufacturerName(componentDTO.getManufacturerName());
    // component.setPrice(componentDTO.getPrice());
    // component.setWattageConsumption(componentDTO.getWattageConsumption());
    // component.setComponentType(componentTypeService.getComponentTypeById(componentDTO.getComponentTypeId()).get());

    // String imageUUID;
    // if(!file.isEmpty()){
    // imageUUID = file.getOriginalFilename();
    // Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
    // Files.createDirectories(fileNameAndPath.getParent()); // create parent
    // directories if necessary
    // Files.write(fileNameAndPath, file.getBytes());
    // } else {
    // imageUUID = imgName;
    // }

    // component.setImageName(imageUUID);

    // componentService.addComponent(component);
    // return "redirect:/manager/components";
    // }

    // @GetMapping("/manager/component/delete/{id}")
    // public String deleteComp(@PathVariable long id){
    // componentService.removeComponent(id);
    // return "redirect:/manager/components";
    // }

    // @GetMapping("/manager/component/update/{id}")
    // public String updateCompGet(@PathVariable long id, Model model){

    // Component component = componentService.getComponentById(id).get();
    // ComponentDTO componentDTO = new ComponentDTO();

    // componentDTO.setId(component.getId());
    // componentDTO.setName(component.getName());
    // componentDTO.setComponentTypeId(component.getComponentType().getId());
    // componentDTO.setPrice(component.getPrice());
    // componentDTO.setManufacturerName(component.getManufacturerName());
    // componentDTO.setWattageConsumption(component.getWattageConsumption());
    // componentDTO.setImageName(component.getImageName());

    // model.addAttribute("componentTypes",
    // componentTypeService.getAllComponentTypes());
    // model.addAttribute("componentDTO", componentDTO);

    // return "componentsAdd";

    // }

    @GetMapping("/manager/availability/{type}")
    public String availabilityCheckGet(Model model, @PathVariable String type) {
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
        if (type.equals("GPU")) {
            model.addAttribute("gpuDTO", new GPUDTO());
        }
        if (type.equals("Storage")) {
            model.addAttribute("storageDTO", new StorageDTO());
        }
        if (type.equals("BuildCase")) {
            model.addAttribute("buildCaseDTO", new BuildCaseDTO());
        }
        if (type.equals("Cooler")) {
            model.addAttribute("coolerDTO", new CoolerDTO());
        }

        model.addAttribute("type", type);
        return "availability";
    }

    @PostMapping("/manager/availability/RAM")
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

        return "availability";
    }

    @PostMapping("/manager/availability/CPU")
    public String availabilityCheckPost(@ModelAttribute("cpuDTO") CPUDTO cpuDTO, Model model) throws IOException {
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

        return "availability";
    }

    @PostMapping("/manager/availability/PSU")
    public String availabilityCheckPost(@ModelAttribute("psuDTO") PSUDTO psuDTO, Model model) throws IOException {
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

        return "availability";
    }

    @PostMapping("/manager/availability/Motherboard")
    public String availabilityCheckPost(@ModelAttribute("motherboardDTO") MotherboardDTO motherboardDTO, Model model)
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

        return "availability";
    }

    @PostMapping("/manager/availability/Storage")
    public String availabilityCheckPost(@ModelAttribute("storageDTO") StorageDTO storageDTO, Model model)
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

        return "availability";
    }

    @PostMapping("/manager/availability/GPU")
    public String availabilityCheckPost(@ModelAttribute("gpuDTO") GPUDTO gpuDTO, Model model)
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

        return "availability";
    }

    @PostMapping("/manager/availability/Cooler")
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
    
        return "availability";
    }
    
@PostMapping("/manager/availability/BuildCase")
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

    return "availability";
}


    private String idToName(int id) {
        return componentService.getComponentById(id).get().getComponentType().getName();
    }

    private String idToType(int id) {
        return componentTypeService.getComponentTypeById(id).get().getName();
    }



    
    @Autowired
    OrderService orderService;

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! MANAGE ORDERS
    @GetMapping("manager/manage-orders/{filter}")
    public String getManagerOrders(Model model, @PathVariable OrderStatus filter) {
        model.addAttribute("statuses", OrderStatus.values());

        if (filter == OrderStatus.all) {
            model.addAttribute("orders", orderService.getAllOrders(null));
        } else {
            model.addAttribute("orders", orderService.getAllOrders(filter));
        }

        // Add all ordered components to the model
        model.addAttribute("orderedComponents", orderService.getAllOrderedComponents(filter));

        return "managerOrders";
    }

    @PostMapping("/manager/manage-orders/{orderId}/change-status")
    public String changeOrderStatus(@PathVariable("orderId") Long orderId,
            @RequestParam("newStatus") OrderStatus newStatus) {
        // Retrieve the order from the database using its ID
        Order order = orderService.getOrderById(orderId).get();

        if (order != null) {
            // Update the order status and save it back to the database
            order.setOrderStatus(newStatus);
            orderService.saveOrder(order);
        }

        // Redirect back to the management page
        return "redirect:/manager/manage-orders/all";
    }

    // !!!!!!!!!!!!!!!!!!!!!! Chart !!!!!!!!!!!!!!!!!!!!!!!!!!

    @Autowired
    OrderRepository orderRepository;

    @GetMapping("admin/view-graphs")
    public String graphsGet() {

        return "statistics";
    }

    @PostMapping("admin/view-graphs")
    public String processDates(
            @RequestParam("startMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth startMonth,
            @RequestParam("endMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth endMonth,
            Model model, HttpSession session) {

        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = endMonth.atEndOfMonth();
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        if (startMonth.isAfter(endMonth)) {
            model.addAttribute("errorMessage", "Start date must be before end date.");
            return "statistics";
        }

        List<Object[]> revenueList = orderRepository.findTotalRevenueByMonthBetweenDates(startDate, endDate);
        for (Object[] ob : revenueList) {
            System.out.println("Revenue: " + ob[0] + ", Month: " + ob[1] + ", Year: " + ob[2]);
        }

        System.out.println("Most Ordered Components:");
        List<Object[]> mostPopularList = orderRepository.findMostOrderedComponentsByDateRange(startDate, endDate);
        for (Object[] ob : mostPopularList) {
            System.out.println("Component Name: " + ob[0] + ", Component Type Name: " + ob[1] + ", Component Type ID: "
                    + ob[2] + ", Quantity Sold: " + ob[3]);
        }

        System.out.println("\nLeast Ordered Components:");
        List<Object[]> leastPopularList = orderRepository.findLeastOrderedComponentsByDateRange(startDate, endDate);
        for (Object[] ob : leastPopularList) {
            System.out.println("Component Name: " + ob[0] + ", Component Type Name: " + ob[1] + ", Component Type ID: "
                    + ob[2] + ", Sales Count: " + ob[3] + ", Inventory Volume: " + ob[4]);
        }

        // Store the revenue data in the session
        session.setAttribute("revenueData", revenueList);
        session.setAttribute("mostPopularData", mostPopularList);
        session.setAttribute("leastPopularData", leastPopularList);
        
        model.addAttribute("datesSelected", true);
        model.addAttribute("mostPopularList", mostPopularList);
        model.addAttribute("leastPopularList", leastPopularList);
        
        return "statistics"; // Return the statistics page
    }

    @GetMapping("/admin/chart/revenueData")
    public ResponseEntity<byte[]> getChart(HttpSession session) {
        // Retrieve the revenue data from the session
        @SuppressWarnings("unchecked")
        List<Object[]> list = (List<Object[]>) session.getAttribute("revenueData");

        // Create a dataset using the revenue data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] data : list) {
            String month = data[1].toString() + "/" + data[2].toString();
            Number revenue = (Number) data[0];
            dataset.addValue(revenue.doubleValue(), "Revenue", month);
        }

        // Create a bar chart using the dataset
        JFreeChart chart = ChartFactory.createBarChart("Monthly Revenue Graph", "Month", "Revenue($)", dataset);

        // Customize chart appearance
        chart.setBackgroundPaint(Color.white);

        // Customize plot appearance
        CategoryPlot plot = chart.getCategoryPlot();
        Color lightGray = new Color(230, 230, 230); // Lighter gray color for background
        plot.setBackgroundPaint(lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // Customize renderer appearance
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        Color lightBlue = new Color(135, 206, 250); // Light blue color for bars
        renderer.setSeriesPaint(0, lightBlue);
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.2);

        // Remove gradient paint (white shine)
        renderer.setBarPainter(new StandardBarPainter());

        // Render chart to a BufferedImage
        BufferedImage chartImage = chart.createBufferedImage(900, 600);

        // Convert BufferedImage to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeBufferedImageAsPNG(byteArrayOutputStream, chartImage);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping("/admin/chart/mostPopularData")
    public ResponseEntity<byte[]> getPopularData(HttpSession session) {
        // Retrieve the revenue data from the session
        @SuppressWarnings("unchecked")
        List<Object[]> mostPopularlist = (List<Object[]>) session.getAttribute("mostPopularData");

        // Create a dataset using the revenue data

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] data : mostPopularlist) {
            String componentName = data[1].toString();
            Integer quantitySold = (Integer) data[3];
            dataset.addValue(quantitySold.intValue(), "Quantity Sold", componentName);
        }

        // Create a bar chart using the dataset
        JFreeChart chart = ChartFactory.createBarChart("Most Popular Components", "Component Type", "Quantity Sold",
                dataset);

        // Customize chart appearance
        chart.setBackgroundPaint(Color.white);

        // Customize plot appearance
        CategoryPlot plot = chart.getCategoryPlot();
        Color lightGray = new Color(230, 230, 230); // Lighter gray color for background
        plot.setBackgroundPaint(lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // Customize renderer appearance
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        Color lightBlue = new Color(235, 150, 150); // Light blue color for bars
        renderer.setSeriesPaint(0, lightBlue);
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.2);

        // Remove gradient paint (white shine)
        renderer.setBarPainter(new StandardBarPainter());

        // Enable tooltips for the chart

        renderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator());

        // Render chart to a BufferedImage
        BufferedImage chartImage = chart.createBufferedImage(900, 600);

        // Convert BufferedImage to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ChartUtils.writeBufferedImageAsPNG(byteArrayOutputStream, chartImage);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping("/admin/chart/leastPopularData")
    public ResponseEntity<byte[]> getLEastData(HttpSession session) {
        // Retrieve the revenue data from the session
        @SuppressWarnings("unchecked")
        List<Object[]> leastPopularlist = (List<Object[]>) session.getAttribute("leastPopularData");

        // Create a dataset using the revenue data

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] data : leastPopularlist) {
            String componentName = data[1].toString();
            Number inventoryVolume = (Number) data[4];
            dataset.addValue(inventoryVolume.intValue(), "Inventory Volume", componentName);
        }

        // Create a bar chart using the dataset
        JFreeChart chart = ChartFactory.createBarChart("Least Popular Components", "Component Name",
                "Inventory Volume (Quantity Available * Price)",
                dataset);

        // Customize chart appearance
        chart.setBackgroundPaint(Color.white);

        // Customize plot appearance
        CategoryPlot plot = chart.getCategoryPlot();
        Color lightGray = new Color(230, 230, 230); // Lighter gray color for background
        plot.setBackgroundPaint(lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
       
        // Customize renderer appearance
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        Color lightBlue = new Color(235, 206, 150); // Light blue color for bars
        renderer.setSeriesPaint(0, lightBlue);
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.2);

        // Remove gradient paint (white shine)
        renderer.setBarPainter(new StandardBarPainter());

        // Enable tooltips for the chart

        renderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator());

        // Render chart to a BufferedImage
        BufferedImage chartImage = chart.createBufferedImage(900, 600);

        // Convert BufferedImage to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ChartUtils.writeBufferedImageAsPNG(byteArrayOutputStream, chartImage);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}
