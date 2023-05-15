package com.projects.mypcb.dto.components;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MotherboardDTO extends ComponentDTO {
    private String socket;
    private String formFactor;
    private Integer maxMemory;
    private Integer memorySlots;
}
