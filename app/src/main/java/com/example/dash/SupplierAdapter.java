package com.example.dash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    List<Supplier> supplierList;


    private OnItemClicked onClick;

    public SupplierAdapter(List<Supplier> supplierList) {
        this.supplierList = supplierList;
    }

    //make interface like this
    public interface OnItemClicked {
       //What happens when supplier is clicked.
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_row,parent,false);
        return new SupplierAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.SrNo.setText(String.valueOf(position+1));
        holder.Owner.setText(supplierList.get(position).Owner);
        holder.Firm.setText(supplierList.get(position).Business);
        holder.GST.setText(supplierList.get(position).GST);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView SrNo, Owner, GST, Firm;
        Button AddDetails,Delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            SrNo = mView.findViewById(R.id.SerialNumber);
            Owner = mView.findViewById(R.id.OwnerName);
            GST = mView.findViewById(R.id.GSTIN);
            Firm = mView.findViewById(R.id.BusinessName);
            AddDetails = mView.findViewById(R.id.EditDetails);
            Delete = mView.findViewById(R.id.DeleteSupplier);
        }
    }
}
