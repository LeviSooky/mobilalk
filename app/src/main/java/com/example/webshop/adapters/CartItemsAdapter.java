package com.example.webshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.R;
import com.example.webshop.ShopListActivity;
import com.example.webshop.model.CartItem;
import com.example.webshop.model.CartProvider;
import com.example.webshop.tasks.DownloadImageTask;

import java.util.List;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder> {
    Context context;
    private final List<CartItem> items;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_cart, parent, false));
    }

    public CartItemsAdapter(Context context, List<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem currentItem = items.get(position);

        holder.bindTo(currentItem);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView idText;
        private final TextView titleText;
        private final TextView quantityText;
        private final TextView priceText;
        private final TextView totalPriceText;
        private final ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idText = itemView.findViewById(R.id.idText);
            titleText = itemView.findViewById(R.id.itemTitle);
            quantityText = itemView.findViewById(R.id.quantity);
            priceText = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemPicture);
            totalPriceText = itemView.findViewById(R.id.itemTotalPrice);

            itemView.findViewById(R.id.plusOne).setOnClickListener((view -> ((ShopListActivity) context).updateCartIndicator((String) idText.getText())));
            itemView.findViewById(R.id.minusOne).setOnClickListener((view -> removeOne(idText.getText())));
        }

        private void removeOne(CharSequence text) {
            CartProvider.removeFromCart(Integer.parseInt(text.toString()));
            notifyDataSetChanged();
        }

        public void bindTo(CartItem currentItem) {
            idText.setText(currentItem.getItem().getId());
            titleText.setText(currentItem.getItem().getName());
            quantityText.setText(currentItem.getQuantity());
            priceText.setText(String.valueOf(currentItem.getItem().getPrice()));
            totalPriceText.setText(String.valueOf((currentItem.getItem().getPrice() * currentItem.getQuantity())));
            new DownloadImageTask(itemImage).execute(currentItem.getItem().getPictureUrl());

        }
    }
}
