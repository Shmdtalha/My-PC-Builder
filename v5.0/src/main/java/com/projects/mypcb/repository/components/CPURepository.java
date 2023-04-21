package com.projects.mypcb.repository.components;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.CPU;

public interface CPURepository extends JpaRepository<CPU, Long>  {

    List<CPU> findAllByComponentType_Id(int id);

}
