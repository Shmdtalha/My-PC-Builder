package com.projects.mypcb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.Component;

public interface ComponentRepository extends JpaRepository<Component, Long>  {

    List<Component> findAllByComponentType_Id(int id);

}
