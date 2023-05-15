package com.projects.mypcb.service.components;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mypcb.entity.components.BuildCase;
import com.projects.mypcb.repository.components.BuildCaseRepository;

@Service
public class BuildCaseService {

    @Autowired
    BuildCaseRepository buildCaseRepository;

    public List<BuildCase> getAllBuildCases(String searchKey) {
        System.out.println("Search key: " + searchKey);
        if (searchKey.equals("")) {
            return buildCaseRepository.findAll();
        } else {
            return buildCaseRepository.findByNameContainingIgnoreCase(searchKey);
        }
    }

    public List<BuildCase> getAllBuildCases(String name, String manufacturer, String type, String panel, String colour) {
        List<BuildCase> allBuildCases = getAllBuildCases(name);

        if (!manufacturer.isEmpty()) {
            allBuildCases = filterByManufacturer(allBuildCases, manufacturer);
        }

        if (!type.isEmpty()) {
            allBuildCases = filterByType(allBuildCases, type);
        }

        if (!panel.isEmpty()) {
            allBuildCases = filterByPanel(allBuildCases, panel);
        }

        if (!colour.isEmpty()) {
            allBuildCases = filterByColour(allBuildCases, colour);
        }

        return allBuildCases;
    }

    private List<BuildCase> filterByManufacturer(List<BuildCase> buildCaseList, String manufacturer) {
        return buildCaseList.stream()
                .filter(c -> c.getManufacturerName().toLowerCase().equals(manufacturer.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<BuildCase> filterByType(List<BuildCase> buildCaseList, String type) {
        return buildCaseList.stream()
                .filter(c -> c.getType().toLowerCase().equals(type.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<BuildCase> filterByPanel(List<BuildCase> buildCaseList, String panel) {
        return buildCaseList.stream()
                .filter(c -> c.getPanel().toLowerCase().equals(panel.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<BuildCase> filterByColour(List<BuildCase> buildCaseList, String colour) {
        return buildCaseList.stream()
                .filter(c -> c.getColour().toLowerCase().equals(colour.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void addBuildCase(BuildCase pcBuildCase) {
        buildCaseRepository.save(pcBuildCase);
    }

    public void removeBuildCase(Long id) {
        buildCaseRepository.deleteById(id);
    }

    public Optional<BuildCase> getBuildCaseById(long id) {
        return buildCaseRepository.findById(id);
    }
}
