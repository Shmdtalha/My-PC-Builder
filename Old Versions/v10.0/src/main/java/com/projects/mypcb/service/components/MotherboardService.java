package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.Motherboard;
import com.projects.mypcb.repository.components.MotherboardRepository;

@Service
public class MotherboardService {

    @Autowired
    private MotherboardRepository motherboardRepository;

    public List<Motherboard> getAllMotherboards(String searchKey) {
        if (searchKey.isEmpty()) {
            return motherboardRepository.findAll();
        } else {
            return motherboardRepository.findByNameContainingIgnoreCase(searchKey);
        }
    }

    public List<Motherboard> getAllMotherboards(String name, String manufacturerName, String socket, String formFactor, int maxMemory, int memorySlots) {
        List<Motherboard> allMotherboards = getAllMotherboards(name);

        if (!manufacturerName.isEmpty()) {
            allMotherboards = filterByManufacturer(allMotherboards, manufacturerName);
        }

        if (!socket.isEmpty()) {
            allMotherboards = filterBySocket(allMotherboards, socket);
        }

        if (!formFactor.isEmpty()) {
            allMotherboards = filterByFormFactor(allMotherboards, formFactor);
        }

        if (maxMemory > 0) {
            allMotherboards = filterByMaxMemory(allMotherboards, maxMemory);
        }

        if (memorySlots > 0) {
            allMotherboards = filterByMemorySlots(allMotherboards, memorySlots);
        }

        return allMotherboards;
    }

    private List<Motherboard> filterByManufacturer(List<Motherboard> motherboardList, String manufacturerName) {
        return motherboardList.stream()
                .filter(motherboard -> motherboard.getManufacturerName().toLowerCase().equals(manufacturerName.toLowerCase()))
                .toList();
    }

    private List<Motherboard> filterBySocket(List<Motherboard> motherboardList, String socket) {
        return motherboardList.stream()
                .filter(motherboard -> motherboard.getSocket().toLowerCase().equals(socket.toLowerCase()))
                .toList();
    }

    private List<Motherboard> filterByFormFactor(List<Motherboard> motherboardList, String formFactor) {
        return motherboardList.stream()
                .filter(motherboard -> motherboard.getFormFactor().toLowerCase().equals(formFactor.toLowerCase()))
                .toList();
    }

    private List<Motherboard> filterByMaxMemory(List<Motherboard> motherboardList, int maxMemory) {
        return motherboardList.stream()
                .filter(motherboard -> motherboard.getMaxMemory() >= maxMemory)
                .toList();
    }

    private List<Motherboard> filterByMemorySlots(List<Motherboard> motherboardList, int memorySlots) {
        return motherboardList.stream()
                .filter(motherboard -> motherboard.getMemorySlots() >= memorySlots)
                .toList();
    }

    public void addMotherboard(Motherboard motherboard) {
        motherboardRepository.save(motherboard);
    }

    public void removeMotherboard(Long id) {
        motherboardRepository.deleteById(id);
    }

    public Optional<Motherboard> getMotherboardById(long id) {
        return motherboardRepository.findById(id);
    }
}
