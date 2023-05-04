package com.projects.mypcb.repository.components;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.BuildCase;


public interface BuildCaseRepository extends JpaRepository<BuildCase, Long> {

    List<BuildCase> findAllByComponentType_Id(int id);
    public List<BuildCase> findByNameContainingIgnoreCase(String name);

}
