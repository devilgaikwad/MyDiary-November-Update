package com.ajaygaikwad.mydiary.Adapter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajaygaikwad.mydiary.AttachImage;
import com.ajaygaikwad.mydiary.Classes.MyApplication;
import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.ajaygaikwad.mydiary.pojo.DetailsItem;
import com.ajaygaikwad.mydiary.pojo.DetailsItem;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class AdapterDateDetails extends RecyclerView.Adapter<AdapterDateDetails.MyViewHolder> implements Filterable {

    Context context;
    List<DetailsItem> list1;
    List<DetailsItem> list2;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private ProgressDialog progressBar;
    private int progressBarStatus;

    String result11="0";
    onClick onClick;
    private DatePickerDialog.OnDateSetListener mdateSetListener;
    String dateSelected;

    public AdapterDateDetails(Context context, List<DetailsItem> list) {
        this.context=context;
        this.list1=list;
        this.list2=list;
        preferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=preferences.edit();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final DetailsItem pojo = list1.get(position);
        holder.description.setText("Description = "+pojo.getDescription());

        holder.date11.setText("Date = "+pojo.getDate()+"  "+pojo.getOnlytime());
        holder.dealerName.setText("Name = "+pojo.getDealerName());

        holder.type.setText("Type = "+pojo.getPropType());
        editor.putString("sno11",pojo.getSrno());
        editor.commit();


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
            holder.custoNo.setText("  +₹"+pojo.getCustomerMobile()+" Credited By "+pojo.getDealerName());
        }
        if(pojo.getPropType().equals("Expense")){
            holder.custoNo.setTextColor(Color.RED);
            holder.custoNo.setText("  -₹"+pojo.getCustomerMobile()+" Expense By "+pojo.getDealerName());
        }
        if(pojo.getPropType().equals("Borrow To")){
            holder.custoNo.setTextColor(Color.MAGENTA);
            holder.custoNo.setText("  -₹"+pojo.getCustomerMobile()+" Borrow To "+pojo.getDealerName());
        }
        if(pojo.getPropType().equals("Borrow From")){
            holder.custoNo.setTextColor(Color.BLUE);
            holder.custoNo.setText("  +₹"+pojo.getCustomerMobile()+" Borrow From "+pojo.getDealerName());
        }



        final String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        mdateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;

                String date =year+"-"+month+"-"+day;
                dateSelected = date+" "+timeStamp;
                holder.date11.setText("Update Date = "+date+" "+timeStamp);
                AlertBox1();
            }
        };
        holder.delete_prop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                    pDialog.setContentText("Details Deleted Permenantly");
                    pDialog.setTitle("Confirm Delete");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    pDialog.setConfirmButton("Confirm", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            pDialog.dismissWithAnimation();
                            delete_prop_method(pojo.getSrno());

                        }
                    });
                    pDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            pDialog.dismissWithAnimation();
                        }
                    });
                }catch (Exception e){
                    //delete_prop_method();
                }
                //delete_prop_method();
            }
        });

    }



    private void AlertBox1() {

        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitle("Confirm Resheduled");
        pDialog.setContentText("Are you sure to resheduled appointment to '"+dateSelected+"'");
        pDialog.show();
        pDialog.setConfirmButton("Sure", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismissWithAnimation();

            }
        });
        pDialog.setCancelable(false);
        pDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismissWithAnimation();
            }
        });
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
                    List<DetailsItem> filteredList = new ArrayList<>();
                    for (DetailsItem row : list2) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.description.toLowerCase().contains(charString.toLowerCase()) || row.dealerName.toLowerCase().contains(charString.toLowerCase()) || row.propType.toLowerCase().contains(charString.toLowerCase()) || row.customerMobile.toLowerCase().contains(charString.toLowerCase())) {
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
                list1 = (ArrayList<DetailsItem>) filterResults.values;
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
            type =itemView.findViewById(R.id.type);
            imgAttach =itemView.findViewById(R.id.imgAttach);
            reshedule_prop.setVisibility(View.GONE);

        }
    }


    private void AlertBox2(String sts) {
        try {

            final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
            if(sts.equals("d1")){
                pDialog.setTitle("Done");
                pDialog.setContentText("Deleted successfully");
            }else{
                pDialog.setTitle("Done");
                pDialog.setContentText("Details deleted successfully");
            }
            pDialog.setTitle("Done");
            pDialog.setContentText("Details resheduled successfully");
            pDialog.show();
            pDialog.setConfirmButton("Okey", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    pDialog.dismissWithAnimation();
                    Intent in = new Intent(context, MainNavActivity.class);
                    context.startActivity(in);

                }
            });
        }catch (Exception e){
            Intent in = new Intent(context, MainNavActivity.class);
            context.startActivity(in);
        }
    }

    private void delete_prop_method(final String srno) {

        progressDiaglog();
        StringRequest postRequest1 = new StringRequest(Request.Method.POST, Config.DELETE_PROP,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.dismiss();
                            JSONObject object=new JSONObject(response);
                             result11 = object.getString("success");

                            if (result11.equals("1")) {
                                //Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show();
                                Toasty.success(context, "Deleted Successfully").show();
                                AlertBox2("d1");



                            } else {
                                progressBar.dismiss();
                                //Toast.makeText(context, " Delete Details Unsuccessfull", Toast.LENGTH_SHORT).show();
                                Toasty.error(context, "Delete Details Unsuccessfull").show();
                            }

                        }
                        catch (Exception e) {
                            progressBar.dismiss();
                            e.printStackTrace();
                            //Toast.makeText(getActivity(), "No Appointment on this date", Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                //Toast.makeText(getActivity(),"Error Connecting To Server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("name",et_name.getText().toString());

                params.put("srno", srno);



                return params;
            }
        };

        // Adding request to request queue
        RetryPolicy policy = new DefaultRetryPolicy(90000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest1.setRetryPolicy(policy);
        postRequest1.setShouldCache(false);
        MyApplication.getInstance().addToReqQueue(postRequest1);

    }


    public void progressDiaglog(){

        progressBar = new ProgressDialog(context);
        progressBar.setCancelable(false);
        progressBar.setMessage(context.getString(R.string.pleaseWait));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
    }

    public void clear(){
        list1.clear();
        notifyDataSetChanged();
    }

    public void setOnClickListenerAdapter(onClick onClick) {
        this.onClick = onClick;
    }

    public interface onClick {
        public void onItemClick(AdapterDateDetails adapterDateDetails);
    }

}
