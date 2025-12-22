import models.Employee;
import models.Model;
import services.StockService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. SETUP DUMMY DATA (So you don't need CSVs yet)
        List<Model> myInventory = new ArrayList<>();
        myInventory.add(new Model("DW2300-1", 349.00, 2));
        myInventory.add(new Model("DW2300-2", 349.00, 1));
        myInventory.add(new Model("SW2500-1", 845.00, 5));

        // 2. SIMULATE LOGGED IN USER (You need this for receipts)
        Employee currentUser = new Employee("C6001", "Srinivaas", "password", "Manager");

        // 3. START YOUR SERVICE
        StockService stockService = new StockService(myInventory);
        Scanner scanner = new Scanner(System.in);

        System.out.println("DEBUG: System started. Logged in as " + currentUser.getName());

        // 4. TEST LOOP
        while (true) {
            System.out.println("\n=== STOCK MANAGER DEBUG MENU ===");
            System.out.println("1. Test Morning Stock Count");
            System.out.println("2. Test Stock In (Receive items)");
            System.out.println("3. Test Stock Out (Send items)");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    stockService.performStockCount(scanner);
                    break;
                case "2":
                    stockService.processStockTransfer(scanner, currentUser, true); // true = Stock In
                    break;
                case "3":
                    stockService.processStockTransfer(scanner, currentUser, false); // false = Stock Out
                    break;
                case "4":
                    System.out.println("Exiting test...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}