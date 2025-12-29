package models;

import java.time.LocalDateTime;
import java.util.*;

public class Sale {
    private String customerName;
    private String employeeInCharge;
    private String outletCode; // Added this field
    private String method;
    private LocalDateTime timestamp;
    private List<SaleItem> items;

    public Sale(String customerName, String employeeInCharge, String outletCode, String method) {
        this.customerName = customerName;
        this.employeeInCharge = employeeInCharge;
        this.outletCode = outletCode; // Now required to know which column to deduct stock from
        this.method = method;
        this.timestamp = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public void addItem(String model, String colour, int qty, double price) {
        items.add(new SaleItem(model, colour, qty, price));
    }

    // Getters
    public String getOutletCode() { return outletCode; }
    public String getCustomerName() { return customerName; }
    public String getEmployeeInCharge() { return employeeInCharge; }
    public String getMethod() { return method; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<SaleItem> getItems() { return items; }

    public double getTotal() {
        return items.stream().mapToDouble(SaleItem::getSubtotal).sum();
    }
}