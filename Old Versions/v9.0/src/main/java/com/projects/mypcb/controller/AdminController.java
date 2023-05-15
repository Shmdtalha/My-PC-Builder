package com.projects.mypcb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.itextpdf.io.exceptions.IOException;
import com.projects.mypcb.dto.UserDTO;
import com.projects.mypcb.entity.User;
import com.projects.mypcb.service.UserService;


import com.projects.mypcb.entity.User;
import com.projects.mypcb.repository.UserRepository;
import com.projects.mypcb.service.UserService;

@Controller
public class AdminController {
    @Autowired
    UserService userService;
    
    // @Autowired
    // UserRepository userRepository;

    @GetMapping("/admin")
    public String adminGet(Model model, Authentication authentication){
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        // Retrieve the custom User entity from the database using the username
       User user = userService.findUserByEmail(userDetails.getUsername());

        
        model.addAttribute("user", user);
        return "managers";
    }

    @GetMapping("/admin/manage-managers")
    public String adminManageManagersGet(Model model){
        List<User> managerList = userService.getAllManagers();
        System.out.println(managerList.size());
        model.addAttribute("managerList", managerList);
        return "managers";
    }

    @GetMapping("/admin/manage-managers/add")
    public String managerAddGet(Model model) {
        model.addAttribute("managerDTO", new UserDTO());
        return "managersAdd";
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("admin/manage-managers/add")
    public String managerAddPost(@ModelAttribute("managerDTO") UserDTO managerDTO) throws IOException{
        User manager = new User();
        manager.setId(managerDTO.getId());
        manager.setName(managerDTO.getName());
        manager.setEmail(managerDTO.getEmail());
        manager.setPassword(passwordEncoder.encode(managerDTO.getPassword()));

        userService.saveManager(managerDTO);
        return "redirect:/admin/manage-managers";
    }


}

