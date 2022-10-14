package com.example.dash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockSearchAdapter extends RecyclerView.Adapter<StockSearchAdapter.ViewHolder> {
    public static int mExpandedPosition = -1;
    public static int previousExpandedPosition = -1;
    private final List<Label> stocklist;
    private OnItemClickListener mListener;

    public StockSearchAdapter(List<Label> stocklist) {
        this.stocklist = stocklist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_search_list,parent,false);
        return new ViewHolder(view);
    }
    public interface OnItemClickListener{

        void RequestInventoryLabel(Label label);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final boolean isExpanded = holder.getAdapterPosition()==mExpandedPosition;
        holder.PrintLabel.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);

        if (isExpanded)
            previousExpandedPosition = holder.getAdapterPosition();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        if(holder.PrintLabel.getVisibility()==View.VISIBLE){
            holder.PrintLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.RequestInventoryLabel(stocklist.get(holder.getAdapterPosition()));
                }
            });
        }

        holder.SrNo.setText(String.valueOf(position+1));
        holder.barcode.setText(stocklist.get(position).getBarcode());
        holder.itemname.setText(stocklist.get(position).getName());
        holder.gw.setText(stocklist.get(position).getGW());
        holder.nw.setText(stocklist.get(position).getNW());
        holder.ec.setText(stocklist.get(position).getEC());
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
        Button PrintLabel;
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
            huid = mView.findViewById(R.id.HUID);
            PrintLabel = mView.findViewById(R.id.PrintLabel);
        }
    }
}
