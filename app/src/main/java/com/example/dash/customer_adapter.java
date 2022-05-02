package com.example.dash;

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

    public customer_adapter(List<Customer> recordList) {
        this.recordList = recordList;
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Name.setText(recordList.get(position).getName());
        holder.Phone.setText(recordList.get(position).getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView Name, Phone, DOB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            Name = mView.findViewById(R.id.Name);
            Phone = mView.findViewById(R.id.Phone);
        }
    }
}
