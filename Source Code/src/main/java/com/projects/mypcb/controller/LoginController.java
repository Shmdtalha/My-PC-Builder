package com.projects.mypcb.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projects.mypcb.dto.UserDTO;
import com.projects.mypcb.entity.User;
import com.projects.mypcb.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        UserDTO user = new UserDTO();
        model.addAttribute("user", user);
        return "register";
    }

    

    @PostMapping("/registration")
    public String registration(
            @Valid @ModelAttribute("user") UserDTO userDto,
            BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
    
        System.out.println("Registering: " + userDto.getName() + " - " + userDto.getEmail() + " - " + userDto.getPassword());
        User existingUser = userService.findUserByEmail(userDto.getEmail());
    
        if (existingUser != null){
            result.rejectValue("email", null,
                    "User already registered !!!");
    
            redirectAttributes.addFlashAttribute("error", "User already present!");
            return "redirect:/registration"; // Modify this line
        }
    
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }
    
        userService.saveCustomer(userDto);
        return "redirect:/registration?success";
    }
    
}