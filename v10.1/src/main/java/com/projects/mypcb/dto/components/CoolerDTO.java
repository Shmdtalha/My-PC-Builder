package com.projects.mypcb.dto.components;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CoolerDTO extends ComponentDTO {
    private Double fanRPM;
    private Double minNoiseLevel;
    private Double maxNoiseLevel;

    
}
