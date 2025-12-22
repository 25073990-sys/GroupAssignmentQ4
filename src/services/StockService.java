package services;

import models.Model;
import models.Employee;
import utils.ReceiptGenerator; // We will build this next
import utils.DateUtils;        // Helper for "2025-10-13" strings

import java.util.List;
import java.util.Scanner;

public class StockService {
    private List<Model> inventory;
    private ReceiptGenerator receiptGenerator;

    public StockService(List<Model> inventory) {
        this.inventory = inventory;
        this.receiptGenerator = new ReceiptGenerator();
    }

    // --- FEATURE 1: Morning/Night Stock Count  ---
    public void performStockCount(Scanner scanner) {
        System.out.println("=== Stock Count Session ===");
        System.out.println("Date: " + DateUtils.getCurrentDate());
        System.out.println("Time: " + DateUtils.getCurrentTime());

        int matches = 0;
        int mismatches = 0;

        for (Model watch : inventory) {
            System.out.println("Model: " + watch.getModelName());
            System.out.print("Enter Counted Quantity: ");

            try {
                int inputCount = Integer.parseInt(scanner.nextLine());

                System.out.println("Store Record: " + watch.getStockCount());

                if (inputCount == watch.getStockCount()) {
                    System.out.println("Stock tally correct.");
                    matches++;
                } else {
                    int diff = Math.abs(inputCount - watch.getStockCount());
                    System.out.println("! Mismatch detected (" + diff + " unit difference)"); // [cite: 84]
                    mismatches++;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number entered. Skipping model.");
            }
            System.out.println("-------------------------");
        }

        System.out.println("Total Models Checked: " + inventory.size());
        System.out.println("Tally Correct: " + matches);
        System.out.println("Mismatches: " + mismatches); // [cite: 88]

        if (mismatches > 0) {
            System.out.println("Warning: Please verify stock."); // [cite: 90]
        }
    }

    // --- FEATURE 2: Stock Transfer (In/Out)  ---
    public void processStockTransfer(Scanner scanner, Employee currentUser, boolean isStockIn) {
        String type = isStockIn ? "Stock In" : "Stock Out";
        System.out.println("=== " + type + " ===");

        System.out.print("Enter Source Outlet (From): "); // [cite: 99]
        String fromOutlet = scanner.nextLine();

        System.out.print("Enter Destination Outlet (To): "); // [cite: 100]
        String toOutlet = scanner.nextLine();

        System.out.print("Enter Model Name: ");
        String modelName = scanner.nextLine();

        // Find the model
        Model selectedModel = null;
        for (Model m : inventory) {
            if (m.getModelName().equalsIgnoreCase(modelName)) {
                selectedModel = m;
                break;
            }
        }

        if (selectedModel == null) {
            System.out.println("Error: Model not found.");
            return;
        }

        System.out.print("Enter Quantity: ");
        int qty = Integer.parseInt(scanner.nextLine());

        // Update Logic
        if (isStockIn) {
            selectedModel.adjustStock(qty); // Add stock
        } else {
            if (selectedModel.getStockCount() < qty) {
                System.out.println("Error: Insufficient stock for transfer.");
                return;
            }
            selectedModel.adjustStock(-qty); // Reduce stock
        }

        System.out.println("Model quantities updated successfully.");

        // Generate Receipt 
        receiptGenerator.generateStockReceipt(type, fromOutlet, toOutlet, selectedModel, qty, currentUser);
    }
}