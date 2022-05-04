package com.example.webshop.model;

public class CartItem {
    private int quantity;
    private ClothesItem item;

    public ClothesItem getItem() {
        return item;
    }

    public void setItem(ClothesItem item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
