package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.RAM;
import com.projects.mypcb.repository.components.RAMRepository;

@Service
public class RAMService {

    @Autowired
    RAMRepository ramRepository;

    public List<RAM> getAllRAMs(String searchKey){
        System.out.println("Search key: " + searchKey);
        if(searchKey.equals("")){
            return ramRepository.findAll();
        } else{
            return ramRepository.findByNameContainingIgnoreCase(searchKey);
        }
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

    public List<RAM> getAllRAMs(String name, String manufacturer, Integer speed, Integer memory) {
       
        List<RAM> allRAMs = getAllRAMs(name);
        System.out.println("size.allRAMS(): " + allRAMs.size());
        List<RAM> test = ramRepository.findAll();
        System.out.println("Test: " + test.size());

        if(manufacturer != "") {
            allRAMs = filterByManufacturer(allRAMs, manufacturer);
            System.out.println("By Manufacturer: " + allRAMs.size());
        }
        
        if(speed != 0) {
            allRAMs = filterBySpeed(allRAMs, speed);
            System.out.println("By Speed: " + allRAMs.size());
        }
        
        if(memory != 0) {
            allRAMs = filterByMemory(allRAMs, memory);
            System.out.println("By Memory: " + allRAMs.size());
        }

      
        return allRAMs;
    }
    
    private List<RAM> filterBySpeed(List<RAM> ramList, Integer speed) {
        return ramList.stream()
                .filter(ram -> ram.getSpeed() == speed)
                .collect(Collectors.toList());
    }
    
    private List<RAM> filterByMemory(List<RAM> ramList, Integer memory) {
        return ramList.stream()
                .filter(ram -> ram.getMemory() == memory)
                .collect(Collectors.toList());
    }
    
    private List<RAM> filterByManufacturer(List<RAM> ramList, String manufacturer) {
        return ramList.stream()
                .filter(ram -> ram.getManufacturerName().equals( manufacturer))
                .collect(Collectors.toList());
    }
    


    
    
}
