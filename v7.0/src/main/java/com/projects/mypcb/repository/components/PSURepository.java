package com.projects.mypcb.repository.components;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.PSU;

public interface PSURepository extends JpaRepository<PSU, Long> {

    List<PSU> findAllByComponentType_Id(int id);
    public List<PSU> findByNameContainingIgnoreCase(String name);

}
