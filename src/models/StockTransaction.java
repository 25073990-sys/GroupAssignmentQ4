package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StockTransaction {
    private String transactionId;
    private String type;
    private LocalDateTime timestamp;
    private String fromOutlet;
    private String toOutlet;
    private String employeeName;
    private List<StockItem> items;

    public static class StockItem {
        private String modelName;
        private int quantity;
        public StockItem(String modelName, int qty) { this.modelName = modelName; this.quantity = qty; }
        public String getModelName() { return modelName; }
        public int getQuantity() { return quantity; }
    }

    public StockTransaction(String type, String from, String to, String empName) {
        this.timestamp = LocalDateTime.now();
        this.transactionId = "ST" + timestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.type = type;
        this.fromOutlet = from;
        this.toOutlet = to;
        this.employeeName = empName;
        this.items = new ArrayList<>();
    }

    public void addItem(String model, int qty) { items.add(new StockItem(model, qty)); }

    public String getType() { return type; }
    public String getFromOutlet() { return fromOutlet; }
    public String getToOutlet() { return toOutlet; }
    public String getEmployeeName() { return employeeName; }
    public List<StockItem> getItems() { return items; }
    public int getTotalQuantity() { return items.stream().mapToInt(StockItem::getQuantity).sum(); }

    public String toCSV() {
        String details = items.stream().map(i -> i.getModelName() + ":" + i.getQuantity()).collect(Collectors.joining(";"));
        return String.join(",", transactionId, type, timestamp.toString(), fromOutlet, toOutlet, employeeName, String.valueOf(getTotalQuantity()), details);
    }
}