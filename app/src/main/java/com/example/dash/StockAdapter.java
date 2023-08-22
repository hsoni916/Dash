package com.example.dash;

import android.content.Context;
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
    private Context parentContext;

    public StockAdapter(List<Label> sundrystocklist, Context context ) {
        this.stocklist = sundrystocklist;
        this.parentContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view1 = layoutInflater.inflate(R.layout.stock_entry_list, parent, false);
        return new ViewHolder(view1);
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
        DBManager dbManager = new DBManager(parentContext);
        dbManager.open();
        String wastage = String.valueOf(dbManager.getTouch(stocklist.get(position).getName(),stocklist.get(position).getBarcode()));
        holder.touch.setText(wastage);
        holder.purity.setText(stocklist.get(position).getHMStandard());
        holder.huid.setText(stocklist.get(position).getHUID());
    }

    @Override
    public int getItemCount() {
        return stocklist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView SrNo,barcode,itemname,purity,gw,nw,ec,touch,huid;
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
            touch = mView.findViewById(R.id.Costing);
            huid = mView.findViewById(R.id.HUIDLabel);
        }
    }
}
