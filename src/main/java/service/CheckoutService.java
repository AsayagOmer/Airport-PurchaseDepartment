package service;

import dao.PassengerDAO;
import dao.ProductDAO;
import model.Passenger;
import model.Product;
import repository.PurchaseRepository;
import process.CalcDiscount;

import java.util.Map;

/**
 * Service class responsible for handling the checkout and purchasing logic.
 * It coordinates between the data access layer (DAOs), the repository layer,
 * and the discount calculation process.
 */
public class CheckoutService {
    private final PassengerDAO passengerDAO;
    private final ProductDAO productDAO;
    private final PurchaseRepository purchaseRepository;
    private final MLServiceClient mlServiceClient;

    /**
     * Initializes the CheckoutService with the necessary DAOs and Repository.
     */
    public CheckoutService() {
        this.passengerDAO = new PassengerDAO();
        this.productDAO = new ProductDAO();
        this.purchaseRepository = new PurchaseRepository();
        this.mlServiceClient = new MLServiceClient();
    }

    /**
     * Fetches recommended products for a passenger from the ML microservice.
     */
    public java.util.List<Product> getRecommendedProducts(String passportNumber) {
        String hashedPassport = util.HashUtil.hashSHA256(passportNumber);
        java.util.List<Integer> recommendedIds = mlServiceClient.getRecommendations(hashedPassport);
        java.util.List<Product> recommendedProducts = new java.util.ArrayList<>();
        for (Integer id : recommendedIds) {
            Product p = getProduct(id);
            if (p != null) {
                recommendedProducts.add(p);
            }
        }
        return recommendedProducts;
    }

    /**
     * Authenticates a passenger based on their passport number.
     *
     * @param passportNumber The passport number provided by the user.
     * @return The Passenger object if found, otherwise null.
     */
    public Passenger authenticatePassenger(String passportNumber) {
        String hashedPassport = util.HashUtil.hashSHA256(passportNumber);
        return passengerDAO.findByPassportNumber(hashedPassport);
    }

    /**
     * Retrieves product details by the product ID.
     *
     * @param prodId The unique identifier of the product.
     * @return The Product object if found, otherwise null.
     */
    public Product getProduct(int prodId) {
        return productDAO.findById(prodId);
    }

    /**
     * Calculates the total cost for a specific product based on its quantity
     * and any active discount rules (e.g., percentages, 1+1).
     *
     * @param product  The product being purchased.
     * @param quantity The amount of the product being purchased.
     * @return The final calculated price for the given quantity.
     */
    public float calculateItemCost(Product product, int quantity) {
        return CalcDiscount.calcDiscount(product.getProductPrice(), product.getProductSale(), quantity);
    }

    /**
     * Calculates the total bill for the entire cart in parallel.
     *
     * @param cart A map containing the products to be bought and their quantities.
     * @return The total cost of the purchase.
     */
    public float calculateTotalBill(Map<Product, Integer> cart) {
        if (cart == null || cart.isEmpty()) {
            return 0f;
        }
        
        // Parallelize the calculation of the total bill across cart items
        return cart.entrySet().parallelStream()
                .map(entry -> calculateItemCost(entry.getKey(), entry.getValue()))
                .reduce(0f, Float::sum);
    }

    /**
     * Processes the final checkout by passing the transaction data to the repository.
     *
     * @param passenger The authenticated passenger making the purchase.
     * @param cart      A map containing the products to be bought and their quantities.
     * @param totalBill The total cost of the purchase.
     * @return true if the transaction was successful and committed, false if it failed.
     */
    public boolean processCheckout(Passenger passenger, Map<Product, Integer> cart, float totalBill) {
        if (cart == null || cart.isEmpty()) {
            return false;
        }
        return purchaseRepository.executePurchase(passenger, cart, totalBill);
    }
}
