package com.example.dash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    List<String> OrderItems, ItemRemarks;
    List<Double> ItemWeights;

    public OrderItemAdapter(List<String> items, List<Double> weights, List<String> remarks) {
        this.OrderItems = items;
        this.ItemWeights = weights;
        this.ItemRemarks = remarks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return OrderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Context context;

        TextView ItemName,Remarks, Carat, Weight;
        ImageView SamplePhoto;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mView = itemView;
            context = context;
            SamplePhoto = mView.findViewById(R.id.ItemPicture);
            ItemName = mView.findViewById(R.id.ItemName);
            Remarks = mView.findViewById(R.id.Remarks);
            Carat = mView.findViewById(R.id.ItemCategory);
            Weight = mView.findViewById(R.id.ItemWeight);
        }
    }

}
