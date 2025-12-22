package models;

public class Model {
    private String modelName; // e.g., DW2300-1
    private double price;
    private int stockCount;   // Current stock in YOUR store

    public Model(String modelName, double price, int stockCount) {
        this.modelName = modelName;
        this.price = price;
        this.stockCount = stockCount;
    }

    // Getters and Setters
    public String getModelName() { return modelName; }
    public double getPrice() { return price; }
    public int getStockCount() { return stockCount; }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    // Helper to add/remove stock easily
    public void adjustStock(int quantity) {
        this.stockCount += quantity;
    }

    @Override
    public String toString() {
        return modelName + "," + price + "," + stockCount;
    }
}