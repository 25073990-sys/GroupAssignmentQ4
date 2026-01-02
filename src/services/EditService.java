package services;

import models.Employee;
import models.Model;
import models.Sale;
import models.SaleItem;

import java.util.List;
import java.util.Scanner;

public class EditService {

    private List<Model> inventory;
    private List<Sale> salesHistory;

    // Colors
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    public EditService(List<Model> inventory, List<Sale> salesHistory) {
        this.inventory = inventory;
        this.salesHistory = salesHistory;
    }

    // ==========================================
    // 1. EDIT STOCK
    // ==========================================
    public void editStock(Scanner scanner, Employee currentUser) {
        System.out.println("\n=== Edit Stock Information ===");

        // Identify outlet from Employee ID (e.g., C6013 -> C60)
        String outletCode = "C60"; // Default
        if (currentUser.getId().length() >= 3) {
            outletCode = currentUser.getId().substring(0, 3).toUpperCase();
        }

        System.out.print("Enter Model Name: ");
        String modelName = scanner.nextLine().trim();

        Model found = inventory.stream()
                .filter(m -> m.getModelName().equalsIgnoreCase(modelName))
                .findFirst()
                .orElse(null);

        if (found != null) {
            int currentQty = found.getStock(outletCode);
            System.out.println("\nCurrent Stock: " + currentQty);

            System.out.print("Enter New Stock Value: ");
            try {
                int newQty = Integer.parseInt(scanner.nextLine().trim());

                // Update the stock
                found.setStock(outletCode, newQty);

                System.out.println("\nStock information updated " + GREEN + "successfully" + RESET + ".");
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid number format." + RESET);
            }

        } else {
            System.out.println(RED + "Model not found." + RESET);
        }
    }

    // ==========================================
    // 2. EDIT SALES
    // ==========================================
    public void editSales(Scanner scanner) {
        System.out.println("\n=== Edit Sales Information ===");

        System.out.print("Enter Transaction Date: "); // e.g., 2025-10-13
        String dateInput = scanner.nextLine().trim();

        System.out.print("Enter Customer Name: ");
        String customerName = scanner.nextLine().trim();

        // Find the sale
        Sale targetSale = null;
        if (salesHistory != null) {
            for (Sale s : salesHistory) {
                // Match Date (Check if timestamp string contains the date input)
                boolean dateMatch = s.getTimestamp().toString().startsWith(dateInput);
                boolean nameMatch = s.getCustomerName().equalsIgnoreCase(customerName);

                if (dateMatch && nameMatch) {
                    targetSale = s;
                    break;
                }
            }
        }

        if (targetSale != null) {
            // Display found record
            System.out.println("\nSales Record Found:");
            // Showing first item for simplicity as per screenshot
            if (!targetSale.getItems().isEmpty()) {
                SaleItem firstItem = targetSale.getItems().get(0);
                System.out.println("Model: " + firstItem.getModelName() + " Quantity: " + firstItem.getQuantity());
            }
            System.out.printf("Total: RM%.0f%n", targetSale.getTotal());
            System.out.println("Transaction Method: " + targetSale.getMethod());
            System.out.println();

            // Edit Menu
            System.out.println("Select number to edit:");
            System.out.println("1. Name    2. Model    3. Quantity    4. Total");
            System.out.println("5. Transaction Method");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            System.out.println();

            switch (choice) {
                case "1":
                    System.out.print("Enter New Customer Name: ");
                    targetSale.setCustomerName(scanner.nextLine());
                    break;
                case "2":
                    System.out.print("Enter New Model: ");
                    // For simplicity, updating the first item
                    if (!targetSale.getItems().isEmpty()) {
                        targetSale.getItems().get(0).setModelName(scanner.nextLine());
                    }
                    break;
                case "3":
                    System.out.print("Enter New Quantity: ");
                    if (!targetSale.getItems().isEmpty()) {
                        int q = Integer.parseInt(scanner.nextLine());
                        targetSale.getItems().get(0).setQuantity(q);
                    }
                    break;
                case "4":
                    System.out.print("Enter New Total: ");
                    double t = Double.parseDouble(scanner.nextLine());
                    targetSale.setTotal(t);
                    break;
                case "5":
                    System.out.print("Enter New Transaction Method: ");
                    targetSale.setMethod(scanner.nextLine());
                    break;
                default:
                    System.out.println("Invalid selection.");
                    return;
            }

            System.out.print("Confirm Update? (Y/N): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("Y")) {
                System.out.println("\nSales information updated " + GREEN + "successfully" + RESET + ".");
            } else {
                System.out.println("Update cancelled.");
            }

        } else {
            System.out.println(RED + "\nSales record not found." + RESET);
        }
    }
}