package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private String outletCode;
    private String customerName;
    private String method; // Payment method
    private String employeeName;
    private double total;
    private LocalDateTime timestamp;
    private List<SaleItem> items;

    //"Fresh Sale" for new transactions
    public Sale(String outletCode, String customerName, String method, String employeeName) {
        this.outletCode = outletCode;
        this.customerName = customerName;
        this.method = method;
        this.employeeName = employeeName;
        this.timestamp = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.total = 0.0;
    }
    //"Old sale" for ReportService
    public Sale(LocalDateTime timestamp, String outletCode, String customerName, String method,double total, String employeeName) {
        this.timestamp = timestamp;
        this.outletCode = outletCode;
        this.customerName = customerName;
        this.method = method;
        this.total = total;
        this.employeeName = employeeName;
        this.items = new ArrayList<>();
    }

    // Helper to add item easily
    public void addItem(String modelName, String color, int quantity, double price) {
        SaleItem item = new SaleItem(modelName, color, quantity, price);
        this.items.add(item);
        this.total += (price * quantity);
    }

    // ===== GETTERS =====
    public String getOutletCode() { return outletCode; }
    public String getCustomerName() { return customerName; }
    public String getMethod() { return method; }
    public String getEmployeeInCharge() { return employeeName; }
    public double getTotal() { return total; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<SaleItem> getItems() { return items; }

    // ===== SETTERS (Needed for EditService) =====
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public void setTotal(double total) {
        this.total = total;
    }
}