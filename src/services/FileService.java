package services;

import models.Employee;
import models.Model;
import models.Outlet;
import models.Sale;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader; //reads the file by line
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader; //opens a file
import java.io.FileWriter;
import java.io.IOException; //handle file errors
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    //======== Method 1: For Employees ========
    public static List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("data/employees.csv"))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; //skips the first row (column headings)
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 4) { //prevent errors from incomplete rows
                    Employee emp = new Employee(
                            data[0].trim(), // Employee ID
                            data[1].trim(), // EmployeeName
                            data[2].trim(), // Role
                            data[3].trim() // Password
                    );
                    employees.add(emp);
                }
            }
            System.out.println("Loaded " + employees.size() + " employees with roles");

        } catch (IOException e) {
            System.out.println("Error loading CSV: " + e.getMessage());
        }
        return employees;
    }

    //======== Method 2: For Watch Models ========
    private static final String MODELS_PATH = "data/models.csv";
    private static final String SALES_PATH = "data/sales.csv";

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
    }

    // Saves a sale record to sales.csv
    public static void saveSaleToCSV(Sale sale) {
        //Check if the file is empty before adding header
        File file = new File(SALES_PATH);
        boolean fileExists = file.exists() &&  file.length() > 0;

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(SALES_PATH, true)))) {
            //If file is new/empty, add the header
            if (!fileExists) {
                out.println("Date,Time,Outlet,Customer,Total(RM),PaymentMethod,Employee");
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm");
            out.println(String.format("%s,%s,%s,%.2f,%s,%s",
                    sale.getTimestamp().format(dtf),sale.getOutletCode(), sale.getCustomerName(), sale.getTotal(),
                    sale.getMethod(), sale.getEmployeeInCharge()));
        } catch (IOException e) {
            System.out.println("Error saving sale: " + e.getMessage());
        }
    }

    // Overwrites models.csv with updated stock levels
    public static void saveModels(List<Model> models) {
        try (PrintWriter out = new PrintWriter(new FileWriter(MODELS_PATH))) {
            out.println("Model,Dial Colour,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69");
            for (Model m : models) {
                out.println(m.toCSVRow());
            }
        } catch (IOException e) {
            System.out.println("Error updating inventory: " + e.getMessage());
        }
    }

    //======== Method 3: For Outlets ========
    private static final String OUTLET_PATH = "data/outlet.csv";
    public static List<Outlet> loadOutlets() {
        List<Outlet> outlets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(OUTLET_PATH))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String code = data[0].trim();
                    String name = data[1].trim();

                    outlets.add(new Outlet(code, name));
                }
            }
            System.out.println("Loaded " + outlets.size() + " outlets");
        } catch (IOException e) {
            System.out.println("Error loading Outlets: " + e.getMessage());
        }
        return outlets;
    }

    public static void appendEmployee(Employee e) {
        try (FileWriter fw = new FileWriter("data/employees.csv", true)) {
            fw.write(
                    e.getId() + "," +
                            e.getName() + "," +
                            e.getRole() + "," +
                            e.getPassword() + "\n"
            );
        } catch (IOException ex) {
            System.out.println("Error saving new employee.");
        }
    }
    //======== Method 4: Load Sales History for report ========
    public static List<Sale> loadSales() {
        List<Sale> sales = new ArrayList<>();
        File file = new File (SALES_PATH);
        if (!file.exists()) return sales;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm");

        try(BufferedReader br= new BufferedReader(new FileReader(file))){
            String line;
            boolean firstLine = true;
            while((line = br.readLine())!= null) { //read one line at a time until the file ends
                if (firstLine) { //skip the header
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                // expect 7 columns
                if (data.length >= 7) {
                    String dateTimeString = data[0] + "," + data[1];
                    //parse cuz in csv file, it is stored as text
                    LocalDateTime timestamp = LocalDateTime.parse(dateTimeString, dtf);


                    //Extract data
                    String outletCode = data[2].trim();
                    String customerName = data[3].trim();
                    double total = Double.parseDouble(data[4].trim()); //parse cuz in csv file, it is stored as text
                    String method = data[5].trim();
                    String employeeInCharge = data[6].trim();

                    //Create object
                    //Sale(timestamp, outlet, customer, method, total, employee)
                    Sale s = new Sale(timestamp, outletCode, customerName, method,total,employeeInCharge);
                    sales.add(s);
                }
            }
        }catch (Exception e){
            System.out.println("Error loading Sales: " + e.getMessage());
        }
        return sales;
    }
}
