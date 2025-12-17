package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

public class ReceiptGenerator {
    private static final String RECEIPT_PATH = "receipts/stock_";

    public static void appendStockReceipt(String details) {
        String fileName = RECEIPT_PATH + LocalDate.now().toString() + ".txt";

        try (FileWriter fw = new FileWriter(fileName, true);
             PrintWriter out = new PrintWriter(fw)) {
            out.println("-------------------------------------------");
            out.println(details);
            out.println("-------------------------------------------");
            System.out.println("Successfully added to daily receipt: " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing receipt: " + e.getMessage());
        }
    }
}