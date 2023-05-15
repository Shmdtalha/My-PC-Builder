package com.projects.mypcb.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.projects.mypcb.dto.OrderDTO;
import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.Order;
import com.projects.mypcb.entity.OrderedComponent;
import com.projects.mypcb.global.ComputerBuild;
import com.projects.mypcb.repository.ComponentRepository;
import com.projects.mypcb.repository.OrderRepository;
import com.projects.mypcb.repository.OrderedComponentRepository;
import com.projects.mypcb.service.ComponentService;
import com.projects.mypcb.service.PdfService;
import com.projects.mypcb.service.components.CPUService;
import com.projects.mypcb.service.components.BuildCaseService;
import com.projects.mypcb.service.components.CoolerService;
import com.projects.mypcb.service.components.GPUService;
import com.projects.mypcb.service.components.MotherboardService;
import com.projects.mypcb.service.components.PSUService;
import com.projects.mypcb.service.components.RAMService;
import com.projects.mypcb.service.components.StorageService;

@Controller
public class BuildController {
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
BuildCaseService buildCaseService;
@Autowired
CoolerService coolerService;



    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable int id){
        String name = idToName(id);
        if(name.equals( "RAM")){
            ComputerBuild.rams.add(ramService.getRAMById(id).get());
            System.out.println("Adding: " + ramService.getRAMById(id).get().getName());
        }
        else if(name.equals("CPU")){
            ComputerBuild.cpus.add(cpuService.getCPUById(id).get());
            System.out.println("Adding: " + cpuService.getCPUById(id).get().getName());
       
        }
        else if(name.equals("PSU")){
            ComputerBuild.psus.add(psuService.getPSUById(id).get());
            System.out.println("Adding: " + psuService.getPSUById(id).get().getName());
       
        }
        else if(name.equals("Motherboard")){
            ComputerBuild.motherboards.add(motherboardService.getMotherboardById(id).get());
            System.out.println("Adding: " + motherboardService.getMotherboardById(id).get().getName());
        }
        else if(name.equals("Storage")){
            ComputerBuild.storages.add(storageService.getStorageById(id).get());
            System.out.println("Adding: " + storageService.getStorageById(id).get().getName());
        }
        else if(name.equals("GPU")){
            ComputerBuild.gpus.add(gpuService.getGPUById(id).get());
            System.out.println("Adding: " + gpuService.getGPUById(id).get().getName());
        }
        else if (name.equals("BuildCase")) {
            ComputerBuild.buildCases.add(buildCaseService.getBuildCaseById(id).get());
            System.out.println("Adding: " + buildCaseService.getBuildCaseById(id).get().getName());
        } 
        else if (name.equals("Cooler")) {
            ComputerBuild.coolers.add(coolerService.getCoolerById(id).get());
            System.out.println("Adding: " + coolerService.getCoolerById(id).get().getName());
        }
        


        ComputerBuild.printComponents();
        
        return "redirect:/shop";
    }

    @GetMapping("/build")
    public String buildGet(Model model){
        model.addAttribute("buildCount", ComputerBuild.getCount());
        model.addAttribute("total", ComputerBuild.getTotal());
        model.addAttribute("buildRAM", ComputerBuild.rams);
        model.addAttribute("buildCPU", ComputerBuild.cpus);
        model.addAttribute("numOfCPUs", ComputerBuild.cpus.size());
        model.addAttribute("buildPSU", ComputerBuild.psus);
        model.addAttribute("numOfPSUs", ComputerBuild.psus.size());
        model.addAttribute("buildMotherboard", ComputerBuild.motherboards);
        model.addAttribute("numOfMotherboards", ComputerBuild.motherboards.size());
        model.addAttribute("buildStorage", ComputerBuild.storages);
        model.addAttribute("numOfStorages", ComputerBuild.storages.size());
        model.addAttribute("buildGPU", ComputerBuild.gpus);
        model.addAttribute("numOfGPUs", ComputerBuild.gpus.size());
        model.addAttribute("buildBuildCase", ComputerBuild.buildCases);
model.addAttribute("numOfBuildCases", ComputerBuild.buildCases.size());
model.addAttribute("buildCooler", ComputerBuild.coolers);
model.addAttribute("numOfCoolers", ComputerBuild.coolers.size());
        
        model.addAttribute("wattageConsumption", ComputerBuild.getWattageConsumption());
        model.addAttribute("powerSupply", ComputerBuild.gettPowerSupply());

        Boolean power = ComputerBuild.getWattageConsumption() < ComputerBuild.gettPowerSupply();
        Boolean singleCPU = true; if(ComputerBuild.cpus.size() != 1) singleCPU = false;
        Boolean singleGPU = true; if(ComputerBuild.gpus.size() != 1) singleGPU = false;
        Boolean singlePSU = true; if(ComputerBuild.psus.size() != 1) singlePSU = false;
        Boolean singleCooler = true; if(ComputerBuild.coolers.size() != 1) singleCooler = false;
        Boolean singleCase = true; if(ComputerBuild.buildCases.size() != 1) singleCase = false;
        Boolean singleMotherboard = true; if(ComputerBuild.motherboards.size() != 1) singleMotherboard = false;


        Boolean compatibilityCheck = power &&
                                    singleCPU && 
                                     singleGPU &&
                                     singlePSU && 
                                     singleGPU &&
                                     singleCooler && 
                                     singleCase &&
                                     singleMotherboard;

        model.addAttribute("compatibilityCheck", compatibilityCheck);
                                     
        System.out.println("buildCount" + ComputerBuild.getCount());
        System.out.println("buildTotal" + ComputerBuild.getTotal());

        
        return "/build";
    }


    private String idToName(int id){
        return componentService.getComponentById(id).get().getComponentType().getName();
    }



    @GetMapping("/checkout")
    public String checkout(Model model){
        if(ComputerBuild.getCount() == 0){
            System.out.println("Please pick components to buy!");
            return "redirect:/shop";
        }
       model.addAttribute("orderDTO", new OrderDTO());
     model.addAttribute("total", ComputerBuild.getTotal());
        return "checkout";
    }
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderedComponentRepository orderedComponentRepository;
    

    @PostMapping("/checkout")
    public  ResponseEntity<byte[]> checkoutPost(
        @ModelAttribute("orderDTO") OrderDTO orderDTO
    ){

     
        System.out.println("OrderDTO: " + orderDTO.customerName + " - " + orderDTO.customerAddress
       + " - " + orderDTO.customerCity
       + " - " + orderDTO.customerNumber
       + " - " + orderDTO.customerEmail);

        Order order = new Order();
        order.setId(orderDTO.getId());
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerAddress(orderDTO.getCustomerAddress());
        order.setCustomerNumber(orderDTO.getCustomerNumber());
        order.setCustomerEmail(orderDTO.getCustomerEmail());
        order.setCustomerCity(orderDTO.getCustomerCity());

        LocalDate time= LocalDate.now();
        order.setOrderDate(time);
        order.setMonth(time.getMonthValue());
        order.setYear(time.getYear());

        order.setTotalCost((int) ComputerBuild.getTotal());
        List<Component> componentList = ComputerBuild.getSingleList();
        
        Order savedOrder = orderRepository.save(order);

        for(Component component : componentList){
            OrderedComponent orderedComponent = new OrderedComponent();
            orderedComponent.setComponentName(component.getName());
            orderedComponent.setOrder(savedOrder);
            orderedComponent.setComponentType(component.getComponentType());
            orderedComponentRepository.save(orderedComponent);

           Component comp = componentService.getComponentById(component.getId()).get();
            comp.setQuantity(comp.getQuantity()-1);
            componentService.addComponent(comp);
        }

        ResponseEntity<byte[]> printResponse =  downloadComponentsPdfasReceipt(savedOrder);
        System.out.println("Order saved: " + savedOrder.getId() + " - " + savedOrder.getCustomerName() + " --Components: ");
        for(Component component : componentList){
            System.out.print(component.getName() + " - ");
        }



        ComputerBuild.clearBuild();

        return printResponse;
    }


    @Autowired
    private PdfService pdfService;

    // Other controller methods...

    @GetMapping("/download/components-pdf")
    public ResponseEntity<byte[]> downloadComponentsPdfasSharable() {
        List<Component> components = ComputerBuild.getSingleList();
        byte[] pdfBytes = pdfService.generateComponentsPdf(components);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "components.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

   // @GetMapping("/download/invoice-pdf")
    public ResponseEntity<byte[]> downloadComponentsPdfasReceipt(Order order) {
        List<Component> components = ComputerBuild.getSingleList();

        byte[] pdfBytes = pdfService.generateInvoicePdf(components, order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        System.out.println("Downloading Invoice");
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }


}

