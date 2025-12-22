package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StockTransaction {
    private String transactionId;
    private String transactionType; // "STOCK_IN" or "STOCK_OUT" or "MORNING_COUNT" or "NIGHT_COUNT"
    private LocalDateTime dateTime;
    private String fromOutlet;
    private String toOutlet;
    private List<StockItem> items;
    private String employeeId;
    private String employeeName;
    private int totalQuantity;
    private String notes;

    // Inner class for stock items
    public static class StockItem {
        private String modelCode;
        private String modelName;
        private int quantity;
        private String status; // "MATCH" or "MISMATCH" for stock counts
        private int storedQuantity; // For stock count comparison

        public StockItem(String modelCode, String modelName, int quantity) {
            this.modelCode = modelCode;
            this.modelName = modelName;
            this.quantity = quantity;
        }

        // Getters and Setters
        public String getModelCode() { return modelCode; }
        public void setModelCode(String modelCode) { this.modelCode = modelCode; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public int getStoredQuantity() { return storedQuantity; }
        public void setStoredQuantity(int storedQuantity) { this.storedQuantity = storedQuantity; }
    }

    // Constructor
    public StockTransaction(String transactionType, String fromOutlet, String toOutlet,
                            String employeeId, String employeeName) {
        this.transactionId = generateTransactionId();
        this.transactionType = transactionType;
        this.dateTime = LocalDateTime.now();
        this.fromOutlet = fromOutlet;
        this.toOutlet = toOutlet;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.items = new ArrayList<>();
        this.totalQuantity = 0;
    }

    // Generate unique transaction ID
    private String generateTransactionId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "ST" + LocalDateTime.now().format(formatter);
    }

    // Add item to transaction
    public void addItem(String modelCode, String modelName, int quantity) {
        StockItem item = new StockItem(modelCode, modelName, quantity);
        items.add(item);
        totalQuantity += quantity;
    }

    // Add item with stock count comparison
    public void addItemWithCount(String modelCode, String modelName, int counted, int stored) {
        StockItem item = new StockItem(modelCode, modelName, counted);
        item.setStoredQuantity(stored);
        item.setStatus(counted == stored ? "MATCH" : "MISMATCH");
        items.add(item);
    }

    // Calculate total quantity
    public void calculateTotalQuantity() {
        totalQuantity = 0;
        for (StockItem item : items) {
            totalQuantity += item.getQuantity();
        }
    }

    // Get formatted date and time
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return dateTime.format(formatter);
    }

    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        return dateTime.format(formatter);
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getFromOutlet() { return fromOutlet; }
    public void setFromOutlet(String fromOutlet) { this.fromOutlet = fromOutlet; }

    public String getToOutlet() { return toOutlet; }
    public void setToOutlet(String toOutlet) { this.toOutlet = toOutlet; }

    public List<StockItem> getItems() { return items; }
    public void setItems(List<StockItem> items) { this.items = items; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Convert to CSV format for saving
    public String toCSV() {
        StringBuilder itemsStr = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            StockItem item = items.get(i);
            itemsStr.append(item.getModelCode()).append(":").append(item.getQuantity());
            if (i < items.size() - 1) {
                itemsStr.append(";");
            }
        }

        return String.join(",",
                transactionId,
                transactionType,
                getFormattedDateTime(),
                fromOutlet,
                toOutlet,
                itemsStr.toString(),
                employeeId,
                employeeName,
                String.valueOf(totalQuantity),
                notes != null ? notes : ""
        );
    }

    @Override
    public String toString() {
        return "StockTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", type='" + transactionType + '\'' +
                ", dateTime=" + getFormattedDateTime() +
                ", from='" + fromOutlet + '\'' +
                ", to='" + toOutlet + '\'' +
                ", totalQuantity=" + totalQuantity +
                '}';
    }
}