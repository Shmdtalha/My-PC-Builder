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
import com.projects.mypcb.dto.components.MotherboardDTO;
import com.projects.mypcb.dto.components.PSUDTO;
import com.projects.mypcb.dto.components.RAMDTO;
import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.ComponentType;
import com.projects.mypcb.entity.Order;
import com.projects.mypcb.entity.User;
import com.projects.mypcb.entity.components.CPU;
import com.projects.mypcb.entity.components.Motherboard;
import com.projects.mypcb.entity.components.PSU;
import com.projects.mypcb.entity.components.RAM;
import com.projects.mypcb.global.OrderStatus;
import com.projects.mypcb.repository.OrderRepository;
import com.projects.mypcb.repository.components.CPURepository;
import com.projects.mypcb.service.ComponentService;
import com.projects.mypcb.service.ComponentTypeService;
import com.projects.mypcb.service.OrderService;
import com.projects.mypcb.service.UserService;
import com.projects.mypcb.service.components.CPUService;
import com.projects.mypcb.service.components.MotherboardService;
import com.projects.mypcb.service.components.PSUService;
import com.projects.mypcb.service.components.RAMService;

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
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication
                .getPrincipal();

        // Retrieve the custom User entity from the database using the username
        User user = userService.findUserByEmail(userDetails.getUsername());

        model.addAttribute("user", user);
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
    public String postCompTypeAdd(@ModelAttribute("componentType") ComponentType componentType) {
        componentTypeService.addComponentTypes(componentType);
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

        // model.addAttribute("componentTypes",
        // componentTypeService.getAllComponentTypes());
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
            String componentName = data[0].toString();
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
            String componentName = data[0].toString();
            Number inventoryVolume = (Number) data[4];
            dataset.addValue(inventoryVolume.intValue(), "Inventory Volume", componentName);
        }

        // Create a bar chart using the dataset
        JFreeChart chart = ChartFactory.createBarChart("Least Popular Components", "Component Name", "Inventory Volume (Quantity Available * Price)",
                dataset);

        // Customize chart appearance
        chart.setBackgroundPaint(Color.white);

        // Customize plot appearance
        CategoryPlot plot = chart.getCategoryPlot();
        Color lightGray = new Color(230, 230, 230); // Lighter gray color for background
        plot.setBackgroundPaint(lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        CategoryAxis domainAxis = plot.getDomainAxis();
domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 32.0));

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
