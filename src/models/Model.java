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

    // ===== Stock methods =====

    public int getStock(String outletCode) {
        return outletStocks.getOrDefault(outletCode, 0);
    }

    // [FIX] This is the method EditService and SalesService were looking for
    public void setStock(String outletCode, int quantity) {
        outletStocks.put(outletCode, quantity);
    }

    public void adjustStock(String outletCode, int quantity) {
        int current = getStock(outletCode);
        outletStocks.put(outletCode, current + quantity);
    }

    public void addStock(String outletCode, int quantity) {
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

    public String toCSVRow() {
        String[] codes = {"C60", "C61", "C62", "C63", "C64", "C65", "C66", "C67", "C68", "C69"};
        StringBuilder sb = new StringBuilder();
        sb.append(modelName).append(",").append(dialColour).append(",").append(price);

        for (String code : codes) {
            sb.append(",").append(getStock(code));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return modelName + " (" + dialColour + ") - RM" + price;
    }
}