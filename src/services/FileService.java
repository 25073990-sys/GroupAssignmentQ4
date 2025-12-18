package services;

import models.Employee;
import models.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
                    firstLine = false;
                    continue;
                }
                String []data = line.split(",");
                if(data.length >= 4){
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

    }catch (Exception e){
        System.out.println("Error loading CSV: " + e.getMessage());
        }
        return employees;
    }

    //Method 2: For Watch Models
    public static List<Model> loadModels() {
        List<Model> models = new ArrayList<>();
        // Path matches your framework: data/models.csv
        try (BufferedReader br = new BufferedReader(new FileReader("data/models.csv"))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }

                String[] data = line.split(",");
                // Assuming: ModelID, Brand, Name, Price, Stock
                if (data.length >= 3) {
                            String name = data[0].trim();
                            String colour = data[1].trim();
                            //Convert price string to a double
                            double price = Double.parseDouble(data[2].trim());
                            models.add(new Model (name,colour,price));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading Models: " + e.getMessage());
        }
        return models;
    }

    //Method 3: For Outlets
}
