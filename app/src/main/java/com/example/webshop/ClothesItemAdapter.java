package com.example.webshop;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.model.ClothesItem;

import java.util.List;
import java.util.stream.Collectors;

public class ClothesItemAdapter extends RecyclerView.Adapter<ClothesItemAdapter.ViewHolder> implements Filterable {

    private List<ClothesItem> currentItems;
    private final List<ClothesItem> allItems;
    Context context;
    private final int lastPosition = -1;

    public ClothesItemAdapter(List<ClothesItem> allItems, Context context) {
        this.allItems = allItems;
        this.currentItems = allItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ClothesItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClothesItemAdapter.ViewHolder holder, int position) {
        ClothesItem currentItem = currentItems.get(position);

        holder.bindTo(currentItem);
    }

    @Override
    public int getItemCount() {
        return currentItems.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.count = allItems.size();
                    filterResults.values = allItems;
                    return filterResults;

                } else {
                    String filter = charSequence.toString().toLowerCase().trim();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        List<ClothesItem> result = allItems.stream()
                                .filter(item -> item.getName().contains(filter) || item.getDescription().contains(filter))
                                .collect(Collectors.toList());
                        filterResults.count = result.size();
                        filterResults.values = result;
                    }
                    return filterResults;
                }
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                currentItems = ((List<ClothesItem>) filterResults.values);
                notifyDataSetChanged();
            }
        };
        return filter;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView priceText;
        private final ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.itemTitle);
            descriptionText = itemView.findViewById(R.id.itemDescription);
            priceText = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemPicture);

            itemView.findViewById(R.id.to_cart).setOnClickListener((view -> ((ShopListActivity) context).updateCartIndicator()));
        }

        public void bindTo(ClothesItem currentItem) {
            titleText.setText(currentItem.getName());
            descriptionText.setText(currentItem.getDescription());
            priceText.setText(currentItem.getPrice());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                itemImage.setImageIcon(Icon.createWithContentUri("https://images.pexels.com/photos/9035242/pexels-photo-9035242.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940"));
            }

        }
    }
}
