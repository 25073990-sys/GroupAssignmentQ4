package services;

import models.Sale;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class SalesService {

    private static final String SALES_CSV = "data/sales.csv"; // Ensure path is correct

    public void recordNewSale(Scanner scanner, String currentEmployee) {
        System.out.println("\n=== Record New Sale ===");

        // Input Customer
        System.out.print("Customer Name: ");
        String customer = scanner.nextLine();

        // Initialize Sale
        Sale currentSale = new Sale(customer, currentEmployee, "");

        // Add Items
        boolean adding = true;
        while (adding) {
            System.out.print("Enter Model: ");
            String model = scanner.nextLine();

            System.out.print("Enter Quantity: ");
            int qty = 0;
            try {
                qty = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
                continue;
            }

            System.out.print("Unit Price: RM");
            double price = 0;
            try {
                price = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid price.");
            }

            currentSale.addItem(model, qty, price);

            System.out.print("More items? (Y/N): ");
            if (scanner.nextLine().equalsIgnoreCase("N")) adding = false;
        }

        // Method
        System.out.print("Method (Cash/Card/E-wallet): ");
        currentSale = new Sale(customer, currentEmployee, scanner.nextLine()); // Update method
        // (Note: In a real refactor, setMethod would be better, but this works for now)

        // Save
        generateTextReceipt(currentSale);
        saveToSalesHistory(currentSale);
        System.out.println("Sale Recorded. Total: RM" + currentSale.getTotal());
    }

    private void generateTextReceipt(Sale sale) {
        String fileName = "receipts/sales_" + sale.getDateStr() + ".txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            writer.println("=== SALE RECEIPT ===");
            writer.println("Date: " + sale.getDateStr() + " Time: " + sale.getTimeStr());
            writer.println("Customer: " + sale.getCustomerName());
            writer.println("Items:");
            for (Sale.SaleItem item : sale.getItems()) {
                writer.println("- " + item.getModelName() + " x" + item.getQuantity() + " (RM" + item.getSubtotal() + ")");
            }
            writer.println("Total: RM" + sale.getTotal());
            writer.println("Served by: " + sale.getEmployeeInCharge());
            writer.println("-----------------------------------");
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }

    private void saveToSalesHistory(Sale sale) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(SALES_CSV, true)))) {
            // Date,Time,Customer,Total,Method,Employee
            pw.println(sale.getDateStr() + "," + sale.getTimeStr() + "," +
                    sale.getCustomerName() + "," + sale.getTotal() + "," +
                    sale.getMethod() + "," + sale.getEmployeeInCharge());
        } catch (IOException e) {
            System.out.println("Error saving to database.");
        }
    }
}