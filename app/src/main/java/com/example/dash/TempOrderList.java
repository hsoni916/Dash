package com.example.dash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TempOrderList extends RecyclerView.Adapter<TempOrderList.ViewHolder> {

    private final List<Double> Sizes;
    private final List<String> Items;
    private final List<Double> Weights;
    private final List<String> Carats;
    private final OnItemClicked clicked;

    public TempOrderList(List<String> Items, List<Double> Weights, List<String> Carats , List<Double> Sizes, OnItemClicked clicked){
        this.Items = Items;
        this.Weights = Weights;
        this.Carats = Carats;
        this.Sizes = Sizes;
        this.clicked = clicked;

    }

    public interface OnItemClicked {
        void DeleteThisItem(int position);
        void EditDetails(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item_saved,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemname.setText(Items.get(position));
        holder.size.setText(String.valueOf(Sizes.get(position)));
        holder.carat.setText(Carats.get(position));
        holder.nw.setText(String.valueOf(Weights.get(position)));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked.DeleteThisItem(holder.getAdapterPosition());
            }
        });
        holder.editdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked.EditDetails(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView itemname,nw,carat,size;
        ImageButton editdetails,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            itemname = mView.findViewById(R.id.ItemName);
            nw = mView.findViewById(R.id.ItemWeight);
            carat = mView.findViewById(R.id.ItemCarat);
            size = mView.findViewById(R.id.ItemSize);
            editdetails = mView.findViewById(R.id.EditBtn);
            delete = mView.findViewById(R.id.DeleteBtn);
        }

    }
}
