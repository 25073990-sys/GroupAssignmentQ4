package services;

import models.*;
import utils.ReceiptGenerator;
import java.util.List;

public class SalesService {
    public boolean processSale(Sale sale, List<Model> inventory) {
        // 1. Verify all items are in stock at the sale outlet
        for (SaleItem item : sale.getItems()) {
            Model model = findModel(item.getModelName(), inventory);
            if (model == null || model.getStockForOutlet(sale.getOutletCode()) < item.getQuantity()) {
                System.out.println("Insufficient stock for: " + item.getModelName());
                return false;
            }
        }

        // 2. Deduct stock and finalize
        for (SaleItem item : sale.getItems()) {
            Model model = findModel(item.getModelName(), inventory);
            model.updateStock(sale.getOutletCode(), -item.getQuantity());
        }

        FileService.saveSaleToCSV(sale);
        FileService.saveModels(inventory);
        ReceiptGenerator.createReceiptFile(sale);
        return true;
    }

    private Model findModel(String name, List<Model> inventory) {
        return inventory.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
