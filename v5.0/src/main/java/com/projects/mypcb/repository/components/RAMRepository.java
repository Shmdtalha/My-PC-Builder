package com.projects.mypcb.repository.components;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.RAM;

public interface RAMRepository extends JpaRepository<RAM, Long>  {

    List<RAM> findAllByComponentType_Id(int id);

}
