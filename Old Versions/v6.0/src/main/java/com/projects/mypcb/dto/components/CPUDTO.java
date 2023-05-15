package com.projects.mypcb.dto.components;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CPUDTO extends ComponentDTO {
    private String series;
    private Integer generation;
    private String model;
   
    private Double coreSpeed;
    private Integer cores;

    
}
