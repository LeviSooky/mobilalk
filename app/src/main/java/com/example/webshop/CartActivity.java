package com.example.webshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.adapters.CartItemsAdapter;
import com.example.webshop.model.CartItem;
import com.example.webshop.model.CartProvider;
import com.example.webshop.model.ClothesItem;
import com.example.webshop.model.Order;
import com.example.webshop.services.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private final String LOG_TAG = CartActivity.class.getName();

    private TextView countTextView;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;
    private CollectionReference items;
    private RecyclerView recyclerView;
    CartItemsAdapter adapter;
    private List<CartItem> cartItemsList;
    private Button checkoutBtn;

    public List<CartItem> getResult() {
        return result;
    }

    List<CartItem> result = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        checkUserLogin();
        recyclerView = findViewById(R.id.cartRecyclerView);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        items = fireStore.collection("clothes");
        adapter = new CartItemsAdapter(this, result); //TODO
        recyclerView.setAdapter(adapter);
        getCartItemsFromFB();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!CartProvider.cartItems.isEmpty()) {
            startService(new Intent(this, NotificationService.class));
        }
    }

    public void getCartItemsFromFB() {
        for (Map.Entry<Integer, Integer> item : CartProvider.cartItems.entrySet()) {
            items.whereEqualTo("id", item.getKey().toString()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    CartItem cartItem = new CartItem();
                    cartItem.setItem(snapshot.toObject(ClothesItem.class));
                    cartItem.setQuantity(item.getValue());
                    result.add(cartItem);
                }
                adapter.notifyDataSetChanged();
            });
        }
    }


    public void checkout(View view) {
        Order order = new Order();
        order.setUID(mAuth.getUid());
        order.setOrderedItems(result);
        fireStore.collection("orders").add(order).addOnSuccessListener((itm) -> {
            CartProvider.clearCart();
            Toast toast = new Toast(this);
            toast.setText("Sikeres vásárlás!");
            toast.show();
            switchActivity(ShopListActivity.class);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        countTextView = findViewById(R.id.view_alert_count_textview);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.back:
                switchActivity(ShopListActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private <T extends AppCompatActivity> void switchActivity(Class<T> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void checkUserLogin() {
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }
    }

}