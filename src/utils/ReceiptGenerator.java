package utils;

import models.StockTransaction;
import models.Sale;
import models.SaleItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    // Ensures we write to stock_YYYY-MM-DD.txt
    private String getStockFileName() {
        return "stock_" + DateUtils.getCurrentDate() + ".txt";
    }

    public String generateStockReceipt(StockTransaction txt) {
        new File("receipts").mkdirs();
        String fileName = getStockFileName();

        // "true" enables appending (so we don't overwrite previous records for the day)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("receipts/" + fileName, true))) {

            // Format matches the text-based receipt requirement
            bw.write("=== " + txt.getType() + " ===");
            bw.newLine();
            bw.write("Date: " + DateUtils.getCurrentDate());
            bw.newLine();
            bw.write("Time: " + DateUtils.getCurrentTime());
            bw.newLine();
            bw.write("From: " + txt.getFromOutlet());
            bw.newLine();
            bw.write("To:   " + txt.getToOutlet());
            bw.newLine();
            bw.write("Models Received:");
            bw.newLine();
            for (StockTransaction.StockItem item : txt.getItems()) {
                bw.write(" - " + item.getModelName() + " (Quantity: " + item.getQuantity() + ")");
                bw.newLine();
            }
            bw.write("Total Quantity: " + txt.getTotalQuantity());
            bw.newLine();
            bw.write("Served by: " + txt.getEmployeeName());
            bw.newLine();
            bw.write("---------------------------------");
            bw.newLine();
            bw.newLine();

            return fileName;

        } catch (IOException e) {
            return "Error";
        }
    }

    // This is your friend's existing code for Sales receipts
    // I included it here so you can copy the whole file safely.
    public static void createReceiptFile(Sale sale) {
        new File("receipts").mkdirs();
        String timestamp = sale.getTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String fileName = "receipts/sales_" + sale.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt";

        // Using FileWriter(path, true) to also append Sales if needed,
        // or just unique files as per your friend's logic.
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
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
            writer.println("Served by: " + sale.getEmployeeInCharge());
            writer.println("=====================================");
            writer.println("       THANK YOU FOR YOUR VISIT      ");
            writer.println();
            writer.println();
        } catch (IOException e) {
            System.err.println("Failed to generate receipt: " + e.getMessage());
        }
    }
}