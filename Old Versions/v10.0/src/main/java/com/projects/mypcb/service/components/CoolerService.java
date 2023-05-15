package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.Cooler;
import com.projects.mypcb.repository.components.CoolerRepository;

@Service
public class CoolerService {

    @Autowired
    CoolerRepository coolerRepository;

    public List<Cooler> getAllCoolers(String searchKey){
        System.out.println("Search key: " + searchKey);
        if(searchKey.equals("")){
            return coolerRepository.findAll();
        } else{
            return coolerRepository.findByNameContainingIgnoreCase(searchKey);
        }
    }

    public List<Cooler> getAllCoolers(String name, String manufacturer, Double fanRPM, Double minNoiseLevel, Double maxNoiseLevel) {
        List<Cooler> allCoolers = getAllCoolers(name);
    
        if (!manufacturer.isEmpty()) {
            allCoolers = filterByManufacturer(allCoolers, manufacturer);
        }
    
        if (fanRPM != 0) {
            allCoolers = filterByFanRPM(allCoolers, fanRPM);
        }
    
        if (minNoiseLevel != 0) {
            allCoolers = filterByMinNoiseLevel(allCoolers, minNoiseLevel);
        }

        if (maxNoiseLevel != 0) {
            allCoolers = filterByMaxNoiseLevel(allCoolers, maxNoiseLevel);
        }
    
        return allCoolers;
    }
    
    private List<Cooler> filterByManufacturer(List<Cooler> coolerList, String manufacturer) {
        return coolerList.stream()
                .filter(cooler -> cooler.getManufacturerName().toLowerCase().equals(manufacturer.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<Cooler> filterByFanRPM(List<Cooler> coolerList, Double fanRPM) {
        return coolerList.stream()
                .filter(cooler -> cooler.getFanRPM() == fanRPM)
                .collect(Collectors.toList());
    }
    
    private List<Cooler> filterByMinNoiseLevel(List<Cooler> coolerList, Double minNoiseLevel) {
        return coolerList.stream()
                .filter(cooler -> cooler.getMinNoiseLevel() >= minNoiseLevel)
                .collect(Collectors.toList());
    }

    private List<Cooler> filterByMaxNoiseLevel(List<Cooler> coolerList, Double maxNoiseLevel) {
        return coolerList.stream()
                .filter(cooler -> cooler.getMaxNoiseLevel() <= maxNoiseLevel)
                .collect(Collectors.toList());
    }
    
    public void addCooler(Cooler cooler){
        coolerRepository.save(cooler);
    }

    public void removeCooler(Long id){
        coolerRepository.deleteById(id);
    }

    public Optional<Cooler> getCoolerById(long id){
        return coolerRepository.findById(id);
    }
}
