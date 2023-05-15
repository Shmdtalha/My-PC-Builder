package com.projects.mypcb.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import jakarta.persistence.InheritanceType;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Component {
    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    protected Long id;
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componentType_id", referencedColumnName = "componentType_id")
    protected ComponentType componentType;
    protected double price;
    protected int quantity;
    protected double wattageConsumption;
    protected String manufacturerName;
    protected String imageName;
    protected String officialSiteLink;
    
    @Column(nullable = true)
    protected String utilitySiteLink;
}
