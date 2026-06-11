package model;

import java.sql.Timestamp;

public class Purchase {
    private int purchaseId;
    private int userId;
    private double totalPrice;
    private Timestamp purchaseDate;

    public Purchase(int userId, double totalPrice) {
        this.userId = userId;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public int getUserId() { return userId; }
    public double getTotalPrice() { return totalPrice; }
}