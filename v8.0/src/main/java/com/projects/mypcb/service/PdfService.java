package com.projects.mypcb.service;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFontFamilies;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import com.projects.mypcb.dto.OrderDTO;
import com.projects.mypcb.entity.Component;
import com.projects.mypcb.entity.Order;
import com.projects.mypcb.global.ComputerBuild;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

@Service
public class PdfService {

    public byte[] generateComponentsPdf(List<Component> components) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add a title
        document.add(new Paragraph("Components List")
              //  .setFont(PdfFontFactory.createFont(FontStyles.HELVETICA_BOLD))
                .setFontSize(18));

        // Create a table with 4  columns for ID, name, price, and wattage consumption
        float[] columnWidths = {3, 10, 3, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));

        // Add table headers
        table.addHeaderCell("Component");
        table.addHeaderCell("Name");
        table.addHeaderCell("Price");
        table.addHeaderCell("Wattage Consumption");

        // Add table rows
        for (Component component : components) {
            table.addCell(String.valueOf(component.getComponentType().getName()));
            table.addCell(component.getName());
            table.addCell("$" + String.valueOf(component.getPrice()));
            table.addCell(String.valueOf(component.getWattageConsumption()) + "W");
        }

        
        float[] columnWidths2 = {1,1,1,1};
        Table table2 = new Table(UnitValue.createPercentArray(columnWidths2));
        table2.addHeaderCell("No. of Components");
        table2.addHeaderCell("Total Cost");
        table2.addHeaderCell("Wattage Consumption");
        table2.addHeaderCell("Power Supply");

        table2.addCell(String.valueOf(ComputerBuild.getSingleList().size()));
        table2.addCell("$" + String.valueOf(ComputerBuild.getTotal()));
        table2.addCell(String.valueOf(ComputerBuild.getWattageConsumption())+ "W");
        table2.addCell(String.valueOf(ComputerBuild.getPowerSupply())+ "W");

        
        table.setMarginBottom(20);
        document.add(table);
        document.add(table2);
        document.close();

        return outputStream.toByteArray();
    }



    public byte[] generateInvoicePdf(List<Component> components, Order dto) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add a title
        document.add(new Paragraph("Receipt")
              //  .setFont(PdfFontFactory.createFont(FontStyles.HELVETICA_BOLD))
                .setFontSize(20)).setMargins(0, 5, 5, 5);


        document.add(new Paragraph("Name: " + dto.getCustomerName()));
        document.add(new Paragraph("Email: " + dto.getCustomerEmail()));
        document.add(new Paragraph("Number: " + dto.getCustomerNumber()));
        document.add(new Paragraph("Address: " + dto.getCustomerAddress()));
        document.add(new Paragraph("Town/City : " + dto.getCustomerCity()));
        // Create a table with 4  columns for ID, name, price, and wattage consumption

        document.add(new Paragraph("Components").setFontSize(16)).setMargins(15, 0, 0, 0);;
        float[] columnWidths = {3, 10, 3, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));

        // Add table headers
        table.addHeaderCell("Component");
        table.addHeaderCell("Name");
        table.addHeaderCell("Price");
        table.addHeaderCell("Wattage Consumption");

        // Add table rows
        for (Component component : components) {
            table.addCell(String.valueOf(component.getComponentType().getName()));
            table.addCell(component.getName());
            table.addCell("$" + String.valueOf(component.getPrice()));
            table.addCell(String.valueOf(component.getWattageConsumption()) + "W");
        }

        
        float[] columnWidths2 = {1,1,1,1};
        Table table2 = new Table(UnitValue.createPercentArray(columnWidths2));
        table2.addHeaderCell("No. of Components");
        table2.addHeaderCell("Total Cost");
        table2.addHeaderCell("Wattage Consumption");
        table2.addHeaderCell("Power Supply");

        table2.addCell(String.valueOf(ComputerBuild.getSingleList().size()));
        table2.addCell("$" + String.valueOf(ComputerBuild.getTotal()));
        table2.addCell(String.valueOf(ComputerBuild.getWattageConsumption())+ "W");
        table2.addCell(String.valueOf(ComputerBuild.getPowerSupply())+ "W");

        
        table.setMarginBottom(20);
        table.setMarginTop(5);
        document.add(table);
        document.add(table2);
        document.close();

        return outputStream.toByteArray();
    }
}