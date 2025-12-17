package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockTransaction {
    private String transactionId;
    private String type; // "IN", "OUT", or "ADJUSTMENT"
    private String modelName;
    private int quantity;
    private String source;
    private String destination;
    private String timestamp;
    private String employeeId;

    public StockTransaction(String transactionId, String type, String modelName,
                            int quantity, String source, String destination, String employeeId) {
        this.transactionId = transactionId;
        this.type = type;
        this.modelName = modelName;
        this.quantity = quantity;
        this.source = source;
        this.destination = destination;
        this.employeeId = employeeId;
        // Auto-generate timestamp
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Convert object to a CSV line
    public String toCSV() {
        return String.join(",", transactionId, type, modelName, String.valueOf(quantity),
                source, destination, timestamp, employeeId);
    }
}