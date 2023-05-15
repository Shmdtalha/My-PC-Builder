package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.Storage;
import com.projects.mypcb.repository.components.StorageRepository;

@Service
public class StorageService {

    @Autowired
    StorageRepository storageRepository;

    public List<Storage> getAllStorages(String searchKey) {
        System.out.println("Search key: " + searchKey);
        if (searchKey.equals("")) {
            return storageRepository.findAll();
        } else {
            return storageRepository.findByNameContainingIgnoreCase(searchKey);
        }
    }

    public List<Storage> getAllStorages(String name, String manufacturer, String type, Integer capacity, Integer cache) {
        List<Storage> allStorages = getAllStorages(name);

        if (!manufacturer.isEmpty()) {
            allStorages = filterByManufacturer(allStorages, manufacturer);
        }

        if (!type.isEmpty()) {
            allStorages = filterByType(allStorages, type);
        }

        if (capacity != 0) {
            allStorages = filterByCapacity(allStorages, capacity);
        }

        if (cache != 0) {
            allStorages = filterByCache(allStorages, cache);
        }

        return allStorages;
    }

    private List<Storage> filterByManufacturer(List<Storage> storageList, String manufacturer) {
        return storageList.stream()
                .filter(storage -> storage.getManufacturerName().toLowerCase().equals(manufacturer.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<Storage> filterByType(List<Storage> storageList, String type) {
        return storageList.stream()
                .filter(storage -> storage.getType().toLowerCase().equals(type.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<Storage> filterByCapacity(List<Storage> storageList, Integer capacity) {
        return storageList.stream()
                .filter(storage -> storage.getCapacity().equals(capacity))
                .collect(Collectors.toList());
    }

    private List<Storage> filterByCache(List<Storage> storageList, Integer cache) {
        return storageList.stream()
                .filter(storage -> storage.getCache().equals(cache))
                .collect(Collectors.toList());
    }

    public void addStorage(Storage storage) {
        storageRepository.save(storage);
    }

    public void removeStorage(Long id) {
        storageRepository.deleteById(id);
    }

    public Optional<Storage> getStorageById(long id) {
        return storageRepository.findById(id);
    }
}
