package com.projects.mypcb.global;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.components.CPU;
import com.projects.mypcb.entity.components.RAM;

public class ComputerBuild {
    public static List<List<Component>> components;
    public static List<Component> componentsSingleList;
    public static List<Component> cpus;
    public static List<Component> rams;
    public static List<Component> psus;
public static List<Component> motherboards;
    static {
        components = new ArrayList<>();
        cpus = new ArrayList<>();
        rams = new ArrayList<>();
        psus = new ArrayList<>();
        motherboards = new ArrayList<>();
        components.add(cpus);
        components.add(rams);
        components.add(psus);
        components.add(motherboards);

        // cpus.add(new CPU());
        // components.add(cpus);
    }

    public static int getCount() {
        int count = 0;

        for (List<Component> list : components) {
            count += list.size();
        }

        return count;
    }

    public static double getTotal() {
        double total = 0;

        for (List<Component> list : components) {
            for (Component component : list) {
                total += component.getPrice();
            }
        }

        

        return (int)total;
    }

    public static int getWattageConsumption() {
        int totalWattage = 0;
        for (List<Component> list : components) {
            for (Component component : list) {
            if(!component.getComponentType().getName().equals( "PSU"))
                totalWattage += component.getWattageConsumption();
            }
        }

        return totalWattage;
    }

    public static void printComponents() {

        for (List<Component> list : components) {
            for (Component component : list) {
                System.out.print(component + " ");
            }
        }

    }

    public static int gettPowerSupply() {

        int total = 0;

        for (Component component : psus) {
            total -= component.getWattageConsumption();
        }

        return total;

    }

    public  static List<Component> getSingleList() {
        List<Component> singleList = new ArrayList<Component>();
        for (List<Component> list : components) {
            for (Component component : list) {
                singleList.add(component);
            }
        }

        return singleList;
    }


    public static void clearBuild(){
        for (List<Component> list : components) {
           list.clear();
        }
    }

    public static int getPowerSupply(){
        int totalSupply = 0;
        for (Component component : psus) {
            totalSupply+= component.getWattageConsumption();
        }

        return -totalSupply;
    }
}
