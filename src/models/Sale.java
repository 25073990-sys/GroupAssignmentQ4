package models;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    /**
     * Project: GoldenHour Store Operations Management System
     * Module: Sales System
     * Student Name: Sarah (as per reference)
     */
    public class sales_system {

        private Scanner scanner = new Scanner(System.in);
        private static final String SALES_CSV = "sales.csv"; // File for auto-generated history [cite: 200]

        // Line 21: Added main method for execution
        public static void main(String[] args) {
            sales_system system = new sales_system();

            // This simulates the logged-in employee requirement [cite: 103, 120]
            String currentEmployee = "Tan Guan Han (C60)";

            system.recordNewSale(currentEmployee);
        }

        // Represents individual items in a sale [cite: 123]
        class SaleItem {
            String modelName;
            int quantity;
            double unitPrice;

            SaleItem(String modelName, int quantity, double unitPrice) {
                this.modelName = modelName;
                this.quantity = quantity;
                this.unitPrice = unitPrice;
            }

            double getSubtotal() {
                return quantity * unitPrice;
            }
        }

        // Represents a full Sale transaction [cite: 126]
        class Sale {
            String customerName;
            String employeeInCharge;
            String method;
            LocalDateTime timestamp;
            List<SaleItem> items = new ArrayList<>();

            Sale(String customerName, String employeeInCharge, String method) {
                this.customerName = customerName;
                this.employeeInCharge = employeeInCharge;
                this.method = method;
                this.timestamp = LocalDateTime.now(); // Automatic capture [cite: 120]
            }

            double getTotal() {
                double total = 0;
                for (SaleItem item : items) total += item.getSubtotal();
                return total;
            }
        }

        // --- CORE SYSTEM METHODS ---

        /**
         * Main method to record a sale [cite: 118]
         * @param currentEmployee The ID/Name of the logged-in employee [cite: 120]
         */
        public void recordNewSale(String currentEmployee) {
            System.out.println("\n=== Record New Sale ===");

            // Automatic display of date and time [cite: 120, 130, 131]
            LocalDateTime now = LocalDateTime.now();
            System.out.println("Date: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            System.out.println("Time: " + now.format(DateTimeFormatter.ofPattern("hh:mm a")));

            // Input Customer Name [cite: 122, 132]
            System.out.print("Customer Name: ");
            String customer = scanner.nextLine();

            // Initialize Sale Object
            Sale currentSale = new Sale(customer, currentEmployee, "");

            // Loop to add multiple items [cite: 123, 137]
            boolean adding = true;
            while (adding) {
                System.out.print("Enter Model: ");
                String model = scanner.nextLine();
                System.out.print("Enter Quantity: ");
                int qty = Integer.parseInt(scanner.nextLine());
                System.out.print("Unit Price: RM");
                double price = Double.parseDouble(scanner.nextLine());

                currentSale.items.add(new SaleItem(model, qty, price));

                // REMARK: In your full system, you should call your Stock module here
                // to decrease the inventory based on 'qty'[cite: 126, 141].

                System.out.print("Are there more items purchased? (Y/N): ");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("N")) adding = false;
            }

            // Input Transaction Method [cite: 124, 138]
            System.out.print("Enter transaction method (Cash/Card/E-wallet): ");
            currentSale.method = scanner.nextLine();

            System.out.println("Subtotal: RM" + currentSale.getTotal());

            // Process finalization
            generateTextReceipt(currentSale);
            saveToSalesHistory(currentSale);

            System.out.println("Transaction successful. Sale recorded successfully.");
            System.out.println("Model quantities updated successfully."); // Simulated [cite: 141]
        }

        /**
         * Requirement: Generate text-based receipt saved by date [cite: 126, 127]
         */
        private void generateTextReceipt(Sale sale) {
            String dateStr = sale.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fileName = "sales_" + dateStr + ".txt"; // Example: sales_2025-10-13.txt [cite: 142]

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
                writer.println("=== SALE RECEIPT ===");
                writer.println("Date: " + dateStr);
                writer.println("Time: " + sale.timestamp.format(DateTimeFormatter.ofPattern("hh:mm a")));
                writer.println("Customer: " + sale.customerName);
                writer.println("Items Purchased:");
                for (SaleItem item : sale.items) {
                    writer.println("- " + item.modelName + " (Qty: " + item.quantity + ") RM" + item.getSubtotal());
                }
                writer.println("Total Price: RM" + sale.getTotal());
                writer.println("Method: " + sale.method);
                writer.println("Employee: " + sale.employeeInCharge);
                writer.println("-----------------------------------\n");
                System.out.println("Receipt generated: " + fileName); //[cite: 142]
            } catch (IOException e) {
                System.out.println("Error generating receipt: " + e.getMessage()); //[cite: 16]
            }
        }

        /**
         * Requirement: Append sales data to a CSV for permanent storage [cite: 200, 201]
         */
        private void saveToSalesHistory(Sale sale) {
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(SALES_CSV, true)))) {
                // Format: Date,Time,Customer,Total,Method,Employee
                StringBuilder sb = new StringBuilder();
                sb.append(sale.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(",");
                sb.append(sale.timestamp.format(DateTimeFormatter.ofPattern("HH:mm"))).append(",");
                sb.append(sale.customerName).append(",");
                sb.append(sale.getTotal()).append(",");
                sb.append(sale.method).append(",");
                sb.append(sale.employeeInCharge);

                pw.println(sb.toString());
            } catch (IOException e) {
                System.out.println("Error saving to sales history."); //[cite: 16]
            }
        }
    }
}
