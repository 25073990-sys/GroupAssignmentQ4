package utils;

import models.StockTransaction;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import models.Sale;
import models.SaleItem;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    private String getFileName() {
        return "receipts_" + DateUtils.getCurrentDate() + ".txt";
    }

    public String generateStockReceipt(StockTransaction txt) {
        new File("receipts").mkdirs();
        String fileName = getFileName();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("receipts/" + fileName, true))) {
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