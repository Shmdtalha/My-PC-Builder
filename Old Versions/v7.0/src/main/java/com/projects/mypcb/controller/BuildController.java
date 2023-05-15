package com.projects.mypcb.controller;

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
import com.projects.mypcb.service.components.PSUService;
import com.projects.mypcb.service.components.RAMService;

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

        model.addAttribute("wattageConsumption", ComputerBuild.getWattageConsumption());
        model.addAttribute("powerSupply", ComputerBuild.gettPowerSupply());

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

        order.setTotalCost((int) ComputerBuild.getTotal());
        List<Component> componentList = ComputerBuild.getSingleList();
        
        Order savedOrder = orderRepository.save(order);

        for(Component component : componentList){
            OrderedComponent orderedComponent = new OrderedComponent();
            orderedComponent.setComponentName(component.getName());
            orderedComponent.setOrder(savedOrder);
            orderedComponentRepository.save(orderedComponent);

            component.setQuantity(component.getQuantity()-1);
            componentService.addComponent(component);
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

