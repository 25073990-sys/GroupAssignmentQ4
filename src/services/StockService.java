package services;

import models.StockTransaction;
import utils.ReceiptGenerator;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class StockService {
    private static final String DB_PATH = "data/stock_transactions.csv";

    public void recordMovement(String type, String model, int qty, String src, String dest, String empId) {
        // 1. Create the Transaction object
        String id = UUID.randomUUID().toString().substring(0, 5); // Short ID
        StockTransaction tx = new StockTransaction(id, type, model, qty, src, dest, empId);

        // 2. Save to CSV (The Database)
        try (FileWriter fw = new FileWriter(DB_PATH, true);
             PrintWriter out = new PrintWriter(fw)) {
            out.println(tx.toCSV());
        } catch (IOException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        // 3. Generate the Text Receipt
        String receiptText = String.format("TX ID: %s\nTYPE: %s\nMODEL: %s\nQTY: %d\nFROM: %s\nTO: %s\nBY EMP: %s",
                id, type, model, qty, src, dest, empId);
        ReceiptGenerator.appendStockReceipt(receiptText);
    }
}