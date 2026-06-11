package repository;

import model.Passenger;
import model.Product;
import db.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class PurchaseRepository {

    /**
     * Executes the complete purchasing transaction.
     * Inserts into Purchasing_table, Purchase_Products, and updates the Products stock.
     */
    public boolean executePurchase(Passenger passenger, Map<Product, Integer> cart, double totalPrice) {
        String insertPurchaseSql = "INSERT INTO Purchasing_table (user_id, total_price) VALUES (?, ?)";
        String insertItemSql = "INSERT INTO Purchase_Items (purchase_id, product_id, amount) VALUES (?, ?, ?)";
        String updateStockSql = "UPDATE Products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";

        Connection conn = null;

        try {
            conn = DatabaseConnectionManager.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert into the main Purchasing_table
            int newPurchaseId = -1;
            try (PreparedStatement pstmtPurchase = conn.prepareStatement(insertPurchaseSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPurchase.setInt(1, passenger.getId());
                pstmtPurchase.setDouble(2, totalPrice);
                pstmtPurchase.executeUpdate();

                ResultSet rs = pstmtPurchase.getGeneratedKeys();
                if (rs.next()) {
                    newPurchaseId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve new purchase ID.");
                }
            }

            // 2. Insert items into Purchase_Items and update stock
            try (PreparedStatement pstmtItem = conn.prepareStatement(insertItemSql);
                 PreparedStatement pstmtStock = conn.prepareStatement(updateStockSql)) {

                for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();

                    // Add row to Purchase_Items table
                    pstmtItem.setInt(1, newPurchaseId);
                    pstmtItem.setInt(2, product.getProductId());
                    pstmtItem.setInt(3, quantity);
                    pstmtItem.executeUpdate();

                    // Reduce stock in Products table
                    pstmtStock.setInt(1, quantity);
                    pstmtStock.setInt(2, product.getProductId());
                    pstmtStock.setInt(3, quantity);

                    int affectedRows = pstmtStock.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Not enough stock for product ID: " + product.getProductId());
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction failed, rolling back. Reason: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
