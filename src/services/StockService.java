package services;

import models.Model;
import models.Employee;
import models.StockTransaction;
import utils.ReceiptGenerator;
import utils.DateUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class StockService {
    private List<Model> inventory;
    private ReceiptGenerator receiptGenerator;
    private static final String MODELS_FILE = "data/models.csv";
    private static final String TRANS_FILE = "data/stock_transactions.csv";
    private static final String[] OUTLETS = {"C60","C61","C62","C63","C64","C65","C66","C67","C68","C69"};

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";

    public StockService(List<Model> inventory) {
        this.inventory = inventory;
        this.receiptGenerator = new ReceiptGenerator();
    }

    public void performStockCount(Scanner scanner) {
        System.out.println("\n=== Morning Stock Count ===");
        System.out.println("Date: " + DateUtils.getCurrentDate());
        System.out.println("Time: " + DateUtils.getCurrentTime());
        System.out.println();

        System.out.print("Enter Outlet Code (e.g., C60): ");
        String outlet = scanner.nextLine().toUpperCase().trim();
        if (!isValidOutlet(outlet)) { System.out.println(RED + "Invalid Outlet." + RESET); return; }

        int matches = 0, mismatches = 0, total = 0;

        for (Model watch : inventory) {
            int sysStock = watch.getStock(outlet);

            System.out.print("Model: " + watch.getModelName() + " - Counted: ");
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;
                int count = Integer.parseInt(input);
                total++;

                System.out.println("Store Record: " + sysStock);

                if (count == sysStock) {
                    System.out.println(GREEN + "Stock tally correct." + RESET);
                    matches++;
                } else {
                    int diff = Math.abs(count - sysStock);
                    System.out.println(RED + "! Mismatch detected (" + diff + " unit difference)" + RESET);
                    mismatches++;
                }
                System.out.println();
            } catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        System.out.println("// (Repeat for each model)");
        System.out.println("Total Models Checked: " + total);
        System.out.println("Tally " + GREEN + "Correct" + RESET + ": " + matches);
        System.out.println(RED + "Mismatches" + RESET + ": " + mismatches);

        System.out.print("Morning stock count " + GREEN + "completed" + RESET + ".\n");
        if (mismatches > 0) {
            System.out.println(RED + "Warning: Please verify stock." + RESET);
        }
    }

    public void processStockTransfer(Scanner scanner, Employee user, boolean isStockIn) {
        String typeTitle = isStockIn ? "Stock In" : "Stock Out";
        System.out.println("\n=== " + typeTitle + " ===");
        System.out.println("Date: " + DateUtils.getCurrentDate());
        System.out.println("Time: " + DateUtils.getCurrentTime());

        System.out.print("From: "); String fromInput = scanner.nextLine().trim();
        System.out.print("To: "); String toInput = scanner.nextLine().trim();

        boolean fromValid = isValidOutlet(fromInput) || (isStockIn && fromInput.equalsIgnoreCase("HQ"));
        boolean toValid = isValidOutlet(toInput);

        if (!fromValid || !toValid) {
            System.out.println(RED + "Error: Invalid Outlet Code." + RESET);
            return;
        }

        System.out.println("From: " + getDisplayName(fromInput));
        System.out.println("To: " + getDisplayName(toInput));

        StockTransaction trans = new StockTransaction(typeTitle, fromInput.toUpperCase(), toInput.toUpperCase(), user.getName());
        boolean adding = true;

        System.out.println("Enter Models to add:");

        while (adding) {
            System.out.print("- Model Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) break;

            Model selected = inventory.stream()
                    .filter(m -> m.getModelName().equalsIgnoreCase(name))
                    .findFirst().orElse(null);

            if (selected == null) {
                System.out.println("  " + RED + "Error: Model not found." + RESET);
                continue;
            }

            System.out.print("  Quantity: ");
            try {
                int qty = Integer.parseInt(scanner.nextLine().trim());

                if (!fromInput.equalsIgnoreCase("HQ") && selected.getStock(fromInput) < qty) {
                    System.out.println("  " + RED + "Error: Insufficient stock." + RESET);
                    continue;
                }

                selected.adjustStock(fromInput, -qty);
                selected.adjustStock(toInput, qty);
                trans.addItem(selected.getModelName(), qty);

            } catch (Exception e) { System.out.println("  Invalid quantity."); }

            System.out.print("  Add another? (y/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) adding = false;
        }

        if (trans.getTotalQuantity() > 0) {
            saveTransactionToCSV(trans);
            saveInventoryToFile();
            String fName = receiptGenerator.generateStockReceipt(trans);

            System.out.println("\nModels Received:");
            for (StockTransaction.StockItem i : trans.getItems()) {
                System.out.println(" - " + i.getModelName() + " (Quantity: " + i.getQuantity() + ")");
            }
            System.out.println("Total Quantity: " + trans.getTotalQuantity());
            System.out.println();
            System.out.println("Model quantities updated " + GREEN + "successfully" + RESET + ".");
            System.out.println(typeTitle + " " + GREEN + "recorded" + RESET + ".");
            System.out.println("Receipt generated: " + fName);
        }
    }

    private String getDisplayName(String code) {
        if (code.equalsIgnoreCase("HQ")) return "HQ (Service Center)";
        if (code.equalsIgnoreCase("C60")) return "C60 (Kuala Lumpur City Centre)";
        return code.toUpperCase();
    }

    private boolean isValidOutlet(String s) {
        for (String o : OUTLETS) if (o.equalsIgnoreCase(s)) return true; return false;
    }

    private void saveTransactionToCSV(StockTransaction t) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANS_FILE, true))) {
            bw.write(t.toCSV()); bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveInventoryToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MODELS_FILE))) {
            bw.write("Model,Dial Colour,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69"); bw.newLine();
            for (Model m : inventory) {
                StringBuilder sb = new StringBuilder();
                sb.append(m.getModelName()).append(",").append(m.getDialColor()).append(",").append(m.getPrice());
                for (String o : OUTLETS) sb.append(",").append(m.getStock(o));
                bw.write(sb.toString()); bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}