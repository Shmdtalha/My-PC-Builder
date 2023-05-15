
package com.projects.mypcb.dto.components;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StorageDTO extends ComponentDTO {
    private String type;
    private Integer capacity;
    private Integer cache;

}
