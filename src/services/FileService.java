package services;

import models.Employee;
import models.Model;
import models.Outlet;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    //Method 1: For Employees
    public static List<Employee> loadEmployees(){
        List<Employee> employees = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader("data/employees.csv"))){
            String line;
            boolean firstLine = true;

            while((line = br.readLine()) != null){
                if(firstLine){
                    firstLine = false; //skips the first row (column headings)
                    continue;
                }
                String []data = line.split(",");
                if(data.length >= 4){ //prevent errors from incomplete rows
                    Employee emp = new Employee(
                            data[0].trim(), // Employee ID
                            data[1].trim(), // EmployeeName
                            data[2].trim(), // Role
                            data[3].trim() // Password
                    );
                    employees.add(emp);
                }
            }
        System.out.println("Loaded" + employees.size() + " employees with roles");

    }catch (IOException e){
        System.out.println("Error loading CSV: " + e.getMessage());
        }
        return employees;
    }

    //Method 2: For Watch Models
    private static final String MODELS_PATH = "data/models.csv";
    private static final String OUTLET_PATH = "data/outlet.csv";

    //Loads models with stock for all 10 outlets
    public static List<Model> loadModels() {
        List<Model> models = new ArrayList<>();
        String[] outlets = {"C60", "C61", "C62", "C63", "C64", "C65", "C66", "C67", "C68", "C69"};

        // Path matches framework: data/models.csv
        try (BufferedReader br = new BufferedReader(new FileReader(MODELS_PATH))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 13) {
                    String name = data[0].trim();
                    String colour = data[1].trim();
                    double price = Double.parseDouble(data[2].trim());

                    Model m = new Model(name, colour, price);
                    for (int i = 0; i < outlets.length; i++) {
                        int stockQty = Integer.parseInt(data[i + 3].trim());
                        m.addStock(outlets[i], stockQty);
                    }
                    models.add(m);
                }
            }
            System.out.println("Loaded " + models.size() + " models.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading Models: " + e.getMessage());
        }
        return models;

    // Saves a sale record to sales.csv
    public static void saveSaleToCSV(Sale sale) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(SALES_PATH, true)))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm");
            out.println(String.format("%s,%s,%.2f,%s,%s,%s",
                    sale.getTimestamp().format(dtf), sale.getCustomerName(), sale.getTotal(),
                    sale.getMethod(), sale.getEmployeeName(), sale.getOutletCode()));
        } catch (IOException e) { System.out.println("Error saving sale: " + e.getMessage()); }
    }

    // Overwrites models.csv with updated stock levels
    public static void saveModels(List<Model> models) {
        try (PrintWriter out = new PrintWriter(new FileWriter(MODELS_PATH))) {
            out.println("Model,Dial Colour,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69");
            for (Model m : models) { out.println(m.toCSVRow()); }
        } catch (IOException e) { System.out.println("Error updating inventory: " + e.getMessage()); }
    }
        public static void saveSaleToHistory(Sale sale) {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/sales.csv", true)))) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm");
                // Format: Date,Time,Customer,Total,Method,Employee,Outlet
                out.println(String.format("%s,%s,%.2f,%s,%s,%s",
                        sale.getTimestamp().format(dtf),
                        sale.getCustomerName(),
                        sale.getTotal(),
                        sale.getMethod(),
                        sale.getEmployeeName(),
                        sale.getOutletCode()));
            } catch (IOException e) {
                System.err.println("Error writing to sales history: " + e.getMessage());
            }
        }

    //Method 3: For Outlets
    public static List<Outlet> loadOutlets(){
        List<Outlet> outlets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader (new FileReader("data/outlet.csv"))){
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null){
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String code = data[0].trim();
                    String name= data[1].trim();

                    outlets.add(new Outlet (code,name));
                }
            }
            System.out.println("Loaded" + outlets.size() + " outlets");
        }catch (IOException e){
            System.out.println("Error loading Outlets: " + e.getMessage());
        }
        return outlets;
    }
