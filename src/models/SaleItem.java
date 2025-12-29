package models;

public class SaleItem {
        private String modelName;
        private String colour; // Added this
        private int quantity;
        private double unitPrice;

        public SaleItem(String modelName, String colour, int quantity, double unitPrice) {
            this.modelName = modelName;
            this.colour = colour;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getModelName() { return modelName; }
        public String getColour() { return colour; }
        public int getQuantity() { return quantity; }
        public double getSubtotal() { return quantity * unitPrice; }
    }

