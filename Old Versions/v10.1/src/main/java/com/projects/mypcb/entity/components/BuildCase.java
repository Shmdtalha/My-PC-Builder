package com.projects.mypcb.entity.components;

import com.projects.mypcb.entity.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class BuildCase extends Component{
    private String type;
    private String panel;
    private String colour;
  
}
