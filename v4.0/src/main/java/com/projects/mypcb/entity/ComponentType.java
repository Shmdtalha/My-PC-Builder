package com.projects.mypcb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data // makes getter setters automatically
public class ComponentType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "componentType_id")
    private int id;

    private String name;
    private String attribute1 = "NULL";
   
    
    
}
