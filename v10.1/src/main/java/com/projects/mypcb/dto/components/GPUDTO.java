
package com.projects.mypcb.dto.components;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class GPUDTO extends ComponentDTO {
    private String chipset;
    private Integer memory;
    private Integer coreSpeed;

    
}
