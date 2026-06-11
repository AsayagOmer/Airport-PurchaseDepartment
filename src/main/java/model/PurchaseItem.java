package model;

public class PurchaseItem {
    private int productId;
    private int amount;

    public PurchaseItem(int productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public int getProductId() { return productId; }
    public int getAmount() { return amount; }
}