package com.projects.mypcb.repository.components;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.components.Motherboard;

public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {

    List<Motherboard> findAllByComponentType_Id(int id);

    List<Motherboard> findByNameContainingIgnoreCase(String name);

    List<Motherboard> findBySocketContainingIgnoreCase(String socket);

    List<Motherboard> findByFormFactorContainingIgnoreCase(String formFactor);

}
