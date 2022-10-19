package com.example.dash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EstimateAdapter extends RecyclerView.Adapter<EstimateAdapter.ViewHolder> {


    public EstimateAdapter(List<String> records){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

        public class ViewHolder extends RecyclerView.ViewHolder {
            View mView;
            TextView rowid, itemname, gw, lw, nw, ec, ws;
            Button adddetails, delete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
                rowid = mView.findViewById(R.id.rowid);
                itemname = mView.findViewById(R.id.ItemName);
                gw = mView.findViewById(R.id.GWholder);
                lw = mView.findViewById(R.id.LWholder);
                nw = mView.findViewById(R.id.NWholder);
                ec = mView.findViewById(R.id.ECholder);
                ws = mView.findViewById(R.id.LabourHolder);
                adddetails = mView.findViewById(R.id.EditDetails);
                delete = mView.findViewById(R.id.RemoveItem);


            }
        }
    }
