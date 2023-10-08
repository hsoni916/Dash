package com.example.dash;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    private List<OrderDetails> CurrentOrders;
    private OnItemClickListener mListener;
    private Context parentContext;

    public OrderListAdapter(List<OrderDetails> Orders, Context context) {
        this.CurrentOrders= Orders;
        this.parentContext = context;
        Log.d("Date:","Constructor");
    }

    public interface OnItemClickListener{
        boolean onItemClick(int position);

    }
    void setOnItemClickListener(OrderListAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("Date:","OnBindView");
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.getAdapterPosition());
            }
        });
        holder.OrderNumber.setText(CurrentOrders.get(position).getOrderNumber());
        holder.Customer.setText(CurrentOrders.get(position).getCustomerName());
        String itemText = "";
        if(CurrentOrders.get(position).getItems()!=null){
            if(CurrentOrders.get(position).getItems().size()>1){
                itemText = CurrentOrders.get(position).getItems().size() + "Items";
            }else{
                itemText = CurrentOrders.get(position).getItems().size() + "Item";
            }
        }
        holder.Items.setText(itemText);
        holder.OrderDate.setText(CurrentOrders.get(position).getOrderDate());
        holder.DeliveryDate.setText(CurrentOrders.get(position).getDeliveryDate());
        String ODD = CurrentOrders.get(position).getDeliveryDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String Today = dateFormat.format(Calendar.getInstance().getTime());
        try{
            Date date = dateFormat.parse(ODD);
            Date TodayDate = dateFormat.parse(Today);
            long diff = date.getTime() - TodayDate.getTime();
            long diffDays = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
            Log.d("Date:", String.valueOf(date));
            Log.d("Date:",String.valueOf(TodayDate));
            Log.d("Date:", String.valueOf(diff));
            Log.d("Date:",String.valueOf(diffDays));
            if(diffDays<0){
                Log.d("less"," than zero");
                holder.DueView.setVisibility(View.VISIBLE);
                String alerts = "Overdue by" + diffDays + " days.";
                holder.DueView.setText(alerts);
            }
            if(diffDays>1){
                Log.d("more"," than one");
                holder.DueView.setVisibility(View.VISIBLE);
                String alerts = "Due in " + diffDays + " days.";
                Log.d("Alert:",alerts);
                holder.DueView.setText(alerts);
            }
            if(diffDays==1){
                Log.d("equal"," to one");
                holder.DueView.setVisibility(View.VISIBLE);
                holder.DueView.setText(R.string.due_tomorrow);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return CurrentOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Context context;

        TextView OrderNumber,Customer, Items, OrderDate, DeliveryDate, DueView;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mView = itemView;
            context = context;
            OrderNumber = mView.findViewById(R.id.OrderNumber);
            Customer = mView.findViewById(R.id.CustomerName);
            Items = mView.findViewById(R.id.ItemCount);
            OrderDate = mView.findViewById(R.id.OrderDate);
            DeliveryDate = mView.findViewById(R.id.DeliveryDate);
            DueView = mView.findViewById(R.id.alertView);
        }
    }
}
