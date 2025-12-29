package utils;

import models.StockTransaction;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
}