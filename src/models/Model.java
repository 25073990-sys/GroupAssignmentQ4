package models;

public class Model {
    private String modelName;
    private String dialcolour;// e.g., DW2300-1
    private double price;
    private int [] outletStocks = new int [10]; //Index 0 = C60, 1=C61...

    public Model(String modelName,String dialcolour, double price) {
        this.modelName = modelName;
        this.dialcolour = dialcolour;
        this.price = price;
    }

    // Helper method to convert "C60" into index 0, "C61" into index 1, etc.
    private int getIndexFromCode(String code) {
        // Extracts the number from "C60", "C61", etc., and subtracts 60
        // C60 -> 60 - 60 = index 0
        // C69 -> 69 - 60 = index 9
        return Integer.parseInt(code.substring(1)) - 60;
    }

    public void addStock(String outletCode, int quantity) {
        int index = getIndexFromCode(outletCode);
        outletStocks[index] = quantity;
    }

    public int getStockForOutlet(String outletCode) {
        return outletStocks[getIndexFromCode(outletCode)];
    }

    public void updateStock(String outletCode, int change) {
        int index = getIndexFromCode(outletCode);
        this.outletStocks[index] += change;
    }

    public String toCSVRow() {
        StringBuilder sb = new StringBuilder();
        sb.append(modelName).append(",").append(dialcolour).append(",").append(price);
        for (int stock : outletStocks) {
            sb.append(",").append(stock);
        }
        return sb.toString();
    }

    // Standard Getters
    public String getName() { return modelName; }
    public String getColour() { return dialcolour; }
    public double getPrice() { return price; }
}