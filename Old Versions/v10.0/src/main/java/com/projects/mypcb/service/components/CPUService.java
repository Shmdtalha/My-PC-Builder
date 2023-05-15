package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.CPU;
import com.projects.mypcb.repository.components.CPURepository;

@Service
public class CPUService {

    @Autowired
    CPURepository cpuRepository;

    public List<CPU> getAllCPUs(String searchKey){
        System.out.println("Search key: " + searchKey);
        if(searchKey.equals("")){
            return cpuRepository.findAll();
        } else{
            return cpuRepository.findByNameContainingIgnoreCase(searchKey);
        }
    }

    public List<CPU> getAllCPUs(String name, String manufacturer, String series, Integer generation, String model, Integer cores, Double coreSpeed) {
        List<CPU> allCPUs = getAllCPUs(name);
    
        if (!manufacturer.isEmpty()) {
            allCPUs = filterByManufacturer(allCPUs, manufacturer);
        }
    
        if (!series.isEmpty()) {
            allCPUs = filterBySeries(allCPUs, series);
        }
    
        if (generation != 0) {
            allCPUs = filterByGeneration(allCPUs, generation);
        }
    
        if (!model.isEmpty()) {
            allCPUs = filterByModel(allCPUs, model);
        }
    
        if (cores != 0) {
            allCPUs = filterByCores(allCPUs, cores);
        }
    
        if (coreSpeed != 0) {
            allCPUs = filterByCoreSpeed(allCPUs, coreSpeed);
        }
    
        return allCPUs;
    }
    
    private List<CPU> filterByManufacturer(List<CPU> cpuList, String manufacturer) {
        return cpuList.stream()
                .filter(cpu -> cpu.getManufacturerName().toLowerCase().equals(manufacturer.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<CPU> filterBySeries(List<CPU> cpuList, String series) {
        return cpuList.stream()
                .filter(cpu -> cpu.getSeries().toLowerCase().equals(series.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<CPU> filterByGeneration(List<CPU> cpuList, Integer generation) {
        return cpuList.stream()
                .filter(cpu -> cpu.getGeneration() ==generation)
                .collect(Collectors.toList());
    }
    
    private List<CPU> filterByModel(List<CPU> cpuList, String model) {
        return cpuList.stream()
                .filter(cpu -> cpu.getModel().toLowerCase().equals(model.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<CPU> filterByCores(List<CPU> cpuList, Integer cores) {
        return cpuList.stream()
                .filter(cpu -> cpu.getCores() == cores)
                .collect(Collectors.toList());
    }
    
    private List<CPU> filterByCoreSpeed(List<CPU> cpuList, Double coreSpeed) {
        return cpuList.stream()
                .filter(cpu -> cpu.getCoreSpeed().equals(coreSpeed))
                .collect(Collectors.toList());
    }
    
    public void addCPU(CPU CPU){
        cpuRepository.save(CPU);
    }

    public void removeCPU(Long id){
        cpuRepository.deleteById(id);
    }

    public Optional<CPU> getCPUById(long id){
        return cpuRepository.findById(id);
    }

  

    
    
}
