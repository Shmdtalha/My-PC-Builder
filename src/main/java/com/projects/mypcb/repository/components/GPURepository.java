package com.projects.mypcb.repository.components;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.GPU;

public interface GPURepository extends JpaRepository<GPU, Long> {

    List<GPU> findAllByComponentType_Id(int id);
    public List<GPU> findByNameContainingIgnoreCase(String name);

}
