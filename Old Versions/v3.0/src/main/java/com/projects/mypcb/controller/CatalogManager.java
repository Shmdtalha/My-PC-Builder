package com.projects.mypcb.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.projects.mypcb.dto.ComponentDTO;
import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.ComponentType;
import com.projects.mypcb.service.ComponentService;
import com.projects.mypcb.service.ComponentTypeService;

@Controller
public class CatalogManager {
    @Autowired
    ComponentTypeService componentTypeService;

    @Autowired
    ComponentService componentService;

    @GetMapping("/manager")
    public String managerHome(){
        return "managerHome";
    }



    @GetMapping("/manager/componentTypes")
    public String getCompType(Model model){
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentTypes";
    }

    @GetMapping("/manager/componentTypes/add")
    public String getCompTypeAdd(Model model){
        model.addAttribute("componentType", new ComponentType());
        return "componentTypesAdd";
    }

    @PostMapping("/manager/componentTypes/add")
    public String postCompTypeAdd(@ModelAttribute("componentType") ComponentType componentType){
        componentTypeService.addComponentTypes(componentType);
        return "redirect:/manager/componentTypes";
    }


    @GetMapping("/manager/componentTypes/delete/{id}")
    public String deleteCompType(@PathVariable int id){
        componentTypeService.deleteComponentTypeByID(id);
        return "redirect:/manager/componentTypes";
    }

    @GetMapping("/manager/componentTypes/update/{id}")
    public String updateCompType(@PathVariable int id, Model model){
        Optional<ComponentType> componentType = componentTypeService.getComponentTypeById(id);
        if(componentType.isPresent()){
            model.addAttribute("componentType", componentType.get());
            return "componentTypesAdd";
        }

        return "404";
        
    }



    //Component Section
    @GetMapping("/manager/components")
    public String components(Model model) {
        model.addAttribute("components", componentService.getAllComponents());
        return "components";
    }

    @GetMapping("/manager/components/add")
    public String componentAddGet(Model model){
        model.addAttribute("componentDTO", new ComponentDTO());
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        return "componentsAdd";
    }

   public static String uploadDir = System.getProperty("user.dir")+ "/src/main/resources/static/componentImages";
    //public static String uploadDir = System.getProperty("user.dir")+ "/componentImages";
    @PostMapping("/manager/components/add")
    public String componentAddPost(
            @ModelAttribute("componentDTO")
            ComponentDTO componentDTO, 
            @RequestParam("componentImage")
            MultipartFile file,
            @RequestParam("imgName") 
            String imgName)
            throws IOException
    {
        Component component = new Component();
        component.setId(componentDTO.getId());
        component.setName(componentDTO.getName());
        component.setManufacturerName(componentDTO.getManufacturerName());
        component.setPrice(componentDTO.getPrice());
        component.setWattageConsumption(componentDTO.getWattageConsumption());
        component.setComponentType(componentTypeService.getComponentTypeById(componentDTO.getComponentTypeId()).get());
        
        String imageUUID;
        if(!file.isEmpty()){
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.createDirectories(fileNameAndPath.getParent()); // create parent directories if necessary
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }
    
        component.setImageName(imageUUID);
        
        componentService.addComponent(component);
        return "redirect:/manager/components";
    }
    
    
    @GetMapping("/manager/component/delete/{id}")
    public String deleteComp(@PathVariable long id){
        componentService.removeComponent(id);
        return "redirect:/manager/components";
    }

    @GetMapping("/manager/component/update/{id}")
    public String updateCompGet(@PathVariable long id, Model model){
        
        Component component = componentService.getComponentById(id).get();
        ComponentDTO  componentDTO = new ComponentDTO();
        
        componentDTO.setId(component.getId());
        componentDTO.setName(component.getName());
        componentDTO.setComponentTypeId(component.getComponentType().getId());
        componentDTO.setPrice(component.getPrice());
        componentDTO.setManufacturerName(component.getManufacturerName());
        componentDTO.setWattageConsumption(component.getWattageConsumption());
        componentDTO.setImageName(component.getImageName());
        
        model.addAttribute("componentTypes", componentTypeService.getAllComponentTypes());
        model.addAttribute("componentDTO", componentDTO);

            return "componentsAdd";
    

    
        
    }

}
