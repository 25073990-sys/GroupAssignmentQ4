import models.Employee;
import models.Model;
import models.Sale;
import services.AuthService;
import services.AttendanceService;
import services.FileService;
import services.StockService;
import services.SearchService;
import services.EditService;
import services.SalesService;
import services.ReportService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ===== Services =====
        AuthService authService = new AuthService();
        AttendanceService attendanceService = new AttendanceService();

        // Load Inventory
        List<Model> inventory = FileService.loadModels();

        // Load Sales
        List<Sale> salesHistory = new ArrayList<>();
        salesHistory = FileService.loadSales();

        // Initialize Module Services
        StockService stockService = new StockService(inventory);
        SearchService searchService = new SearchService(inventory, salesHistory);
        EditService editService = new EditService(inventory, salesHistory);
        SalesService salesService = new SalesService(inventory, salesHistory);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        while (true) {

            // ===== LOGIN =====
            System.out.println("=== GOLDENHOUR SYSTEM LOGIN ===");
            System.out.print("Employee ID: ");
            String id = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();

            if (!authService.login(id, pass)) {
                System.out.println("Login Failed: Invalid ID or Password.\n");
                continue;
            }

            Employee currentUser = authService.getCurrentUser();
            System.out.println("\nWelcome, " + currentUser.getName() + "!");

            boolean loggedIn = true;
            LocalTime clockInTime = null;

            // ===== MAIN MENU =====
            while (loggedIn) {
                System.out.println("\n=== MAIN MENU ===");
                System.out.println("1. Stock Management");
                System.out.println("2. Attendance");
                System.out.println("3. Record New Sale");
                System.out.println("4. Search Information");
                System.out.println("5. Edit Information");
                System.out.println("6. Register New Employee");
                System.out.println("7. Sales History & Data Analytics");
                System.out.println("8. Logout");
                System.out.print("Select option: ");

                String choice = scanner.nextLine();

                switch (choice) {

                    // ===== STOCK =====
                    case "1":
                        runStockModule(stockService, scanner, currentUser);
                        break;

                    // ===== ATTENDANCE =====
                    case "2":
                        System.out.println("\n--- ATTENDANCE ---");
                        System.out.println("1. Clock In");
                        System.out.println("2. Clock Out");
                        System.out.print("Choice: ");
                        String attChoice = scanner.nextLine();

                        if (attChoice.equals("1")) {
                            clockInTime = LocalTime.now();
                            attendanceService.clockIn(currentUser.getId());
                            System.out.println("Clock In Successful!");
                            System.out.println("Date: " + LocalDate.now());
                            System.out.println("Time: " + clockInTime.format(timeFormatter));
                        }
                        else if (attChoice.equals("2")) {
                            if (clockInTime == null) {
                                System.out.println("You have not clocked in yet.");
                            } else {
                                LocalTime out = LocalTime.now();
                                double hours = Duration.between(clockInTime, out).toMinutes() / 60.0;
                                System.out.println("Clock Out Successful!");
                                System.out.println("Time: " + out.format(timeFormatter));
                                System.out.printf("Total Hours Worked: %.1f hours%n", hours);
                                clockInTime = null;
                            }
                        }
                        break;

                    // ===== SALES =====
                    case "3":
                        salesService.recordNewSale(scanner, currentUser);
                        break;

                    // ===== SEARCH =====
                    case "4":
                        System.out.println("\n--- SEARCH INFORMATION ---");
                        System.out.println("1. Search Stock (By Model)");
                        System.out.println("2. Search Sales (By Keyword)");
                        System.out.print("Choice: ");
                        String searchChoice = scanner.nextLine();
                        if (searchChoice.equals("1")) searchService.searchStock(scanner);
                        else if (searchChoice.equals("2")) searchService.searchSales(scanner);
                        break;

                    // ===== EDIT =====
                    case "5":
                        System.out.println("\n--- EDIT INFORMATION ---");
                        System.out.println("1. Edit Stock Information");
                        System.out.println("2. Edit Sales Information");
                        System.out.print("Choice: ");
                        String editChoice = scanner.nextLine();
                        if (editChoice.equals("1")) editService.editStock(scanner, currentUser);
                        else if (editChoice.equals("2")) editService.editSales(scanner);
                        break;

                    // ===== REGISTER =====
                    case "6":
                        if (!currentUser.getRole().equalsIgnoreCase("Manager")) {
                            System.out.println("\n\u001B[31mACCESS DENIED\u001B[0m");
                            System.out.println("Only Managers can register new employees.");
                            break;
                        }
                        System.out.println("\n--- REGISTER NEW EMPLOYEE ---");
                        System.out.print("Employee ID: ");
                        String newId = scanner.nextLine();
                        System.out.print("Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Password: ");
                        String pw = scanner.nextLine();
                        System.out.print("Role (Manager/Staff): ");
                        String role = scanner.nextLine();

                        Employee newEmp = new Employee(newId, name, role, pw);
                        if (authService.register(newEmp)) {
                            System.out.println("\u001B[32mEmployee registered successfully.\u001B[0m");
                        } else {
                            System.out.println("\u001B[31mRegistration failed.\u001B[0m");
                        }
                        break;

                    // ==== FILTER & SORT =====
                    case "7":
                        runReportModule(scanner);
                        break;

                    // ===== LOGOUT =====
                    case "8":
                        authService.logout();
                        loggedIn = false;
                        System.out.println("Logged out successfully.");
                        break;

                    default:
                        System.out.println("Invalid option.");
                }
            }
        }
    }

    // ===== STOCK SUB-MENU =====
    private static void runStockModule(StockService service, Scanner scanner, Employee emp) {
        while (true) {
            System.out.println("\n--- STOCK MANAGEMENT ---");
            System.out.println("1. Morning Stock Count");
            System.out.println("2. Stock Transfer (In/Out)");
            System.out.println("3. Return to Main Menu");
            System.out.print("Choice: ");

            String subChoice = scanner.nextLine();

            if (subChoice.equals("1")) {
                service.performStockCount(scanner);
            }
            else if (subChoice.equals("2")) {
                System.out.print("Type (1) for Stock In, (2) for Stock Out: ");
                String typeInput = scanner.nextLine();
                if (typeInput.equals("1")) service.processStockTransfer(scanner, emp, true);
                else if (typeInput.equals("2")) service.processStockTransfer(scanner, emp, false);
            }
            else {
                break;
            }
        }
    }

    public static void runReportModule(Scanner scanner) {
        //Reload data to ensure it is fresh
        List<Sale> allSales = FileService.loadSales();

        if(allSales.isEmpty()) {
            System.out.println("No sales found.");
            return;
        }
        System.out.println("\n ---SALES & ANALYTICS REPORT---");
        System.out.println("1. Filter & Sort Sales");
        System.out.println("2. Data Analytics");
        System.out.print("Choice: ");

        String mainChoice = scanner.nextLine();
        List <Sale> displayList = allSales;
        boolean showTable = true;

        try {
            switch (mainChoice) {
                //===== FILTER AND SORT ======
                case "1":
                    System.out.println(" [Filter & Sort Sales] ");
                    System.out.println("1. View All Sales");
                    System.out.println("2. Filter by Date Range");
                    System.out.println("3. Sort by Date (Latest first)");
                    System.out.println("4. Sort by Date (Oldest first)");
                    System.out.println("5. Sort by Amount (High -> Low)");
                    System.out.println("6. Sort by Amount (Low -> High)");
                    System.out.println("7. Sort by Customer Name");
                    System.out.print("Choice: ");

                    String subChoice1 = scanner.nextLine();
                        switch(subChoice1) {
                            case "1": break;
                            case "2":
                                System.out.println("Enter Start Date (yyyy-MM-dd): ");
                                LocalDate startDate = LocalDate.parse(scanner.nextLine());
                                System.out.println("Enter End Date (yyyy-MM-dd): ");
                                LocalDate endDate = LocalDate.parse(scanner.nextLine());
                                displayList = ReportService.filterByDateRange(allSales,startDate,endDate);
                                break;
                            case "3":
                                ReportService.sortSales(displayList,"date",false);
                                break;
                            case "4":
                                ReportService.sortSales(displayList,"date",true);
                                break;
                            case "5":
                                ReportService.sortSales(displayList,"amount",false);
                                break;
                            case "6":
                                ReportService.sortSales(displayList, "amount", true);
                                break;
                            case "7":
                                ReportService.sortSales(displayList, "customer", true);
                                break;
                            default:
                                System.out.println("Invalid option.");
                                showTable = false;
                                break;
                        }
                    break;
                //===== DATA ANALYTICS =====
                case "2":
                    showTable = false; // Don't show the big table for analytics

                    System.out.println(" [Data Analytics] ");
                    System.out.println("1. Average Daily Revenue");
                    System.out.println("2. Total Sales by DAY");
                    System.out.println("3. Total Sales by WEEK");
                    System.out.println("4. Total Sales by MONTH");
                    System.out.print("Choice: ");
                    String subChoice2 = scanner.nextLine();
                        switch(subChoice2) {
                            case "1":
                                ReportService.avgDailyRevenue(allSales);
                                break;
                            case "2":
                                ReportService.salesByPeriod(allSales,"DAILY");
                                break;
                            case "3":
                                ReportService.salesByPeriod(allSales,"WEEKLY");
                                break;
                            case "4":
                                ReportService.salesByPeriod(allSales,"MONTHLY");
                                break;
                            default:
                                System.out.println("Invalid option.");
                        }
                        break;
                default:
                    System.out.println("Invalid main option.");
                    showTable = false;
                    break;
            }

            //DISPLAY TABLE
            if(showTable) {
                System.out.println("\n------------------------------------------------------------------------------------------------");
                System.out.printf("%-12s | %-6s | %-6s | %-15s | %-12s | %-10s | %-8s%n",
                        "Date", "Time", "Outlet", "Customer", "Total", "Method", "Staff");
                System.out.println("------------------------------------------------------------------------------------------------");

                DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

                //Display cumulative sales
                double totalRevenue = 0.0;
                for (Sale s : displayList) {
                    System.out.printf("%-12s | %-6s | %-6s | %-15s | RM%-10.2f | %-10s | %-8s%n",
                            s.getTimestamp().toLocalDate(),
                            s.getTimestamp().format(timeFmt),
                            s.getOutletCode(),
                            s.getCustomerName(),
                            s.getTotal(),
                            s.getMethod(),
                            s.getEmployeeInCharge());

                    totalRevenue += s.getTotal();
                }
                System.out.println("------------------------------------------------------------------------------------------------");
                System.out.printf("%58s TOTAL SALES: RM %-10.2f%n", " ", totalRevenue);
                System.out.println("------------------------------------------------------------------------------------------------");
            }
        } catch (Exception e){
                System.out.println("Error processing report (Check date format yyyy-MM-dd): " + e.getMessage());
            }
    }
}