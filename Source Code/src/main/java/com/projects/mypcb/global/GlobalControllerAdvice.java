package com.projects.mypcb.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.projects.mypcb.entity.User;
import com.projects.mypcb.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {
    
    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }
    @ModelAttribute("userSession")
    public User addUserToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findUserByEmail(userDetails.getUsername());
        }
        return null;
    }
    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    @ModelAttribute("isManager")
    public boolean isManager() {
        return hasRole("ROLE_MANAGER");
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().size() > 0) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
