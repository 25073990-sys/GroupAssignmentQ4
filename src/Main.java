import services.StockService;
import models.StockTransaction;public class Main {
    public static void main(String[] args) {
        StockService stockService = new StockService();

        // Simulating a "Stock In" from HQ to your store
        System.out.println("Yo boy Srini is testing Stock Management...");
        stockService.recordMovement("IN", "Rolex Submariner", 5, "HQ", "Store_C60", "EMP001");

        // Simulating a "Stock Out" transfer
        stockService.recordMovement("OUT", "Omega Speedmaster", 2, "Store_C60", "Store_MidValley", "EMP001");
    }
}