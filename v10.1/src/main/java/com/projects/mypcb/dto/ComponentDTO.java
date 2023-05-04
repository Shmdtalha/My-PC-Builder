package com.projects.mypcb.dto;

import lombok.Data;

@Data
public class ComponentDTO {
    protected Long id;
    protected String Name;
    protected int componentTypeId;
    protected double price;
    protected int quantity;
    protected double wattageConsumption;
    protected String manufacturerName;
    protected String imageName;
    protected String officialSiteLink;
    protected String utilitySiteLink;
}
