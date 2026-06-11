package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Passenger;
import model.Product;
import service.CheckoutService;

import java.util.HashMap;
import java.util.Map;

public class DashboardApp extends Application {

    private CheckoutService checkoutService;
    private Passenger currentPassenger;
    private Map<Product, Integer> cart;
    
    // UI Elements
    private Label passengerInfoLabel;
    private ListView<String> cartListView;
    private Label totalLabel;
    private TextField passportField;
    private TextField productIdField;
    private TextField quantityField;
    private Button checkoutButton;
    private Button scanButton;
    private Button addButton;

    @Override
    public void init() {
        checkoutService = new CheckoutService();
        cart = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Airport Purchasing Dashboard");

        // Root Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Header
        Label headerLabel = new Label("Airport Duty-Free Checkout");
        headerLabel.getStyleClass().add("title-label");
        HBox header = new HBox(headerLabel);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 20, 0));
        root.setTop(header);

        // Left Panel (Passenger & Product Entry)
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(300);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #2D2D2D; -fx-background-radius: 8;");

        Label authLabel = new Label("1. Scan Passenger");
        authLabel.setStyle("-fx-font-weight: bold;");
        passportField = new TextField();
        passportField.setPromptText("Enter Passport Number");
        scanButton = new Button("Scan / Authenticate");
        scanButton.getStyleClass().add("primary-button");
        scanButton.setMaxWidth(Double.MAX_VALUE);
        passengerInfoLabel = new Label("No passenger scanned.");
        
        Label productLabel = new Label("2. Add Products");
        productLabel.setStyle("-fx-font-weight: bold;");
        productIdField = new TextField();
        productIdField.setPromptText("Product ID");
        quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        addButton = new Button("Add to Cart");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setDisable(true); // Disabled until passenger is scanned

        leftPanel.getChildren().addAll(
                authLabel, passportField, scanButton, passengerInfoLabel,
                new Separator(),
                productLabel, productIdField, quantityField, addButton
        );

        // Center Panel (Cart & Total)
        VBox centerPanel = new VBox(15);
        centerPanel.setPadding(new Insets(10, 0, 10, 20));
        
        Label cartTitle = new Label("Current Cart");
        cartTitle.setStyle("-fx-font-weight: bold;");
        cartListView = new ListView<>();
        VBox.setVgrow(cartListView, Priority.ALWAYS);

        HBox totalBox = new HBox(10);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        Label totalText = new Label("Total: ");
        totalLabel = new Label("$0.00");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        totalBox.getChildren().addAll(totalText, totalLabel);

        checkoutButton = new Button("Complete Checkout");
        checkoutButton.getStyleClass().add("primary-button");
        checkoutButton.setMaxWidth(Double.MAX_VALUE);
        checkoutButton.setDisable(true);

        centerPanel.getChildren().addAll(cartTitle, cartListView, totalBox, checkoutButton);

        root.setLeft(leftPanel);
        root.setCenter(centerPanel);

        // Actions
        scanButton.setOnAction(e -> handleScanPassenger());
        addButton.setOnAction(e -> handleAddProduct());
        checkoutButton.setOnAction(e -> handleCheckout());

        Scene scene = new Scene(root, 800, 500);
        
        // Load CSS safely
        String cssPath = getClass().getResource("/style.css") != null ? 
                         getClass().getResource("/style.css").toExternalForm() : null;
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        } else {
            System.err.println("Warning: style.css not found in resources!");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleScanPassenger() {
        String passport = passportField.getText().trim();
        if (passport.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a passport number.");
            return;
        }

        currentPassenger = checkoutService.authenticatePassenger(passport);
        if (currentPassenger != null) {
            passengerInfoLabel.setText("Welcome, " + currentPassenger.getFirstName() + " " + currentPassenger.getLastName() + "!");
            passengerInfoLabel.setStyle("-fx-text-fill: #4CAF50;");
            addButton.setDisable(false);
        } else {
            passengerInfoLabel.setText("Passenger not found.");
            passengerInfoLabel.setStyle("-fx-text-fill: #F44336;");
            addButton.setDisable(true);
            currentPassenger = null;
        }
    }

    private void handleAddProduct() {
        try {
            int prodId = Integer.parseInt(productIdField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());

            if (qty <= 0) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Quantity must be > 0");
                return;
            }

            Product product = checkoutService.getProduct(prodId);
            if (product != null) {
                cart.put(product, cart.getOrDefault(product, 0) + qty);
                updateCartView();
                productIdField.clear();
                quantityField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Product ID " + prodId + " not found.");
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numeric values for ID and Quantity.");
        }
    }

    private void updateCartView() {
        cartListView.getItems().clear();
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            float cost = checkoutService.calculateItemCost(p, qty);
            cartListView.getItems().add(String.format("%dx %s - $%.2f", qty, p.getProductName(), cost));
        }

        float total = checkoutService.calculateTotalBill(cart);
        totalLabel.setText(String.format("$%.2f", total));
        
        checkoutButton.setDisable(cart.isEmpty());
    }

    private void handleCheckout() {
        float total = checkoutService.calculateTotalBill(cart);
        boolean success = checkoutService.processCheckout(currentPassenger, cart, total);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction completed successfully!\nTotal paid: $" + String.format("%.2f", total));
            // Reset
            cart.clear();
            currentPassenger = null;
            passportField.clear();
            passengerInfoLabel.setText("No passenger scanned.");
            passengerInfoLabel.setStyle("-fx-text-fill: #E0E0E0;");
            addButton.setDisable(true);
            checkoutButton.setDisable(true);
            updateCartView();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Checkout failed. Please check product stock.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
