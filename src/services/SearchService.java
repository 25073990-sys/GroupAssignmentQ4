package services;

import models.Model;
import models.Sale;
import models.SaleItem;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SearchService {

    private List<Model> inventory;
    private List<Sale> salesHistory;

    // ANSI Colors for the screenshot look
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    // Mapping codes to real names to match your screenshot
    private static final Map<String, String> OUTLET_NAMES = new HashMap<>();
    static {
        OUTLET_NAMES.put("C60", "KLCC");
        OUTLET_NAMES.put("C61", "MidValley");
        OUTLET_NAMES.put("C62", "Lalaport");
        OUTLET_NAMES.put("C63", "KL East");
        OUTLET_NAMES.put("C64", "Nu Sentral");
        OUTLET_NAMES.put("C65", "Pavillion KL");
        OUTLET_NAMES.put("C66", "MyTown");
        OUTLET_NAMES.put("C67", "Sunway Pyramid");
        OUTLET_NAMES.put("C68", "1 Utama");
        OUTLET_NAMES.put("C69", "IOI City");
    }

    public SearchService(List<Model> inventory, List<Sale> salesHistory) {
        this.inventory = inventory;
        this.salesHistory = salesHistory;
    }

    // ==========================================
    // 1. STOCK SEARCH
    // ==========================================
    public void searchStock(Scanner scanner) {
        System.out.println("\n=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String keyword = scanner.nextLine().trim();

        System.out.println(GREEN + "Searching..." + RESET);
        System.out.println(); // Empty line

        Model found = inventory.stream()
                .filter(m -> m.getModelName().equalsIgnoreCase(keyword))
                .findFirst()
                .orElse(null);

        if (found != null) {
            System.out.println("Model: " + found.getModelName());
            System.out.printf("Unit Price: RM%.0f%n", found.getPrice());
            System.out.println();
            System.out.println("Stock by Outlet:");

            // Display in a grid-like format (similar to screenshot)
            int count = 0;
            String[] outletCodes = {"C60", "C61", "C62", "C63", "C64", "C65", "C66", "C67", "C68", "C69"};

            for (String code : outletCodes) {
                String name = OUTLET_NAMES.getOrDefault(code, code);
                int qty = found.getStock(code);

                // Format: "Name: Qty   "
                System.out.printf("%-12s: %-3d  ", name, qty);

                count++;
                if (count % 4 == 0) System.out.println(); // New line every 4 items
            }
            System.out.println();

        } else {
            System.out.println(RED + "Model not found." + RESET);
        }
    }

    // ==========================================
    // 2. SALES SEARCH
    // ==========================================
    public void searchSales(Scanner scanner) {
        System.out.println("\n=== Search Sales Information ===");
        System.out.print("Search keyword: ");
        String keyword = scanner.nextLine().trim().toLowerCase();

        System.out.println(GREEN + "Searching..." + RESET);
        System.out.println();

        boolean foundAny = false;

        // Search in sales history
        if (salesHistory != null) {
            for (Sale sale : salesHistory) {
                // Check Date, Customer Name, or Items
                boolean match = sale.getCustomerName().toLowerCase().contains(keyword) ||
                        sale.getTimestamp().toString().contains(keyword);

                if (!match) {
                    for (SaleItem item : sale.getItems()) {
                        if (item.getModelName().toLowerCase().contains(keyword)) {
                            match = true;
                            break;
                        }
                    }
                }

                if (match) {
                    printSaleRecord(sale);
                    foundAny = true;
                }
            }
        }

        if (!foundAny) {
            System.out.println(RED + "No sales records found matching '" + keyword + "'." + RESET);
        }
    }

    private void printSaleRecord(Sale sale) {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");

        System.out.println("Sales Record Found:");
        System.out.println("Date: " + sale.getTimestamp().format(dateFmt) + "      Time: " + sale.getTimestamp().format(timeFmt));
        System.out.println("Customer: " + sale.getCustomerName());

        System.out.print("Item(s): ");
        for (SaleItem item : sale.getItems()) {
            System.out.print(item.getModelName() + " ");
        }
        System.out.println("   Quantity: " + sale.getItems().stream().mapToInt(SaleItem::getQuantity).sum());

        System.out.printf("Total: RM%.0f%n", sale.getTotal());
        System.out.println("Transaction Method: " + sale.getMethod());
        System.out.println("Employee: " + sale.getEmployeeInCharge());
        System.out.println("Status: Transaction verified.");
        System.out.println("-----------------------------------");
    }
}