package com.projects.mypcb.global;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.CPU;
import com.projects.mypcb.entity.components.RAM;

public class ComputerBuild {
    public static List<List<Component>> components;
    public static List<Component> cpus;
    public static List<Component> rams;
   
    static{
          components = new ArrayList<>();
          cpus = new ArrayList<>();
          rams = new ArrayList<>();
        components.add(cpus);
        components.add(rams);
          
        // cpus.add(new CPU());
        // components.add(cpus);        
    }

    public static int getCount(){
        int count = 0;

        for (List<Component> list : components) {
            count += list.size();
        }

        return count;
    }

    public static double getTotal(){
        double total = 0;

        for (List<Component> list : components) {
            for(Component component : list){
                 total += component.getPrice();
            }
        }

        return total;
    }

    public static void printComponents(){
     

        for (List<Component> list : components) {
            for(Component component : list){
                 System.out.print(component + " ");
            }
        }

    
    }
}
