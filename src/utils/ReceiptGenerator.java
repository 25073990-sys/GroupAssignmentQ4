package utils;

import models.Employee;
import models.Model;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import models.Sale;
import models.SaleItem;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    // Helper to get file name like "receipts_2025-10-13.txt" [cite: 116]
    private String getFileName() {
        return "receipts_" + DateUtils.getCurrentDate() + ".txt";
    }

    public void generateStockReceipt(String type, String from, String to, Model model, int qty, Employee emp) {
        String fileName = "receipts/" + getFileName(); // Saves in receipts folder

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) { // 'true' means append
            bw.write("=== STOCK TRANSACTION RECEIPT ===");
            bw.newLine();
            bw.write("Transaction: " + type); //
            bw.newLine();
            bw.write("Date: " + DateUtils.getCurrentDate());
            bw.newLine();
            bw.write("Time: " + DateUtils.getCurrentTime());
            bw.newLine();
            bw.write("From: " + from); // [cite: 99]
            bw.newLine();
            bw.write("To: " + to); // [cite: 100]
            bw.newLine();
            bw.write("Item: " + model.getModelName() + " (Qty: " + qty + ")"); // [cite: 101]
            bw.newLine();
            bw.write("Employee: " + emp.getName()); // [cite: 103]
            bw.newLine();
            bw.write("---------------------------------");
            bw.newLine();
            bw.newLine(); // Empty line for separation

            System.out.println("Receipt generated: " + fileName); // [cite: 116]

        } catch (IOException e) {
            System.out.println("Error generating receipt: " + e.getMessage());
        }
    }

        public static void createReceiptFile(Sale sale) {
            // Saves to the /receipts folder as per your framework
            String timestamp = sale.getTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
            String fileName = "receipts/receipt_" + timestamp + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("          GOLDEN HOUR STORE          ");
                writer.println("=====================================");
                writer.println("Date: " + sale.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                writer.println("Customer: " + sale.getCustomerName());
                writer.println("Outlet: " + sale.getOutletCode());
                writer.println("-------------------------------------");

                for (SaleItem item : sale.getItems()) {
                    writer.printf("%-18s x%-3d RM%8.2f%n",
                            item.getModelName(), item.getQuantity(), item.getSubtotal());
                }

                writer.println("-------------------------------------");
                writer.printf("TOTAL:                  RM%8.2f%n", sale.getTotal());
                writer.println("Payment Method: " + sale.getMethod());
                writer.println("Served by: " + sale.getEmployeeName());
                writer.println("=====================================");
                writer.println("       THANK YOU FOR YOUR VISIT      ");
            } catch (IOException e) {
                System.err.println("Failed to generate receipt: " + e.getMessage());
            }
        }
    }