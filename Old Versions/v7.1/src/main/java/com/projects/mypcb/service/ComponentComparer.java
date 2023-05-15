package com.projects.mypcb.service;

import com.projects.mypcb.dto.ComponentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentComparer {
    private ComponentDTO ramDTO1;
    private ComponentDTO ramDTO2;
}
