package dao;

import db.DatabaseConnectionManager;
import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO implements DAO<Product> {

    private static final Map<Integer, Product> MOCK_PRODUCTS = new HashMap<>();

    static {
        MOCK_PRODUCTS.put(1, new Product(1, "Whisky Gold Label", 120.0f, 50, "Alcohol", "1+1"));
        MOCK_PRODUCTS.put(2, new Product(2, "Toblerone Chocolate", 15.5f, 100, "Food", "20% off"));
        MOCK_PRODUCTS.put(3, new Product(3, "Chanel No. 5 Perfume", 95.0f, 20, "Perfume", "None"));
    }

    @Override
    public Product findById(int id) {
        String sql = "SELECT * FROM Products WHERE product_id = ?";

        // Using try-with-resources to ensure the connection is closed automatically
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractProductFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error finding product by ID, using mock fallback. Reason: " + e.getMessage());
            return MOCK_PRODUCTS.get(id);
        } catch (Exception e) {
            System.err.println("Error finding product, using mock fallback. Reason: " + e.getMessage());
            return MOCK_PRODUCTS.get(id);
        }
        return MOCK_PRODUCTS.get(id);
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products ORDER BY name ASC";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving all products, using mock fallback. Reason: " + e.getMessage());
            return new ArrayList<>(MOCK_PRODUCTS.values());
        }
        return products;
    }

    /**
     * Custom DAO method specific to the Duty-Free purchasing logic.
     * Reduces the stock of a specific product.
     * @param productId The ID of the product
     * @param amountToReduce The quantity bought by the customer
     * @return true if the stock was successfully reduced, false otherwise (e.g., not enough stock)
     */
    public boolean reduceStock(int productId, int amountToReduce) {
        // The WHERE clause also ensures stock doesn't become negative
        String sql = "UPDATE Products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, amountToReduce);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, amountToReduce);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Database error reducing stock, using mock fallback. Reason: " + e.getMessage());
            Product p = MOCK_PRODUCTS.get(productId);
            if (p != null && p.getProductStock() >= amountToReduce) {
                p.setProductStock(p.getProductStock() - amountToReduce);
                return true;
            }
            return false;
        }
    }

    // --- Unimplemented standard CRUD methods (can be filled later if needed) ---

    @Override
    public void save(Product entity) {
        // Implementation for inserting a new product into the database
    }

    @Override
    public void update(Product entity) {
        // Implementation for updating an existing product's details
    }

    @Override
    public void delete(int id) {
        // Implementation for deleting a product from the catalog
    }

    // --- Helper Methods ---

    /**
     * Extracts a Product object from the current row of a ResultSet.
     */
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        return factory.ModelFactory.createProduct(rs);
    }
}