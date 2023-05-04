package com.projects.mypcb.repository.components;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.Cooler;

public interface CoolerRepository extends JpaRepository<Cooler, Long> {

    List<Cooler> findAllByComponentType_Id(int id);
    public List<Cooler> findByNameContainingIgnoreCase(String name);

}
