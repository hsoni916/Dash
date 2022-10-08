package com.example.dash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    }

    @Override
    public int getItemCount() {
        return stocklist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView rowid,barcode,itemname,purity,gw,nw,ec,huid;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            rowid = mView.findViewById(R.id.Sr);
            itemname = mView.findViewById(R.id.Name);
            purity = mView.findViewById(R.id.Purity);
            barcode = mView.findViewById(R.id.Barcode);
            gw = mView.findViewById(R.id.GW);
            nw = mView.findViewById(R.id.NW);
            ec = mView.findViewById(R.id.EC);
            huid = mView.findViewById(R.id.HUID);
        }
    }
}
