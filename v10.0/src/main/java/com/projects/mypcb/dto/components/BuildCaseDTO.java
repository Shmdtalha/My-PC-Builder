package com.projects.mypcb.dto.components;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class BuildCaseDTO extends ComponentDTO {
    private String type;
    private String panel;
    private String colour;

    
}
