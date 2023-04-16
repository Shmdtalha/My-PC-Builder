package com.projects.mypcb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.RAM;
import com.projects.mypcb.repository.RAMRepository;

@Service
public class RAMService {

    @Autowired
    RAMRepository ramRepository;

    public List<RAM> getAllRAMs(){
        return ramRepository.findAll();
    }
    
    public void addRAM(RAM RAM){
        ramRepository.save(RAM);
    }

    public void removeRAM(Long id){
        ramRepository.deleteById(id);
    }

    public Optional<RAM> getRAMById(long id){
        return ramRepository.findById(id);
    }

    
    
}
