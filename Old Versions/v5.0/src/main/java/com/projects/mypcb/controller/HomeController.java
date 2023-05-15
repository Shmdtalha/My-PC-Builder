package com.projects.mypcb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.projects.mypcb.service.ComponentService;
import com.projects.mypcb.service.ComponentTypeService;
import com.projects.mypcb.service.components.RAMService;



@Controller
public class HomeController {
    @Autowired
    ComponentTypeService componentTypeService;

    @Autowired
    ComponentService componentService;

    @Autowired
    RAMService ramService;

    @GetMapping({"/", "/home"})
    public String home(Model model){
        return "index";
    }

    @GetMapping("/shop")
    public String shop(Model model){
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        model.addAttribute("components", componentService.getAllComponents());
        return "shop";
    }

    @GetMapping("/shop/componentType/{id}")
    public String shopByComponentType(Model model, @PathVariable int id){
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        model.addAttribute("components", componentService.getAllComponentsByTypeId(id));
        return "shop";
    }

    @GetMapping("/shop/viewcomponent/{id}")
    public String viewProduct(Model model, @PathVariable int id){

        model.addAttribute("component", ramService.getRAMById(id).get());
        return "viewProduct";
    }   
       



    
}
