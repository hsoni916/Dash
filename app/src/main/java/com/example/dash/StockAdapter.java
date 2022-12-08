package com.example.dash;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder>{

    private final List<Label> stocklist;

    public StockAdapter(List<Label> sundrystocklist ) {
        this.stocklist = sundrystocklist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_entry_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.SrNo.setText(String.valueOf(position+1));
        holder.barcode.setText(stocklist.get(position).getBarcode());
        holder.itemname.setText(stocklist.get(position).getName());
        holder.gw.setText(stocklist.get(position).getGW());
        holder.nw.setText(stocklist.get(position).getNW());
        holder.ec.setText(stocklist.get(position).getEC());
        Log.d("HM Standard",stocklist.get(position).getHMStandard());
        holder.purity.setText(stocklist.get(position).getHMStandard());
        holder.huid.setText(stocklist.get(position).getHUID());
    }

    @Override
    public int getItemCount() {
        return stocklist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView SrNo,barcode,itemname,purity,gw,nw,ec,huid;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            SrNo = mView.findViewById(R.id.Sr);
            itemname = mView.findViewById(R.id.Name);
            purity = mView.findViewById(R.id.Purity);
            barcode = mView.findViewById(R.id.Barcode);
            gw = mView.findViewById(R.id.GW);
            nw = mView.findViewById(R.id.NW);
            ec = mView.findViewById(R.id.EC);
            huid = mView.findViewById(R.id.HUIDLabel);
        }
    }
}
