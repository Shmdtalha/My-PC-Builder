package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.GPU;
import com.projects.mypcb.repository.components.GPURepository;

@Service
public class GPUService {

    @Autowired
    GPURepository gpuRepository;

    public List<GPU> getAllGPUs(String searchKey){
        System.out.println("Search key: " + searchKey);
        if(searchKey.equals("")){
            return gpuRepository.findAll();
        } else{
            return gpuRepository.findByNameContainingIgnoreCase(searchKey);
        }
    }

    public List<GPU> getAllGPUs(String name, String manufacturer, String chipset, Integer memory, Integer coreSpeed) {
        List<GPU> allGPUs = getAllGPUs(name);
    
        if (!manufacturer.isEmpty()) {
            allGPUs = filterByManufacturer(allGPUs, manufacturer);
        }
    
        if (!chipset.isEmpty()) {
            allGPUs = filterByChipset(allGPUs, chipset);
        }
    
        if (memory != 0) {
            allGPUs = filterByMemory(allGPUs, memory);
        }
    
        if (coreSpeed != 0) {
            allGPUs = filterByCoreSpeed(allGPUs, coreSpeed);
        }
    
        return allGPUs;
    }
    
    private List<GPU> filterByManufacturer(List<GPU> gpuList, String manufacturer) {
        return gpuList.stream()
                .filter(gpu -> gpu.getManufacturerName().toLowerCase().equals(manufacturer.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<GPU> filterByChipset(List<GPU> gpuList, String chipset) {
        return gpuList.stream()
                .filter(gpu -> gpu.getChipset().toLowerCase().equals(chipset.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<GPU> filterByMemory(List<GPU> gpuList, Integer memory) {
        return gpuList.stream()
                .filter(gpu -> gpu.getMemory().equals(memory))
                .collect(Collectors.toList());
    }
    
    private List<GPU> filterByCoreSpeed(List<GPU> gpuList, Integer coreSpeed) {
        return gpuList.stream()
                .filter(gpu -> gpu.getCoreSpeed().equals(coreSpeed))
                .collect(Collectors.toList());
    }
    
    public void addGPU(GPU GPU){
        gpuRepository.save(GPU);
    }

    public void removeGPU(Long id){
        gpuRepository.deleteById(id);
    }

    public Optional<GPU> getGPUById(long id){
        return gpuRepository.findById(id);
    }
}
