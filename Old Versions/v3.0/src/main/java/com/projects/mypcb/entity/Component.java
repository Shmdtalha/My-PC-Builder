package com.projects.mypcb.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Component {
    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    private Long id;
    private String Name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componentType_id", referencedColumnName = "componentType_id")
    private ComponentType componentType;
    private double price;
    private double wattageConsumption;
    private String manufacturerName;
    private String imageName;
}
