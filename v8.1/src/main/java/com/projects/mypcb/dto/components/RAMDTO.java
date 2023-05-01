package com.projects.mypcb.dto.components;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RAMDTO extends ComponentDTO {
    private Integer memory;
    
    private Integer speed;
    
}
