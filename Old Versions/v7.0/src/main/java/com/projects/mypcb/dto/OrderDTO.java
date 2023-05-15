package com.projects.mypcb.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private long id;
    public String customerName;
    public String customerNumber;
    public String customerEmail;
    public String customerAddress;
    public String customerCity;


}
