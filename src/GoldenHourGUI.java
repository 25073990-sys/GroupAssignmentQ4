import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import models.Employee;
import models.Model;
import models.Sale;
import models.SaleItem;
import services.AuthService;
import services.AttendanceService;
import services.FileService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GoldenHourGUI extends Application {

    // === SERVICES ===
    private AuthService authService = new AuthService();
    private AttendanceService attendanceService = new AttendanceService();
    private List<Model> inventory;
    private List<Sale> allSales = new ArrayList<>(); // Session sales history
    private Employee currentUser;

    // === GUI STATE ===
    private Stage primaryStage;
    private ObservableList<SaleItem> currentCart = FXCollections.observableArrayList();
    private Label salesTotalLabel = new Label("Total: RM 0.00");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.inventory = FileService.loadModels(); // Load Data

        primaryStage.setTitle("GoldenHour Management System");
        showLoginScreen();
        primaryStage.show();
    }

    // ==========================================
    // 1. LOGIN SCREEN
    // ==========================================
    private void showLoginScreen() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #F0F4F8;");

        Text sceneTitle = new Text("GoldenHour Login");
        sceneTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        sceneTitle.setFill(Color.web("#2C3E50"));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("Employee ID:");
        TextField userTextField = new TextField();
        grid.add(userName, 0, 1);
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        PasswordField pwBox = new PasswordField();
        grid.add(pw, 0, 2);
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        btn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(e -> {
            String id = userTextField.getText();
            String pass = pwBox.getText();
            if (authService.login(id, pass)) {
                currentUser = authService.getCurrentUser();
                showMainDashboard();
            } else {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Invalid ID or Password");
            }
        });

        Scene scene = new Scene(grid, 900, 600);
        primaryStage.setScene(scene);
    }

    // ==========================================
    // 2. DASHBOARD
    // ==========================================
    private void showMainDashboard() {
        BorderPane border = new BorderPane();

        // --- Header ---
        HBox header = new HBox();
        header.setPadding(new Insets(15, 12, 15, 12));
        header.setSpacing(10);
        header.setStyle("-fx-background-color: #2C3E50;");

        Label welcome = new Label("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        welcome.setTextFill(Color.WHITE);
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            currentUser = null;
            showLoginScreen();
        });

        header.getChildren().addAll(welcome, spacer, logoutBtn);
        border.setTop(header);

        // --- Tabs ---
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().add(createAttendanceTab());
        tabPane.getTabs().add(createStockTab());
        tabPane.getTabs().add(createStockOpsTab()); // Count & Transfer
        tabPane.getTabs().add(createSalesTab());
        tabPane.getTabs().add(createSearchTab());

        if (currentUser.getRole().equalsIgnoreCase("Manager")) {
            tabPane.getTabs().add(createManagerTab());
        }

        border.setCenter(tabPane);
        Scene scene = new Scene(border, 1000, 700);
        primaryStage.setScene(scene);
    }

    // ==========================================
    // 3. ATTENDANCE TAB
    // ==========================================
    private Tab createAttendanceTab() {
        Tab tab = new Tab("Attendance");
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Label statusLabel = new Label("Please record your attendance.");
        statusLabel.setFont(Font.font("Segoe UI", 18));

        Button clockInBtn = new Button("Clock In");
        clockInBtn.setPrefSize(180, 60);
        clockInBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-size: 16px;");

        Button clockOutBtn = new Button("Clock Out");
        clockOutBtn.setPrefSize(180, 60);
        clockOutBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 16px;");
        clockOutBtn.setDisable(true);

        clockInBtn.setOnAction(e -> {
            attendanceService.clockIn(currentUser.getId());
            statusLabel.setText("Clocked In at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
            clockInBtn.setDisable(true);
            clockOutBtn.setDisable(false);
            showAlert("Attendance", "Clock In Successful!");
        });

        clockOutBtn.setOnAction(e -> {
            statusLabel.setText("Clocked Out at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
            clockOutBtn.setDisable(true);
            showAlert("Attendance", "Clock Out Successful!");
        });

        vbox.getChildren().addAll(statusLabel, clockInBtn, clockOutBtn);
        tab.setContent(vbox);
        return tab;
    }

    // ==========================================
    // 4. INVENTORY TAB
    // ==========================================
    private Tab createStockTab() {
        Tab tab = new Tab("Inventory View");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label lbl = new Label("Current Stock Level");
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TableView<Model> table = new TableView<>();

        TableColumn<Model, String> nameCol = new TableColumn<>("Model Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("modelName"));

        TableColumn<Model, String> colorCol = new TableColumn<>("Color");
        colorCol.setCellValueFactory(new PropertyValueFactory<>("dialColour"));

        TableColumn<Model, Double> priceCol = new TableColumn<>("Price (RM)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        String outlet = getOutletCode();
        TableColumn<Model, Integer> stockCol = new TableColumn<>("Stock (" + outlet + ")");
        stockCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getStock(outlet))
        );

        table.getColumns().addAll(nameCol, colorCol, priceCol, stockCol);
        table.setItems(FXCollections.observableArrayList(inventory));
        // FIXED: Using non-deprecated constant
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        vbox.getChildren().addAll(lbl, table);
        tab.setContent(vbox);
        return tab;
    }

    // ==========================================
    // 5. STOCK OPS (COUNT & TRANSFER)
    // ==========================================
    private Tab createStockOpsTab() {
        Tab tab = new Tab("Stock Operations");
        TabPane subTabs = new TabPane();

        // --- A. Stock Count ---
        Tab countTab = new Tab("Stock Count Verification");
        countTab.setClosable(false);
        VBox countBox = new VBox(15);
        countBox.setPadding(new Insets(20));

        TextField countModel = new TextField(); countModel.setPromptText("Model Name");
        TextField countQty = new TextField(); countQty.setPromptText("Physical Qty");
        Button verifyBtn = new Button("Verify");
        Label resultLbl = new Label("Enter data to verify.");

        verifyBtn.setOnAction(e -> {
            Model m = findModel(countModel.getText());
            if (m != null) {
                try {
                    int phys = Integer.parseInt(countQty.getText());
                    int sys = m.getStock(getOutletCode());
                    if (phys == sys) {
                        resultLbl.setText("MATCH! System: " + sys);
                        resultLbl.setTextFill(Color.GREEN);
                    } else {
                        resultLbl.setText("MISMATCH! System: " + sys + " (Diff: " + (phys-sys) + ")");
                        resultLbl.setTextFill(Color.RED);
                    }
                } catch (Exception ex) { showAlert("Error", "Invalid Number"); }
            } else { showAlert("Error", "Model not found"); }
        });

        countBox.getChildren().addAll(new Label("Daily Stock Count"), countModel, countQty, verifyBtn, resultLbl);
        countTab.setContent(countBox);

        // --- B. Stock Transfer ---
        Tab transTab = new Tab("Transfer Stock");
        transTab.setClosable(false);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20)); grid.setHgap(10); grid.setVgap(10);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Stock In (Receive)", "Stock Out (Transfer)");
        typeBox.getSelectionModel().selectFirst();

        TextField tModel = new TextField();
        TextField tQty = new TextField();
        TextField tTarget = new TextField(); tTarget.setPromptText("Target Outlet Code");
        Button procBtn = new Button("Process Transfer");

        procBtn.setOnAction(e -> {
            Model m = findModel(tModel.getText());
            if (m != null) {
                try {
                    int qty = Integer.parseInt(tQty.getText());
                    String myOutlet = getOutletCode();

                    if (typeBox.getValue().contains("Out")) {
                        if (m.getStock(myOutlet) >= qty) {
                            m.setStock(myOutlet, m.getStock(myOutlet) - qty);
                            showAlert("Success", "Transferred " + qty + " units out.");
                            saveTransferLog("OUT", myOutlet, tTarget.getText(), m.getModelName(), qty);
                        } else { showAlert("Error", "Insufficient Stock"); }
                    } else {
                        m.setStock(myOutlet, m.getStock(myOutlet) + qty);
                        showAlert("Success", "Received " + qty + " units.");
                        saveTransferLog("IN", tTarget.getText(), myOutlet, m.getModelName(), qty);
                    }
                } catch(Exception ex) { showAlert("Error", "Invalid Input"); }
            } else { showAlert("Error", "Model Not Found"); }
        });

        grid.addRow(0, new Label("Type:"), typeBox);
        grid.addRow(1, new Label("Model:"), tModel);
        grid.addRow(2, new Label("Qty:"), tQty);
        grid.addRow(3, new Label("From/To Outlet:"), tTarget);
        grid.add(procBtn, 1, 4);

        transTab.setContent(grid);

        subTabs.getTabs().addAll(countTab, transTab);
        tab.setContent(subTabs);
        return tab;
    }

    // ==========================================
    // 6. SALES POS TAB
    // ==========================================
    private Tab createSalesTab() {
        Tab tab = new Tab("Sales POS");
        BorderPane layout = new BorderPane();

        // Left: Form
        VBox form = new VBox(10);
        form.setPadding(new Insets(15));
        form.setPrefWidth(300);
        form.setStyle("-fx-background-color: #ECF0F1;");

        TextField custTxt = new TextField(); custTxt.setPromptText("Customer Name");
        TextField modelTxt = new TextField(); modelTxt.setPromptText("Model Name");
        TextField qtyTxt = new TextField(); qtyTxt.setPromptText("Quantity");

        ComboBox<String> methodBox = new ComboBox<>();
        methodBox.getItems().addAll("Cash", "Credit Card", "E-Wallet");
        methodBox.getSelectionModel().selectFirst();

        Button addBtn = new Button("Add to Cart");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        Button clearBtn = new Button("Clear Cart");

        form.getChildren().addAll(new Label("New Sale"), custTxt, modelTxt, qtyTxt, new Label("Payment"), methodBox, addBtn, clearBtn);

        // Center: Cart
        TableView<SaleItem> cartTable = new TableView<>();

        TableColumn<SaleItem, String> cModel = new TableColumn<>("Model");
        cModel.setCellValueFactory(new PropertyValueFactory<>("modelName"));

        TableColumn<SaleItem, Integer> cQty = new TableColumn<>("Qty");
        cQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SaleItem, Double> cPrice = new TableColumn<>("Price");
        cPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        cartTable.getColumns().addAll(cModel, cQty, cPrice);
        cartTable.setItems(currentCart);
        // FIXED: Using non-deprecated constant
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Bottom: Total & Checkout
        HBox bottom = new HBox(20);
        bottom.setPadding(new Insets(15));
        bottom.setAlignment(Pos.CENTER_RIGHT);

        salesTotalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold;");
        checkoutBtn.setPrefSize(120, 40);

        bottom.getChildren().addAll(salesTotalLabel, checkoutBtn);

        // Logic
        addBtn.setOnAction(e -> {
            Model m = findModel(modelTxt.getText());
            if (m == null) { showAlert("Error", "Model not found"); return; }
            try {
                int qty = Integer.parseInt(qtyTxt.getText());
                String outlet = getOutletCode();
                if (qty > m.getStock(outlet)) {
                    showAlert("Stock Error", "Available: " + m.getStock(outlet));
                    return;
                }
                SaleItem item = new SaleItem(m.getModelName(), "Std", qty, m.getPrice());
                currentCart.add(item);
                updateTotal();
            } catch (Exception ex) { showAlert("Error", "Invalid Qty"); }
        });

        clearBtn.setOnAction(e -> { currentCart.clear(); updateTotal(); });

        checkoutBtn.setOnAction(e -> {
            if (currentCart.isEmpty()) return;
            String outlet = getOutletCode();
            Sale sale = new Sale(outlet, custTxt.getText(), methodBox.getValue(), currentUser.getName());

            for(SaleItem i : currentCart) {
                Model m = findModel(i.getModelName());
                if(m != null) m.setStock(outlet, m.getStock(outlet) - i.getQuantity());
                sale.addItem(i.getModelName(), "Std", i.getQuantity(), i.getPrice());
            }
            allSales.add(sale);
            saveReceipt(sale);
            showAlert("Success", "Transaction Complete!");
            currentCart.clear(); custTxt.clear(); updateTotal();
        });

        layout.setLeft(form);
        layout.setCenter(cartTable);
        layout.setBottom(bottom);
        tab.setContent(layout);
        return tab;
    }

    // ==========================================
    // 7. SEARCH TAB
    // ==========================================
    private Tab createSearchTab() {
        Tab tab = new Tab("Search");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        HBox searchBar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search Customer, Model, or Date");
        Button searchBtn = new Button("Search");
        searchBar.getChildren().addAll(searchField, searchBtn);

        TableView<Sale> table = new TableView<>();
        TableColumn<Sale, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        TableColumn<Sale, String> custCol = new TableColumn<>("Customer");
        custCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Sale, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        table.getColumns().addAll(dateCol, custCol, totalCol);
        // FIXED: Using non-deprecated constant
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        searchBtn.setOnAction(e -> {
            String key = searchField.getText().toLowerCase();
            List<Sale> results = allSales.stream().filter(s ->
                    s.getCustomerName().toLowerCase().contains(key) ||
                            s.getTimestamp().toString().contains(key) ||
                            s.getItems().stream().anyMatch(i -> i.getModelName().toLowerCase().contains(key))
            ).collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(results));
        });

        layout.getChildren().addAll(new Label("Search Sales History"), searchBar, table);
        tab.setContent(layout);
        return tab;
    }

    // ==========================================
    // 8. MANAGER TAB
    // ==========================================
    private Tab createManagerTab() {
        Tab tab = new Tab("Manager Admin");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20)); grid.setHgap(10); grid.setVgap(10);

        TextField idTxt = new TextField();
        TextField nameTxt = new TextField();
        TextField passTxt = new TextField();
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Full-time", "Part-time", "Manager");
        Button regBtn = new Button("Register Employee");

        regBtn.setOnAction(e -> {
            Employee emp = new Employee(idTxt.getText(), nameTxt.getText(), roleBox.getValue(), passTxt.getText());
            if (authService.register(emp)) {
                showAlert("Success", "Employee Registered.");
                idTxt.clear(); nameTxt.clear();
            } else { showAlert("Error", "ID already exists."); }
        });

        grid.addRow(0, new Label("New ID:"), idTxt);
        grid.addRow(1, new Label("Name:"), nameTxt);
        grid.addRow(2, new Label("Password:"), passTxt);
        grid.addRow(3, new Label("Role:"), roleBox);
        grid.add(regBtn, 1, 4);

        tab.setContent(grid);
        return tab;
    }

    // ==========================================
    // UTILITIES
    // ==========================================
    private void updateTotal() {
        double total = currentCart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        salesTotalLabel.setText("Total: RM " + String.format("%.2f", total));
    }

    private String getOutletCode() {
        if (currentUser == null) return "C60";
        String raw = currentUser.getId();
        return raw.toUpperCase().startsWith("C") ? (raw.length() >= 3 ? raw.substring(0, 3) : "C60") : "C60";
    }

    private Model findModel(String name) {
        return inventory.stream().filter(m -> m.getModelName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void saveReceipt(Sale sale) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("receipts/GUI_SALES.txt", true))) {
            writer.write("RECEIPT: " + LocalDateTime.now() + " | Customer: " + sale.getCustomerName() + " | Total: RM" + sale.getTotal());
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveTransferLog(String type, String from, String to, String model, int qty) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("receipts/TRANSFER_LOG.txt", true))) {
            writer.write(LocalDateTime.now() + " | " + type + " | " + from + " -> " + to + " | " + model + " x" + qty);
            writer.newLine();
        } catch (IOException e) {}
    }
}