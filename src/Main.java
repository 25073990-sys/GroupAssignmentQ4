import models.*;
import services.StockService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Loads the real inventory from CSV
    private static List<Model> loadInventory() {
        List<Model> inventory = new ArrayList<>();
        String[] outlets = {"C60","C61","C62","C63","C64","C65","C66","C67","C68","C69"};

        try (BufferedReader br = new BufferedReader(new FileReader("data/models.csv"))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                for (int i = 0; i < data.length; i++) data[i] = data[i].trim(); // Clean spaces

                Model m = new Model(data[0], data[1], Double.parseDouble(data[2]));

                for (int i = 0; i < outlets.length; i++) {
                    if (3 + i < data.length && !data[3 + i].isEmpty()) {
                        m.setStock(outlets[i], Integer.parseInt(data[3 + i]));
                    }
                }
                inventory.add(m);
            }
        } catch (Exception e) {
            System.out.println("Error loading system data: " + e.getMessage());
        }
        return inventory;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Model> inventory = loadInventory();
        StockService stockService = new StockService(inventory);

        // --- 1. LOGIN SCREEN (Makes it Operational) ---
        System.out.println("=== GOLDENHOUR SYSTEM LOGIN ===");
        System.out.print("Employee ID: ");
        String id = scanner.nextLine();
        System.out.print("Password:    ");
        String pass = scanner.nextLine();

        // Simulate successful login (In full system, this checks DB)
        // We use the name "Srinivaas" as per your previous request
        Employee currentUser = new Employee(id, "Srinivaas", pass, "Manager");
        System.out.println("\nWelcome, " + currentUser.getName() + "!");

        // --- 2. MAIN DASHBOARD ---
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Stock Management Module");
            System.out.println("2. Logout / Exit");
            System.out.print("Select Option: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                runStockModule(stockService, scanner, currentUser);
            } else if (choice.equals("2")) {
                System.out.println("Logging out...");
                break;
            }
        }
    }

    // The Stock Module Menu
    private static void runStockModule(StockService service, Scanner scanner, Employee emp) {
        while(true) {
            System.out.println("\n--- STOCK MANAGEMENT ---");
            System.out.println("1. Morning Stock Count");
            System.out.println("2. Stock Transfer (In/Out)");
            System.out.println("3. Return to Main Menu");
            System.out.print("Choice: ");

            String subChoice = scanner.nextLine();

            if (subChoice.equals("1")) {
                service.performStockCount(scanner);
            } else if (subChoice.equals("2")) {
                System.out.print("Type (1) for Stock In, (2) for Stock Out: ");
                String typeInput = scanner.nextLine();
                if (typeInput.equals("1")) service.processStockTransfer(scanner, emp, true);
                else if (typeInput.equals("2")) service.processStockTransfer(scanner, emp, false);
            } else {
                break;
            }
        }
    }
}