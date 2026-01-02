package services;

import models.*;
import utils.ReceiptGenerator;
import java.util.List;

public class SalesService {
    public boolean processSale(Sale sale, List<Model> inventory) {
        // Verification block - check if the product exists and if the specific store has enough stock
        for (SaleItem item : sale.getItems()) {
            Model model = findModel(item.getModelName(), item.getColour(), inventory);

            if (model == null) {
                System.out.println("Error: Product " + item.getModelName() + " not found.");
                return false;
            }

            if (model.getStock(sale.getOutletCode()) < item.getQuantity()) {
                System.out.println("Insufficient stock at " + sale.getOutletCode());
                return false;
            }
        }

        // Deduction block - Deduct stock using the array logic in Model.java
        for (SaleItem item : sale.getItems()) {
            Model model = findModel(item.getModelName(), item.getColour(), inventory);
            model.adjustStock(sale.getOutletCode(), -item.getQuantity());
        }

        // 3. Persistence block - tells FileService to record transaction and update models.csv
        FileService.saveSaleToCSV(sale);
        FileService.saveModels(inventory); // Overwrites models.csv with new array values
        ReceiptGenerator.createReceiptFile(sale);

        return true;
    }

    private Model findModel(String name, String colour, List<Model> inventory) {
        for (Model m : inventory) {
            if (m.getModelName().equalsIgnoreCase(name) && m.getDialColour().equalsIgnoreCase(colour)) {
                return m;
            }
        }
        return null;
    }
}