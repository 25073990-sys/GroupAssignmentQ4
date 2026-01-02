package models;

public class SaleItem {
    private String modelName;
    private String color;
    private int quantity;
    private double price;

    public SaleItem(String modelName, String color, int quantity, double price) {
        this.modelName = modelName;
        this.color = color;
        this.quantity = quantity;
        this.price = price;
    }

    // ===== GETTERS =====
    public String getModelName() { return modelName; }
    public String getColor() { return color; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // [FIXED] Matches what ReceiptGenerator is looking for
    public double getSubtotal() {
        return this.price * this.quantity;
    }

    // ===== SETTERS (Needed for EditService) =====
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}