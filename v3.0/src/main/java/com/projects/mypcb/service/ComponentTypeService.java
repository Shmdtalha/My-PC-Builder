package com.projects.mypcb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.ComponentType;
import com.projects.mypcb.repository.ComponentTypeRepository;

@Service
public class ComponentTypeService {
    
    @Autowired
    ComponentTypeRepository componentTypeRepository;


    public List<ComponentType> getAllComponentTypes(){
        return componentTypeRepository.findAll();
    }
    public void addComponentTypes(ComponentType componentType){
        componentTypeRepository.save(componentType);
    }
    public void deleteComponentTypeByID(int id){
        componentTypeRepository.deleteById(id);
    }

    public Optional<ComponentType> getComponentTypeById(int id){
        return componentTypeRepository.findById(id);
    }

    
}
