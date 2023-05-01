package com.projects.mypcb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.ComponentType;

public interface ComponentTypeRepository extends JpaRepository<ComponentType, Integer>{
    
}
