package services;

import models.Employee;
import models.Model;
import models.Sale;
import models.SaleItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class SalesService {

    private List<Model> inventory;
    private List<Sale> salesHistory;

    // Formatting tools
    private static final DateTimeFormatter FILE_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    public SalesService(List<Model> inventory, List<Sale> salesHistory) {
        this.inventory = inventory;
        this.salesHistory = salesHistory;
    }

    public void recordNewSale(Scanner scanner, Employee currentUser) {
        System.out.println("\n=== Record New Sale ===");

        LocalDateTime now = LocalDateTime.now();
        System.out.println("Date: " + now.toLocalDate());
        System.out.println("Time: " + now.format(DISPLAY_TIME_FMT));

        // 1. Get Customer Details
        System.out.print("Customer Name: ");
        String customerName = scanner.nextLine();

        // 2. Identify Outlet
        String outletCode = currentUser.getId().length() >= 3 ? currentUser.getId().substring(0, 3) : "Invalid outlet code";

        // Create the Sale Object
        // Using getName() or getEmployeeInCharge() depending on what's available
        Sale newSale = new Sale(now,outletCode, customerName,"Pending",0.0,currentUser.getId());

        // 3. Add Items Loop
        boolean addingItems = true;

        while (addingItems) {
            System.out.println("\n--- Add Item (Type 'DONE' to finish) ---");
            System.out.print("Enter Model: ");
            String modelInput = scanner.nextLine().trim();

            if (modelInput.equalsIgnoreCase("DONE")) {
                addingItems = false;
                break;
            }

            // Find model in inventory
            Model product = inventory.stream()
                    .filter(m -> m.getModelName().equalsIgnoreCase(modelInput))
                    .findFirst()
                    .orElse(null);

            if (product == null) {
                System.out.println("Error: Model not found.");
                System.out.print("Try again? (Y to retry, N to stop adding): ");
                String retry = scanner.nextLine();
                if (retry.equalsIgnoreCase("N")) addingItems = false;
                continue;
            }

            System.out.print("Enter Quantity: ");
            int qty = 0;
            try {
                String qtyInput = scanner.nextLine();
                qty = Integer.parseInt(qtyInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format.");
                continue;
            }

            // Check Stock Availability
            int currentStock = product.getStock(outletCode);
            if (qty > currentStock) {
                System.out.println("Error: Insufficient stock. Current stock at " + outletCode + ": " + currentStock);
                System.out.print("Try again? (Y to retry, N to stop adding): ");
                String retry = scanner.nextLine();
                if (retry.equalsIgnoreCase("N")) addingItems = false;
                continue;
            }

            System.out.println("Unit Price: RM" + product.getPrice());

            // Add to Sale Object
            newSale.addItem(product.getModelName(), "Standard", qty, product.getPrice());

            // DEDUCT STOCK IMMEDIATELY
            product.setStock(outletCode, currentStock - qty);
            System.out.println("Item added to cart.");

            System.out.print("Add another item? (Y/N): ");
            String more = scanner.nextLine();
            if (!more.equalsIgnoreCase("Y")) {
                addingItems = false;
            }
        }

        // 4. Finalize Transaction (Check if empty)
        if (newSale.getItems().isEmpty()) {
            System.out.println("\nTransaction cancelled: No items added.");
            return;
        }

        System.out.print("\nEnter transaction method: ");
        String method = scanner.nextLine();
        newSale.setMethod(method);

        System.out.printf("Subtotal: RM%.0f%n", newSale.getTotal());

        // 5. Save & Generate Receipt
        salesHistory.add(newSale);
        saveReceipt(newSale);
        FileService.saveSaleToCSV(newSale);
        FileService.saveModels(inventory);

        System.out.println("\nTransaction \u001B[32msuccessful\u001B[0m."); // Green text
        System.out.println("Sale recorded \u001B[32msuccessfully\u001B[0m.");
        System.out.println("Model quantities updated \u001B[32msuccessfully\u001B[0m.");
        System.out.println("Receipt generated: receipts/sales_" + now.format(FILE_DATE_FMT) + ".txt");
    }

    private void saveReceipt(Sale sale) {
        String dateStr = LocalDateTime.now().format(FILE_DATE_FMT);
        String fileName = "receipts/sales_" + dateStr + ".txt";

        // Ensure directory exists
        File directory = new File("receipts");
        if (!directory.exists()) {
            directory.mkdir();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("========================================");
            writer.newLine();
            writer.write("GOLDEN HOUR RECEIPT");
            writer.newLine();
            writer.write("Salesperson: " + sale.getEmployeeInCharge());
            writer.newLine();
            writer.write("Customer: " + sale.getCustomerName());
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();
            for (SaleItem item : sale.getItems()) {
                writer.write(String.format("%-15s x%d  RM%.2f", item.getModelName(), item.getQuantity(), (item.getPrice() * item.getQuantity())));
                writer.newLine();
            }
            writer.write("----------------------------------------");
            writer.newLine();
            writer.write("Total: RM" + sale.getTotal());
            writer.newLine();
            writer.write("Method: " + sale.getMethod());
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine(); // Empty line for next record
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }
}