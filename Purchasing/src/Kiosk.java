import model.Passenger;
import model.Product;
import service.CheckoutService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Kiosk {
    private static Kiosk instance;
    private final CheckoutService checkoutService;

    // Private constructor for Singleton
    private Kiosk() {
        this.checkoutService = new CheckoutService();
    }

    public static synchronized Kiosk getInstance() {
        if (instance == null) {
            instance = new Kiosk();
        }
        return instance;
    }

    public void startTransaction() {
        Scanner scanner = new Scanner(System.in);

        // 1. Get User ID
        System.out.print("Enter passenger's passport number: ");
        String passngerPassportNumber = scanner.next();
        Passenger currentUser = checkoutService.authenticatePassenger(passngerPassportNumber);

        if (currentUser == null) {
            System.out.println("Invalid Passport number. Exiting.");
            return;
        }

        System.out.println("\n--- Welcome, " + currentUser.getFirstName() + "! ---");

        // Data structures to hold the current transaction details
        Map<Product, Integer> cart = new HashMap<>();
        float totalBill = 0;

        // 2. Loop for Products and Amounts
        while (true) {
            System.out.print("Enter Product ID (or 0 to checkout): ");
            int prodId = scanner.nextInt();

            if (prodId == 0) break;

            Product product = checkoutService.getProduct(prodId);

            if (product != null) {
                System.out.print("Enter Amount (Quantity): ");
                int quantity = scanner.nextInt();

                if (quantity > 0) {
                    float cost = checkoutService.calculateItemCost(product, quantity);
                    totalBill += cost;

                    // Add to cart (updates quantity if product already exists in cart)
                    cart.put(product, cart.getOrDefault(product, 0) + quantity);

                    System.out.printf("Added %d x %s to cart ($%.2f)\n\n", quantity, product.getProductName(), cost);
                } else {
                    System.out.println("Quantity must be greater than 0.");
                }
            } else {
                System.out.println("Product not found!");
            }
        }

        // 3. Finalize and send data to CheckoutService
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Transaction cancelled.");
            return;
        }

        System.out.println("\nProcessing your transaction...");

        // Pass the collected data to the process class
        boolean isSuccess = checkoutService.processCheckout(currentUser, cart, totalBill);

        // 4. Feedback to User
        if (isSuccess) {
            System.out.printf("\n--- Receipt for %s %s ---\n", currentUser.getFirstName(), currentUser.getLastName());
            System.out.printf("Total Amount Paid: $%.2f\n", totalBill);
            System.out.println("Thank you for shopping!");
        } else {
            System.out.println("\nTransaction failed. Please check product stock and try again.");
        }
    }
}