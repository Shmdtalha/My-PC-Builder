package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.PSU;
import com.projects.mypcb.repository.components.PSURepository;

@Service
public class PSUService {

    @Autowired
    PSURepository psuRepository;

    public List<PSU> getAllPSUs(String searchKey) {
        if (searchKey.isEmpty()) {
            return psuRepository.findAll();
        } else {
            return psuRepository.findByNameContainingIgnoreCase(searchKey);
        }
    }

    public List<PSU> getAllPSUs(String name, String manufacturer, String type, Integer powerSupply) {
        List<PSU> allPSUs = getAllPSUs(name);

        if (!manufacturer.isEmpty()) {
            allPSUs = filterByManufacturer(allPSUs, manufacturer);
            
        }System.out.println("PSU:" + allPSUs.size());

        if (!type.isEmpty()) {
            allPSUs = filterByType(allPSUs, type);
            
        }System.out.println(allPSUs.size());

        if (powerSupply != 0) {
            allPSUs = filterByPowerSupply(allPSUs, powerSupply);
           
        } System.out.println(allPSUs.size());

        return allPSUs;
    }

    private List<PSU> filterByManufacturer(List<PSU> psuList, String manufacturer) {
        return psuList.stream()
                .filter(psu -> psu.getManufacturerName().toLowerCase().equals(manufacturer.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<PSU> filterByType(List<PSU> psuList, String type) {
        return psuList.stream()
                .filter(psu -> psu.getType().toLowerCase().equals(type.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<PSU> filterByPowerSupply(List<PSU> psuList, Integer powerSupply) {
        return psuList.stream()
                .filter(psu -> psu.getPowerSupply().equals(powerSupply))
                .collect(Collectors.toList());
    }

    public void addPSU(PSU psu) {
        psuRepository.save(psu);
    }

    public void removePSU(Long id) {
        psuRepository.deleteById(id);
    }

    public Optional<PSU> getPSUById(long id) {
        return psuRepository.findById(id);
    }
}
