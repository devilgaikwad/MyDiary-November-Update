package com.ajaygaikwad.mydiary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ajaygaikwad.mydiary.AttachImage;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.pojo.AppointmentItem;
import com.ajaygaikwad.mydiary.pojo.DetailsItem;

import java.util.ArrayList;
import java.util.List;

public class AllDetailsAdapter extends RecyclerView.Adapter<AllDetailsAdapter.MyViewHolder> implements Filterable {
    Context context;
    List<AppointmentItem> list1 ;
    List<AppointmentItem> list2 ;
    public AllDetailsAdapter(Context context, List<AppointmentItem> list) {
        this.list1 = list;
        this.list2 = list;
        this.context=context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final AppointmentItem pojo = list1.get(position);
        holder.description.setText("Description = "+pojo.getDesc());
        holder.custoNo.setText("Amount = "+pojo.getCustNo());
        holder.dealerName.setText("Name = "+pojo.getDealerName());
        holder.date11.setText("Date = "+pojo.getDate()+"  "+pojo.getOnlytime());
        holder.type.setText("Type = "+pojo.getPropType());
        holder.addAppointmentItem(pojo);

        if(pojo.getPhoto().trim().isEmpty()){
            holder.imgAttach.setVisibility(View.GONE);
        }else{
            holder.imgAttach.setVisibility(View.VISIBLE);
            holder.imgAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent in = new Intent(context, AttachImage.class);
                    in.putExtra("Photo", pojo.getPhoto());
                    context.startActivity(in);
                }
            });
        }


        if(pojo.getPropType().equals("Credit")){
            holder.custoNo.setTextColor(Color.GREEN);
            holder.custoNo.setText("  +₹"+pojo.getCustNo()+" Credited By "+pojo.getDealerName());
        }
        if(pojo.getPropType().equals("Expense")){
            holder.custoNo.setTextColor(Color.RED);
            holder.custoNo.setText("  -₹"+pojo.getCustNo()+" Expense By "+pojo.getDealerName());
        }
        if(pojo.getPropType().equals("Borrow To")){
            holder.custoNo.setTextColor(Color.MAGENTA);
            holder.custoNo.setText("  -₹"+pojo.getCustNo()+" Borrow To "+pojo.getDealerName());
        }
        if(pojo.getPropType().equals("Borrow From")){
            holder.custoNo.setTextColor(Color.BLUE);
            holder.custoNo.setText("  +₹"+pojo.getCustNo()+" Borrow From "+pojo.getDealerName());
        }


    }



    @Override
    public int getItemCount() {
        return list1.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    list1 = list2;
                } else {
                    List<AppointmentItem> filteredList = new ArrayList<>();
                    for (AppointmentItem row : list2) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.desc.toLowerCase().contains(charString.toLowerCase()) || row.dealerName.toLowerCase().contains(charString.toLowerCase()) || row.propType.toLowerCase().contains(charString.toLowerCase()) || row.custNo.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    list1 = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list1;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list1 = (ArrayList<AppointmentItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dealerName,custoName,propID,custoNo,description,date11,type;
        ImageView imgCall,imgAttach;
        CardView cardID;
        Button reshedule_prop,delete_prop;

        public MyViewHolder(View itemView) {

            super(itemView);

            dealerName=itemView.findViewById(R.id.dealerName);

            custoNo=itemView.findViewById(R.id.custoNo);

            description=itemView.findViewById(R.id.description);
            cardID=itemView.findViewById(R.id.cardID);
            reshedule_prop=itemView.findViewById(R.id.reshedule_prop);
            delete_prop=itemView.findViewById(R.id.delete_prop);
            date11=itemView.findViewById(R.id.date11);
            type=itemView.findViewById(R.id.type);
            imgAttach=itemView.findViewById(R.id.imgAttach);
            reshedule_prop.setVisibility(View.GONE);
            delete_prop.setVisibility(View.GONE);
        }

        AppointmentItem appointmentItem;
         void addAppointmentItem(AppointmentItem pojo) {
            this.appointmentItem=pojo;
        }
    }
    public void clear(){
        list1.clear();
        notifyDataSetChanged();
    }
}
