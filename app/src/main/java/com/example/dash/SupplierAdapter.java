package com.example.dash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    List<Supplier> supplierList;
    List<DocumentReference> documentReferenceList;
    private OnItemClicked mListener;

    public SupplierAdapter(List<Supplier> supplierList) {
        this.supplierList = supplierList;
    }
    public void DocReference(List<DocumentReference> documentReferenceList){
        this.documentReferenceList = documentReferenceList;
    }
    //make interface like this
    public interface OnItemClicked {
       //What happens when supplier is clicked.
        void EditDetails(Supplier supplier, DocumentReference documentReference, int adapterPosition);

        boolean Delete(Supplier supplier, DocumentReference documentReference);
    }

    void setOnItemClickListener(OnItemClicked listener){
        this.mListener = listener;
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
        holder.Owner.setText(supplierList.get(position).owner);
        holder.Firm.setText(supplierList.get(position).business);
        holder.GST.setText(supplierList.get(position).gst);
        if(supplierList.get(position).getCategories()!=null){
            holder.Categories.setText(supplierList.get(position).
                    getCategories().toString().replaceAll("[\\Q[\\E]?[\\Q]\\E]?",""));
        }
        holder.AddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.EditDetails(supplierList.get(holder.getAdapterPosition()), documentReferenceList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
                if(mListener.Delete(supplierList.get(holder.getAdapterPosition()),documentReferenceList.get(holder.getAdapterPosition()))){
                    Toast.makeText(view.getContext(), "Delete Successful.",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(view.getContext(), "Delete failed.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView SrNo, Owner, GST, Firm, Categories;
        Button AddDetails;
        ImageButton Delete;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            progressBar = mView.findViewById(R.id.supplier_row_pb);
            SrNo = mView.findViewById(R.id.SerialNumber);
            Owner = mView.findViewById(R.id.OwnerName);
            GST = mView.findViewById(R.id.GSTIN);
            Firm = mView.findViewById(R.id.BusinessName);
            AddDetails = mView.findViewById(R.id.EditDetails);
            Delete = mView.findViewById(R.id.DeleteSupplier);
            Categories = mView.findViewById(R.id.Category);
        }
    }
}
