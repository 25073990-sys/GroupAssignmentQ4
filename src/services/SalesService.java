package services;

import models.*;
import utils.ReceiptGenerator;
import java.util.List;

public class SalesService {
    public boolean processSale(Sale sale, List<Model> inventory) {
        // 1. Verify items (Checking Name AND Colour)
        for (SaleItem item : sale.getItems()) {
            Model model = findModel(item.getModelName(), item.getColour(), inventory);

            if (model == null) {
                System.out.println("Error: Product " + item.getModelName() + " not found.");
                return false;
            }

            if (model.getStockForOutlet(sale.getOutletCode()) < item.getQuantity()) {
                System.out.println("Insufficient stock at " + sale.getOutletCode());
                return false;
            }
        }

        // 2. Deduct stock using the array logic in Model.java
        for (SaleItem item : sale.getItems()) {
            Model model = findModel(item.getModelName(), item.getColour(), inventory);
            model.updateStock(sale.getOutletCode(), -item.getQuantity());
        }

        // 3. Persistence
        FileService.saveSaleToCSV(sale);
        FileService.saveModels(inventory); // Overwrites models.csv with new array values
        ReceiptGenerator.createReceiptFile(sale);

        return true;
    }

    private Model findModel(String name, String colour, List<Model> inventory) {
        for (Model m : inventory) {
            if (m.getName().equalsIgnoreCase(name) && m.getColour().equalsIgnoreCase(colour)) {
                return m;
            }
        }
        return null;
    }
}