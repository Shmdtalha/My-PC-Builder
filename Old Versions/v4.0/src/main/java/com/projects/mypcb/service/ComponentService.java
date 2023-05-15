package com.projects.mypcb.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.repository.ComponentRepository;

@Service
public class ComponentService {
    @Autowired
    ComponentRepository componentRepository;

    public List<Component> getAllComponents(){
        return componentRepository.findAll();
    }
    
    

    public void addComponent(Component component){
        componentRepository.save(component);
    }

    public void removeComponent(Long id){
        componentRepository.deleteById(id);
    }

    public Optional<Component> getComponentById(long id){
        return componentRepository.findById(id);
    }

    public List<Component> getAllComponentsByTypeId(int id){
        return componentRepository.findAllByComponentType_Id(id);
    }
   
}
