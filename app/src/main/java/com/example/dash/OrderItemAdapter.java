package com.example.dash;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final Context BaseCon;
    private List<String> SamplePhotos;
    List<String> OrderItems, ItemRemarks;
    List<Double> ItemWeights;

    public OrderItemAdapter(Context baseContext, List<String> items, List<Double> weights, List<String> remarks, List<String> samplePhotos) {
        this.OrderItems = items;
        this.ItemWeights = weights;
        this.ItemRemarks = remarks;
        this.SamplePhotos = samplePhotos;
        this.BaseCon = baseContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_list,parent,false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ItemName.setText(OrderItems.get(position));
        holder.Weight.setText(String.valueOf(ItemWeights.get(position)));
    try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(BaseCon.getContentResolver() , Uri.parse(SamplePhotos.get(position)));
        holder.SamplePhoto.setImageBitmap(bitmap);
    }catch (Exception e){
        e.printStackTrace();
    }
    //holder.Carat.setText();
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
