package com.example.webshop;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.adapters.ClothesItemAdapter;
import com.example.webshop.model.CartProvider;
import com.example.webshop.model.ClothesItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;
    private CollectionReference items;

    private RecyclerView recyclerView;
    private List<ClothesItem> itemList;
    private ClothesItemAdapter adapter;
    private TextView countTextView;
    private FrameLayout redCircle;
    private final int gridNumber = 1;
    private int cartCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        checkUserLogin();
        recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        itemList = new ArrayList<>();
        adapter = new ClothesItemAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
        fireStore = FirebaseFirestore.getInstance();
        items = fireStore.collection("clothes");
        items.orderBy("name").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                itemList.add(item.toObject(ClothesItem.class));
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void checkUserLogin() {
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_list, menu);
        countTextView = findViewById(R.id.view_alert_count_textview);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = ((SearchView) MenuItemCompat.getActionView(menuItem));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = rootView.findViewById(R.id.view_alert_count_textview);
        int cartSize = CartProvider.getCartSize();
        cartCounter = cartSize;
        hideOrRevealIndicator();
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart clicked!");
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateCartIndicator(String id) {
        CartProvider.addToCart(Integer.parseInt(id));
        cartCounter = CartProvider.getCartSize();
        hideOrRevealIndicator();
    }

    private void hideOrRevealIndicator() {
        if (0 < cartCounter) {
            countTextView.setText(String.valueOf(cartCounter));
        } else {
            countTextView.setText("");
        }
        redCircle.setVisibility((cartCounter > 0) ? VISIBLE : GONE);
    }
}
