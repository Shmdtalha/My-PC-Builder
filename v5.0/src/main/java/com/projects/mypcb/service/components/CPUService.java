package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.CPU;
import com.projects.mypcb.repository.components.CPURepository;

@Service
public class CPUService {

    @Autowired
    CPURepository ramRepository;

    public List<CPU> getAllCPUs(){
        return ramRepository.findAll();
    }
    
    public void addCPU(CPU CPU){
        ramRepository.save(CPU);
    }

    public void removeCPU(Long id){
        ramRepository.deleteById(id);
    }

    public Optional<CPU> getCPUById(long id){
        return ramRepository.findById(id);
    }

    
    
}
