package com.example.webshop.model;

import android.os.Build;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String UID;
    private List<CartItem> orderedItems;
    private String orderDate;

    public Order() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            orderDate = LocalDateTime.now().toString();
        }
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public List<CartItem> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<CartItem> orderedItems) {
        this.orderedItems = orderedItems;
    }
}
