package dao;

import db.DatabaseConnectionManager;
import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class ProductDAO implements DAO<Product> {

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
            System.err.println("Error finding product by ID: " + e.getMessage());
        }
        return null; // Return null if the product was not found
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
            System.err.println("Error retrieving all products: " + e.getMessage());
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
            System.err.println("Error reducing stock: " + e.getMessage());
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