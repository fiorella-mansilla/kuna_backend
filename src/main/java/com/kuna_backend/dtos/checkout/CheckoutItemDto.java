package com.kuna_backend.dtos.checkout;

public class CheckoutItemDto {

    private String productName;
    private int quantity;
    private double price;
    private int productVariationId;
    private int clientId;

    public CheckoutItemDto() {
    }

    public CheckoutItemDto(String productName, int quantity, double price, int productVariationId, int clientId) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.productVariationId = productVariationId;
        this.clientId = clientId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName (String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getProductVariationId() {
        return productVariationId;
    }

    public void setProductVariationId(int productVariationId) {
        this.productVariationId = productVariationId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
}
