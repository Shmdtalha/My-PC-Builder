package com.projects.mypcb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.global.ComputerBuild;
import com.projects.mypcb.service.ComponentService;
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
        model.addAttribute("total", ComputerBuild.getTotal());
        return "checkout";
    }
}

