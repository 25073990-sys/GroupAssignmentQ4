package services;

import models.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileService {
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
}
