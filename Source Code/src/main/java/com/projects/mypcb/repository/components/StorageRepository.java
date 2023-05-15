package com.projects.mypcb.repository.components;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.mypcb.entity.components.Storage;

public interface StorageRepository extends JpaRepository<Storage, Long>  {

    List<Storage> findAllByComponentTypeId(int componentTypeId);
    List<Storage> findByNameContainingIgnoreCase(String name);

}
