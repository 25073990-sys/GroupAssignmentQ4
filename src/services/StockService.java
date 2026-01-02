package services;

import models.Employee;
import models.Model;
import models.StockTransaction;
import utils.DateUtils;
import utils.ReceiptGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class StockService {

    private List<Model> inventory;
    private ReceiptGenerator receiptGenerator;

    // File paths
    private static final String MODELS_FILE = "data/models.csv";
    private static final String TRANS_FILE = "data/stock_transactions.csv";

    // Valid outlet codes
    private static final String[] OUTLETS =
            {"C60","C61","C62","C63","C64","C65","C66","C67","C68","C69"};

    // Colors to match your screenshots (Green for success)
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    public StockService(List<Model> inventory) {
        this.inventory = inventory;
        this.receiptGenerator = new ReceiptGenerator();
    }

    // ==========================================================
    // FEATURE 1: Morning & Night Stock Count
    // ==========================================================
    public void performStockCount(Scanner scanner) {

        System.out.println("\n=== Morning/Night Stock Count ===");
        System.out.println("Date: " + DateUtils.getCurrentDate());
        System.out.println("Time: " + DateUtils.getCurrentTime());

        System.out.print("Enter Outlet Code (e.g., C60): ");
        String outlet = scanner.nextLine().trim().toUpperCase();

        if (!isValidOutlet(outlet)) {
            System.out.println(RED + "Invalid Outlet Code." + RESET);
            return;
        }

        int matches = 0;
        int mismatches = 0;

        System.out.println("\n--- Start Counting ---");

        for (Model m : inventory) {
            int systemStock = m.getStock(outlet);

            // Prompt user
            System.out.print("Model: " + m.getModelName() + " | Count: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            try {
                int counted = Integer.parseInt(input);

                if (counted == systemStock) {
                    System.out.println(GREEN + "Stock tally correct." + RESET);
                    matches++;
                } else {
                    System.out.println(RED + "Mismatch! System Record: " + systemStock + RESET);
                    mismatches++;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }

        System.out.println("\n--- Summary ---");
        System.out.println("Matches: " + matches);
        System.out.println("Mismatches: " + mismatches);

        if (mismatches > 0) {
            System.out.println(RED + "Warning: Please verify stock discrepancies." + RESET);
        } else {
            System.out.println(GREEN + "Stock count complete. All Good." + RESET);
        }
    }

    // ==========================================================
    // FEATURE 2: Stock In & Stock Out
    // Matches the screenshot output exactly
    // ==========================================================
    public void processStockTransfer(Scanner scanner, Employee user, boolean isStockIn) {

        String type = isStockIn ? "Stock In" : "Stock Out";

        System.out.println("\n=== " + type + " ===");
        System.out.println("Date: " + DateUtils.getCurrentDate());
        System.out.println("Time: " + DateUtils.getCurrentTime());

        // 1. Get Details
        System.out.print("From (Outlet Code or HQ): ");
        String from = scanner.nextLine().trim().toUpperCase();

        System.out.print("To (Outlet Code or HQ): ");
        String to = scanner.nextLine().trim().toUpperCase();

        if (!isValidOutlet(to) && !to.equals("HQ")) {
            System.out.println(RED + "Invalid destination." + RESET);
            return;
        }

        StockTransaction transaction = new StockTransaction(type, from, to, user.getName());

        // 2. Add Items loop
        while (true) {
            System.out.print("Model Name (blank to finish): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) break;

            Model model = inventory.stream()
                    .filter(m -> m.getModelName().equalsIgnoreCase(name))
                    .findFirst().orElse(null);

            if (model == null) {
                System.out.println(RED + "Model not found." + RESET);
                continue;
            }

            System.out.print("Quantity: ");
            try {
                int qty = Integer.parseInt(scanner.nextLine());
                if (qty <= 0) continue;

                // Validation: Check source stock (unless it's HQ)
                if (!from.equals("HQ") && model.getStock(from) < qty) {
                    System.out.println(RED + "Insufficient stock at " + from + RESET);
                    continue;
                }

                // Update memory
                if (!from.equals("HQ")) model.adjustStock(from, -qty);
                if (!to.equals("HQ"))   model.adjustStock(to, qty);

                transaction.addItem(model.getModelName(), qty);

            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
            }
        }

        // 3. Finalize & Print Receipt Logic matches Screenshot
        if (transaction.getTotalQuantity() > 0) {

            // Output summary similar to screenshot
            System.out.println("Models Received:");
            for(StockTransaction.StockItem item : transaction.getItems()) {
                System.out.println(" - " + item.getModelName() + " (Quantity: " + item.getQuantity() + ")");
            }
            System.out.println("Total Quantity: " + transaction.getTotalQuantity());
            System.out.println();

            // Save Data
            saveTransaction(transaction);
            saveInventory();
            String rFile = receiptGenerator.generateStockReceipt(transaction);

            // Success Messages
            System.out.println("Model quantities updated " + GREEN + "successfully" + RESET + ".");
            System.out.println(type + GREEN + " recorded" + RESET + ".");
            System.out.println("Receipt generated: receipts/" + rFile);

        } else {
            System.out.println("Transaction cancelled.");
        }
    }

    // ===== Helpers =====
    private boolean isValidOutlet(String code) {
        for (String o : OUTLETS) {
            if (o.equalsIgnoreCase(code)) return true;
        }
        return false;
    }

    private void saveTransaction(StockTransaction t) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANS_FILE, true))) {
            bw.write(t.toCSV());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveInventory() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MODELS_FILE))) {
            bw.write("Model,Dial Colour,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69");
            bw.newLine();
            for (Model m : inventory) {
                bw.write(m.toCSVRow());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}