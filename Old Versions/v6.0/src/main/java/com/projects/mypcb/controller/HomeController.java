package com.projects.mypcb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.global.ComputerBuild;
import com.projects.mypcb.service.ComponentService;
import com.projects.mypcb.service.ComponentTypeService;
import com.projects.mypcb.service.components.CPUService;
import com.projects.mypcb.service.components.RAMService;



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

    @GetMapping({"/", "/home"})
    public String home(Model model){
        model.addAttribute("buildCount", ComputerBuild.getCount());
       
        return "index";
    }

    @GetMapping("/shop")
    public String shop(Model model,
                        @RequestParam(defaultValue = "") String searchKey){ 
        model.addAttribute("buildCount", ComputerBuild.getCount());
       
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        List<Component> searchResult = componentService.getAllComponents(searchKey);
        model.addAttribute("components", searchResult);
        System.out.println(searchResult.size());
        return "shop";
    }

    @GetMapping("/shop/componentType/{id}")
    public String shopByComponentType(Model model, @PathVariable int id){
        model.addAttribute("buildCount", ComputerBuild.getCount());
       
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        model.addAttribute("components", componentService.getAllComponentsByTypeId(id));
        return "shop";
    }

    @GetMapping("/shop/viewcomponent/{id}")
    public String viewProduct(Model model, @PathVariable int id){
        model.addAttribute("buildCount", ComputerBuild.getCount());
       
        String name = idToName(id);
        if(name.equals("RAM"))
            model.addAttribute("component", ramService.getRAMById(id).get());
        if(name.equals( "CPU"))
            model.addAttribute("component", cpuService.getCPUById(id).get());
        return "viewProduct";
    }   

    @GetMapping("build/removeItem/{index}/{type}")
    public String buildItemRemove(@PathVariable int index, @PathVariable int type){
        String name = idToType(type);
        if(name.equals("RAM"))
            ComputerBuild.rams.remove(index);
        if(name.equals("CPU"))
            ComputerBuild.cpus.remove(index);
        return "redirect:/build";

    }

    private String idToName(int id){
        return componentService.getComponentById(id).get().getComponentType().getName();
    }

    private String idToType(int id){
        return componentTypeService.getComponentTypeById(id).get().getName();
    }
       


       



    
}
