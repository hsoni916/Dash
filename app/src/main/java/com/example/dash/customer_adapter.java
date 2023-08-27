package com.example.dash;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class customer_adapter extends RecyclerView.Adapter<customer_adapter.ViewHolder>{
    private final List<Customer> recordList;
    private OnItemClickListener mListener;
    private Context parentContext;

    public customer_adapter(List<Customer> recordList, Context context) {
        this.recordList = recordList;
        this.parentContext = context;
    }

    public interface OnItemClickListener{
        boolean onItemClick(int position);

    }

    void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_row,parent,false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Name.setText(recordList.get(position).getName());
        holder.Phone.setText(recordList.get(position).getPhoneNumber());
        holder.DOB.setText(String.valueOf(recordList.get(position).getDateOfBirth()));
        DBManager dbManager = new DBManager(parentContext);
        dbManager.open();
        holder.Balance.setText(String.valueOf(dbManager.getBalance(recordList.get(position).getName(),recordList.get(position).getPhoneNumber())));
        holder.MetalBalance.setText(String.valueOf(dbManager.getMetalBalance(recordList.get(position).getName(),recordList.get(position).getPhoneNumber())));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Context context;
        TextView Name, Phone, DOB, Balance, MetalBalance;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mView = itemView;
            context = context;
            Name = mView.findViewById(R.id.Name);
            Phone = mView.findViewById(R.id.Phone);
            DOB = mView.findViewById(R.id.DOB);
            Balance = mView.findViewById(R.id.Balance);
            MetalBalance = mView.findViewById(R.id.MetalBalance);
        }
    }
}
