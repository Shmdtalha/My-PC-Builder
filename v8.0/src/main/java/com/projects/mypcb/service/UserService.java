package com.projects.mypcb.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projects.mypcb.dto.UserDTO;
import com.projects.mypcb.entity.Role;
import com.projects.mypcb.entity.User;
import com.projects.mypcb.global.RoleConstants;
import com.projects.mypcb.repository.RoleRepository;
import com.projects.mypcb.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

 
    public void saveCustomer(UserDTO userDto) {
        Role role = roleRepository.findByName(RoleConstants.Roles.CUSTOMER);

        if (role == null)
            role = roleRepository.save(new Role(RoleConstants.Roles.CUSTOMER));

        User customer = new User(userDto.getName(), userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()),
                Arrays.asList(role));
        userRepository.save(customer);
    }


    public void saveManager(UserDTO managerDto) {
        Role role = roleRepository.findByName(RoleConstants.Roles.MANAGER);

        if (role == null)
            role = roleRepository.save(new Role(RoleConstants.Roles.MANAGER));

        User user = new User(managerDto.getName(), managerDto.getEmail(), passwordEncoder.encode(managerDto.getPassword()),
                Arrays.asList(role));
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllAdmins() {
        return userRepository.findByRoles_Name("ROLE_ADMIN");
    }

    public List<User> getAllManagers() {
        return userRepository.findByRoles_Name("ROLE_MANAGER");
    }
    public List<User> getAllCustomers() {
        return userRepository.findByRoles_Name("ROLE_CUSTOMER");
    }
}