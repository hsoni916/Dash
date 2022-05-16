package com.example.dash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private final List<SundryItem> sundryItemList;
    private OnItemClicked onClick;

    public ItemListAdapter(List<SundryItem> sundryItemList, OnItemClicked clicked) {
        this.sundryItemList = sundryItemList;
        this.onClick = clicked;
    }

    //make interface like this
    public interface OnItemClicked {
        void DeleteThisItem(int position);
        void AddDetails(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrow,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rowid.setText(String.valueOf(position+1));
        holder.itemname.setText(sundryItemList.get(position).getName());
        holder.gw.setText(String.valueOf(sundryItemList.get(position).getGW()));
        holder.lw.setText(String.valueOf(sundryItemList.get(position).getLW()));
        holder.nw.setText(String.valueOf(sundryItemList.get(position).getNW()));
        holder.ec.setText(String.valueOf(sundryItemList.get(position).getEC()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.DeleteThisItem(holder.getAdapterPosition());
            }
        });
        holder.adddetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.AddDetails(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return sundryItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView rowid,itemname,gw,lw,nw,ec;
        Button adddetails,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            rowid = mView.findViewById(R.id.rowid);
            itemname = mView.findViewById(R.id.ItemName);
            gw = mView.findViewById(R.id.GWholder);
            lw = mView.findViewById(R.id.LWholder);
            nw = mView.findViewById(R.id.NWholder);
            ec = mView.findViewById(R.id.ECholder);
            adddetails = mView.findViewById(R.id.EditDetails);
            delete = mView.findViewById(R.id.RemoveItem);


        }
    }
}
