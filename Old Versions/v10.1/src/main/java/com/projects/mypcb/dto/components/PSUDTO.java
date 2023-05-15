package com.projects.mypcb.dto.components;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PSUDTO extends ComponentDTO {
    private String type;
    private Integer powerSupply;
   
    
}
