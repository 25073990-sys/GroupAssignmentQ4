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

public class StockService {

    private List<Model> inventory;
    private ReceiptGenerator receiptGenerator;

    private static final String MODELS_FILE = "data/models.csv";
    private static final String TRANS_FILE = "data/stock_transactions.csv";
    private static final String[] OUTLETS =
            {"C60","C61","C62","C63","C64","C65","C66","C67","C68","C69"};

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";

    public StockService(List<Model> inventory) {
        this.inventory = inventory;
        this.receiptGenerator = new ReceiptGenerator();
    }

    // ===== FEATURE 1: Morning / Night Stock Count =====
    public void performStockCount(Scanner scanner) {

        System.out.println("\n=== Morning Stock Count ===");
        System.out.println("Date: " + DateUtils.getCurrentDate());
        System.out.println("Time: " + DateUtils.getCurrentTime());

        System.out.print("Enter Outlet Code (e.g., C60): ");
        String outlet = scanner.nextLine().trim().toUpperCase();

        if (!isValidOutlet(outlet)) {
            System.out.println(RED + "Invalid Outlet Code." + RESET);
            return;
        }

        int matches = 0, mismatches = 0;

        for (Model m : inventory) {
            int systemStock = m.getStock(outlet);

            System.out.print("Model: " + m.getModelName() + " | Counted: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            try {
                int counted = Integer.parseInt(input);
                System.out.println("System Record: " + systemStock);

                if (counted == systemStock) {
                    System.out.println(GREEN + "Stock tally correct." + RESET);
                    matches++;
                } else {
                    System.out.println(RED + "Mismatch detected." + RESET);
                    mismatches++;
                }
                System.out.println();
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }

        System.out.println("Tally Correct: " + matches);
        System.out.println("Mismatches: " + mismatches);

        if (mismatches > 0) {
            System.out.println(RED + "Warning: Please verify stock." + RESET);
        }
    }

    // ===== FEATURE 2: Stock Transfer =====
    public void processStockTransfer(Scanner scanner, Employee user, boolean isStockIn) {

        String type = isStockIn ? "Stock In" : "Stock Out";

        System.out.println("\n=== " + type + " ===");
        System.out.println("Date: " + DateUtils.getCurrentDate());
        System.out.println("Time: " + DateUtils.getCurrentTime());

        System.out.print("From (Outlet / HQ): ");
        String from = scanner.nextLine().trim().toUpperCase();

        System.out.print("To (Outlet): ");
        String to = scanner.nextLine().trim().toUpperCase();

        if (!isValidOutlet(to) || (!isStockIn && !isValidOutlet(from))) {
            System.out.println(RED + "Invalid outlet code." + RESET);
            return;
        }

        StockTransaction transaction =
                new StockTransaction(type, from, to, user.getName());

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

                if (!from.equals("HQ") && model.getStock(from) < qty) {
                    System.out.println(RED + "Insufficient stock." + RESET);
                    continue;
                }

                model.adjustStock(from, -qty);
                model.adjustStock(to, qty);
                transaction.addItem(model.getModelName(), qty);

            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
            }
        }

        if (transaction.getTotalQuantity() > 0) {
            saveTransaction(transaction);
            saveInventory();
            receiptGenerator.generateStockReceipt(transaction);

            System.out.println(GREEN + "Stock updated successfully." + RESET);
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
                StringBuilder sb = new StringBuilder();
                sb.append(m.getModelName()).append(",")
                        .append(m.getDialColour()).append(",")
                        .append(m.getPrice());

                for (String o : OUTLETS) {
                    sb.append(",").append(m.getStock(o));
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

