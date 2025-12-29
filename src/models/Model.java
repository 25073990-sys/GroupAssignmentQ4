package models;

import java.util.HashMap;
import java.util.Map;

public class Model {

    private String modelName;
    private String dialColour;
    private double price;

    // Stock per outlet (C60, C61, ...)
    private Map<String, Integer> outletStocks;

    public Model(String modelName, String dialColour, double price) {
        this.modelName = modelName;
        this.dialColour = dialColour;
        this.price = price;
        this.outletStocks = new HashMap<>();
    }

    // ===== Stock methods (used by StockService & FileService) =====

    public int getStock(String outletCode) {
        return outletStocks.getOrDefault(outletCode, 0);
    }

    public void adjustStock(String outletCode, int quantity) {
        int current = getStock(outletCode);
        outletStocks.put(outletCode, current + quantity);
    }

    public void setStock(String outletCode, int quantity) {
        outletStocks.put(outletCode, quantity);
    }

    // ===== Getters =====
    public String getModelName() {
        return modelName;
    }

    public String getDialColour() {
        return dialColour;
    }

    public double getPrice() {
        return price;
    }

    public Map<String, Integer> getOutletStocks() {
        return outletStocks;
    }

    @Override
    public String toString() {
        return modelName + " (" + dialColour + ") - RM" + price;
    }
}
