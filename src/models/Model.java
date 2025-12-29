package models;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private String modelCode;
    private String dialColor;
    private double price;
    private Map<String, Integer> outletStock = new HashMap<>();

    public Model(String modelCode, String dialColor, double price) {
        this.modelCode = modelCode;
        this.dialColor = dialColor;
        this.price = price;
    }

    public void setStock(String outlet, int quantity) {
        outletStock.put(outlet.toUpperCase(), quantity);
    }

    public int getStock(String outlet) {
        return outletStock.getOrDefault(outlet.toUpperCase(), 0);
    }

    public void adjustStock(String outlet, int adjustment) {
        String key = outlet.toUpperCase();
        // HQ (Service Center) has infinite stock, so we don't track it in our CSV
        if (key.equals("HQ")) return;

        int current = getStock(key);
        outletStock.put(key, current + adjustment);
    }

    public String getModelName() { return modelCode; }
    public String getDialColor() { return dialColor; }
    public double getPrice() { return price; }
}