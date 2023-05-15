package com.projects.mypcb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.projects.mypcb.entity.Role;
import com.projects.mypcb.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createRoles();
    }

    private void createRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);

            Role managerRole = new Role();
            managerRole.setName("ROLE_MANAGER");
            roleRepository.save(managerRole);

            Role userRole = new Role();
            userRole.setName("ROLE_CUSTOMER");
            roleRepository.save(userRole);

            
        }
    }
}
