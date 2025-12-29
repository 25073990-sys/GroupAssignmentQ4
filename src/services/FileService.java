package services;

import models.Employee;
import models.Model;
import models.Outlet;

import java.io.BufferedReader; //reads the file by line
import java.io.FileReader; //opens a file
import java.io.IOException; //handle file errors
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

//    //Method 2: For Watch Models
//    public static List<Model> loadModels() {
//        List<Model> models = new ArrayList<>();
//        String[] outletHeaders = {"C60", "C61", "C62", "C63", "C64", "C65", "C66", "C67", "C68", "C69"};

//        // Path matches your framework: data/models.csv
//        try (BufferedReader br = new BufferedReader(new FileReader("data/models.csv"))) {
//            String line;
//            boolean firstLine = true;
//            while ((line = br.readLine()) != null) {
//                if (firstLine) {
//                    firstLine = false;
//                    continue;
//                }
//
//                String[] data = line.split(",");
//                // Assuming: ModelID, Brand, Name, Price, Stock
//                if (data.length >= 13) {
//                    String name = data[0].trim();
//                    String colour = data[1].trim();
//                    double price = Double.parseDouble(data[2].trim());
//
//                    Model m = new Model(name, colour, price);
//                    for (int i = 0; i < outletHeaders.length; i++) {
//                        int stockQty = Integer.parseInt(data[i + 3].trim());
//                        m.addStock(outletHeaders[i], stockQty);
//                    }
//                    models.add(m);
//                }
//            }
//            System.out.println("Loaded " + models.size() + " models.");
//        } catch (IOException | NumberFormatException e) {
//            System.out.println("Error loading Models: " + e.getMessage());
//        }
//        return models;
//    }
//
//    //Method 3: For Outlets
//    public static List<Outlet> loadOutlets(){
//        List<Outlet> outlets = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader (new FileReader("data/outlet.csv"))){
//            String line;
//            boolean firstLine = true;
//
//            while ((line = br.readLine()) != null){
//                if (firstLine) {
//                    firstLine = false;
//                    continue;
//                }
//                String[] data = line.split(",");
//                if (data.length >= 3) {
//                    String code = data[0].trim();
//                    String name= data[1].trim();
//
//                    outlets.add(new Outlet (code,name));
//                }
//            }
//            System.out.println("Loaded" + outlets.size() + " outlets");
//        }catch (IOException e){
//            System.out.println("Error loading Outlets: " + e.getMessage());
//        }
//        return outlets;
    }
