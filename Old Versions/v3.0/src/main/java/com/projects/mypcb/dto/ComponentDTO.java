package com.projects.mypcb.dto;

import lombok.Data;

@Data
public class ComponentDTO {
    private Long id;
    private String Name;

    private int componentTypeId;
    private double price;
    private double wattageConsumption;
    private String manufacturerName;
    private String imageName;
}
