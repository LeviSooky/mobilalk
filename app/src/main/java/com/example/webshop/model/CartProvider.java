package com.example.webshop.model;

import android.os.AsyncTask;

import com.example.webshop.adapters.CartItemsAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartProvider {
    public static Map<Integer, Integer> cartItems = new HashMap<>();

    public static void addToCart(int itemId) {
        if (cartItems.containsKey(itemId)) {
            cartItems.put(itemId, cartItems.get(itemId) + 1);
        } else {
            cartItems.put(itemId, 1);
        }
    }

    public static void removeFromCart(int itemId) {
        if (cartItems.containsKey(itemId)) {
            if (cartItems.get(itemId) > 1) {
                cartItems.put(itemId, cartItems.get(itemId) - 1);
            } else {
                cartItems.remove(itemId);
            }
        }
    }

    public static void clearCart() {
        cartItems.clear();
    }

    public static List<CartItem> getCartItems() {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        List<CartItem> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> item : cartItems.entrySet()) {
            fireStore.collection("clothes").whereEqualTo("id", item.getKey().toString()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    ClothesItem clothesItem = queryDocumentSnapshot.toObject(ClothesItem.class);
                    CartItem cartItem = new CartItem();
                    cartItem.setItem(clothesItem);
                    cartItem.setQuantity(item.getValue());
                    result.add(cartItem);
                }
            });
        }
        return result;
    }

    public static class GetCartItemsAsync extends AsyncTask<CartItemsAdapter, Void, List<CartItem>> {
        @Override
        protected List<CartItem> doInBackground(CartItemsAdapter... cartItemsAdapters) {

            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
            CollectionReference reference = fireStore.collection("clothes");
            List<ClothesItem> items = new ArrayList<>();
            List<CartItem> result = new ArrayList<>();
            for (Map.Entry<Integer, Integer> item : cartItems.entrySet()) {
                reference.whereEqualTo("id", item.getKey().toString()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        CartItem cartItem = new CartItem();
                        cartItem.setItem(snapshot.toObject(ClothesItem.class));
                        cartItem.setQuantity(item.getValue());
                        result.add(cartItem);
                    }
                    cartItemsAdapters[0].notifyDataSetChanged();
                });
            }
            return result;
        }
    }
}
