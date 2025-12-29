package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private String customerName;
    private String employeeInCharge;
    private String method; // Cash, Card, etc.
    private LocalDateTime timestamp;
    private List<SaleItem> items;

    // Inner class for items in the cart
    public static class SaleItem {
        private String modelName;
        private int quantity;
        private double unitPrice;

        public SaleItem(String modelName, int quantity, double unitPrice) {
            this.modelName = modelName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getModelName() { return modelName; }
        public int getQuantity() { return quantity; }

        public double getSubtotal() {
            return quantity * unitPrice;
        }
    }

    public Sale(String customerName, String employeeInCharge, String method) {
        this.customerName = customerName;
        this.employeeInCharge = employeeInCharge;
        this.method = method;
        this.timestamp = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public void addItem(String model, int qty, double price) {
        items.add(new SaleItem(model, qty, price));
    }

    public double getTotal() {
        return items.stream().mapToDouble(SaleItem::getSubtotal).sum();
    }

    // Getters
    public String getCustomerName() { return customerName; }
    public String getMethod() { return method; }
    public String getEmployeeInCharge() { return employeeInCharge; }
    public List<SaleItem> getItems() { return items; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Date Helpers
    public String getDateStr() { return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); }
    public String getTimeStr() { return timestamp.format(DateTimeFormatter.ofPattern("hh:mm a")); }
}