package com.projects.mypcb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.mypcb.dto.UserDTO;
import com.projects.mypcb.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByRoles_Name(String roleName);
   // void saveCustomer(UserDTO userDto);
   // User findUserByEmail(String email);
}