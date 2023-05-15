package com.projects.mypcb.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RAMDTO extends ComponentDTO {
    private int memory;
    private int speed;
    
}
