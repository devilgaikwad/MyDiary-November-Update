package com.ajaygaikwad.mydiary.Fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ajaygaikwad.mydiary.Adapter.AllDetailsAdapter;
import com.ajaygaikwad.mydiary.Classes.MyApplication;
import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.ajaygaikwad.mydiary.pojo.AppointmentItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

import es.dmoral.toasty.Toasty;
import hari.bounceview.BounceView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllAppointmentFragment extends Fragment {

    RecyclerView recycler_view_all_appointment;
    List<AppointmentItem> list1 ;
    AllDetailsAdapter allDetailsAdapter;
    TextView tvCredit,tvExpense,tvBorrowTo,tvBorrowFrom;
    SearchView searchView;

    private ProgressDialog progressBar;
    private int progressBarStatus;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView fromDate,toDate;
    ActionProcessButton btnGet;
    private DatePickerDialog.OnDateSetListener fromdateSetListener;
    private DatePickerDialog.OnDateSetListener todateSetListener;
    String strfromDate="",strtoDate="";




    public AllAppointmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_all_appointment, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        setHasOptionsMenu(true);
        recycler_view_all_appointment=v.findViewById(R.id.recycler_view_all_appointment);
        list1=new ArrayList<AppointmentItem>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view_all_appointment.setLayoutManager(mLayoutManager);
        tvBorrowTo=v.findViewById(R.id.tvBorrowTo);
        tvBorrowFrom=v.findViewById(R.id.tvBorrowFrom);
        tvCredit=v.findViewById(R.id.tvCredit);
        tvExpense=v.findViewById(R.id.tvExpense);
        fromDate=v.findViewById(R.id.tvFromDate);
        toDate=v.findViewById(R.id.tvToDate);
        btnGet=v.findViewById(R.id.btnGet);
        BounceView.addAnimTo(btnGet);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        Bundle extras = getArguments();
        list1  = extras.getParcelableArrayList("ARRAYLIST");
        tvCredit.setText(preferences.getString("prefCredit",""));
        tvExpense.setText(preferences.getString("prefExpense",""));
        tvBorrowTo.setText(preferences.getString("prefBorrowTo",""));
        tvBorrowFrom.setText(preferences.getString("prefBorrowFrom",""));

        toDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        strtoDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if(extras != null){
            if(extras.getString("viewDetails","").equals("month")){

                fromDate.setText(new SimpleDateFormat("01/MM/yyyy", Locale.getDefault()).format(new Date()));
                strfromDate = new SimpleDateFormat("yyyy-MM-01", Locale.getDefault()).format(new Date());
                getDateMethod();

            }
            if(extras.getString("viewDetails","").equals("all")){
                getAllData();
            }
            if(extras.getString("viewDetails","").equals("today")){

                fromDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
                strfromDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                getDateMethod();

            }
        }
        allDetailsAdapter = new AllDetailsAdapter(getActivity(),list1);
        recycler_view_all_appointment.setAdapter(allDetailsAdapter);

        //getAllData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allDetailsAdapter.clear();
                allDetailsAdapter.notifyDataSetChanged();
                getAllData();

            }
        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGet.setProgress(0);
                btnGet.setEnabled(true);
                Calendar cal1 = Calendar.getInstance();
                final int year1 = cal1.get(Calendar.YEAR);
                final int month1 = cal1.get(Calendar.MONTH);
                final int day1 = cal1.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog
                        (getActivity(), fromdateSetListener,
                                year1, month1, day1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                //dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
        fromdateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;

                strfromDate =year+"-"+month+"-"+day;
                String date1 =day+"/"+month+"/"+year;
                fromDate.setText(date1);

            }
        };

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGet.setProgress(0);
                btnGet.setEnabled(true);
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog
                        (getActivity(), todateSetListener,
                                year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                //dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();

            }
        });

        todateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;

                strtoDate =year+"-"+month+"-"+day;
                String date1 =day+"/"+month+"/"+year;
                toDate.setText(date1);

            }
        };

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDateMethod();
            }
        });


        return v;
    }



    private void getDateMethod() {

        if(strfromDate.equals("")){
            Toasty.warning(getActivity(), "Select From Date").show();
            return;
        }

        if(strtoDate.equals("")){
            Toasty.warning(getActivity(), "Select To Date").show();
            return;
        }

        try{
            allDetailsAdapter.clear();
            allDetailsAdapter.notifyDataSetChanged();
        }catch (Exception c){}

        btnGet.setProgress(1);
        btnGet.setEnabled(false);
        btnGet.setText("Loading..");

        final int[] countCredit = {0};
        final int[] countExpense = { 0 };
        final int[] countBorrowTo = { 0 };
        final int[] countBorrowFrom = { 0 };
       // progressDiaglog();

        StringRequest postreq = new StringRequest(Request.Method.POST, Config.GET_APPOINTMENT_BTW_DATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    btnGet.setProgress(100);
                    btnGet.setEnabled(true);
                    btnGet.setText("Success");
                    //progressBar.dismiss();
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("appointment");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j1 = jsonArray.getJSONObject(i);
                        String date = j1.getString("date");
                        String name = j1.getString("dealer_name");
                        String amount = j1.getString("cust_no");
                        String businessType = j1.getString("prop_type");
                        String desc = j1.getString("desc");
                        String city = j1.getString("city");
                        String photo = j1.getString("photo");
                        String onlytime = j1.getString("onlytime");

                        ///datesList.add(date);

                        AppointmentItem obj = new AppointmentItem(date, name,  amount,  businessType,  desc,  city, photo, onlytime);
                        list1.add(obj);

                        if(businessType.equalsIgnoreCase("Credit")){
                            countCredit[0] = countCredit[0] +Integer.parseInt(amount);
                        }
                        if(businessType.equalsIgnoreCase("Expense")){
                            countExpense[0] = countExpense[0] +Integer.parseInt(amount);
                        }
                        if(businessType.equalsIgnoreCase("Borrow To")){
                            countBorrowTo[0] = countBorrowTo[0] +Integer.parseInt(amount);
                        }
                        if(businessType.equalsIgnoreCase("Borrow From")){
                            countBorrowFrom[0] = countBorrowFrom[0] +Integer.parseInt(amount);
                        }
                    }
                    allDetailsAdapter = new AllDetailsAdapter(getActivity(),list1);
                    recycler_view_all_appointment.setAdapter(allDetailsAdapter);
                    //setCustomdate();
                    tvCredit.setText("Credit \n ₹" +String.valueOf(countCredit[0]));
                    tvExpense.setText("Expense \n ₹" +String.valueOf(countExpense[0]));
                    tvBorrowTo.setText("Borrow To \n ₹" +String.valueOf(countBorrowTo[0]));
                    tvBorrowFrom.setText("Borrow From\n ₹" +String.valueOf(countBorrowFrom[0]));

                } catch (Exception e1) {
                    e1.getStackTrace();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    btnGet.setProgress(-1);
                    btnGet.setEnabled(true);
                    btnGet.setText("Error");
                    //progressBar.dismiss();
                    Toasty.error(getActivity(), "Error retrive Data").show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.dismiss();
                btnGet.setProgress(-1);
                btnGet.setEnabled(true);
                btnGet.setText("Error");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                Toasty.error(getActivity(), "Error Connecting to server").show();
                //alertBox();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();

                //map.put("sno", editsno.getText().toString());
                //  map.put("filtered_price",filtered_price);
                map.put("mobile", new SharedPref(getActivity()).getMobile());
                map.put("fromDate", strfromDate);
                map.put("toDate", strtoDate);

                return map;
            }
        };
        MyApplication.getInstance().addToReqQueue(postreq);


    }

    public void progressDiaglog(){

        progressBar = new ProgressDialog(getActivity());
        progressBar.setCancelable(false);
        progressBar.setMessage(getString(R.string.pleaseWait));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
    }

    private void getAllData() {
        final int[] countCredit = {0};
        final int[] countExpense = { 0 };
        final int[] countBorrowTo = { 0 };
        final int[] countBorrowFrom = { 0 };
        progressDiaglog();

        StringRequest postreq = new StringRequest(Request.Method.POST, Config.GET_APPOINTMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    progressBar.dismiss();
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("appointment");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j1 = jsonArray.getJSONObject(i);
                        String date = j1.getString("date");
                        String name = j1.getString("dealer_name");
                        String amount = j1.getString("cust_no");
                        String businessType = j1.getString("prop_type");
                        String desc = j1.getString("desc");
                        String city = j1.getString("city");
                        String photo = j1.getString("photo");
                        String onlytime = j1.getString("onlytime");

                        ///datesList.add(date);

                        AppointmentItem obj = new AppointmentItem(date, name,  amount,  businessType,  desc,  city, photo , onlytime);
                        list1.add(obj);

                        if(businessType.equalsIgnoreCase("Credit")){
                             countCredit[0] = countCredit[0] +Integer.parseInt(amount);
                        }
                        if(businessType.equalsIgnoreCase("Expense")){
                            countExpense[0] = countExpense[0] +Integer.parseInt(amount);
                        }
                        if(businessType.equalsIgnoreCase("Borrow To")){
                            countBorrowTo[0] = countBorrowTo[0] +Integer.parseInt(amount);
                        }
                        if(businessType.equalsIgnoreCase("Borrow From")){
                            countBorrowFrom[0] = countBorrowFrom[0] +Integer.parseInt(amount);
                        }
                    }
                    allDetailsAdapter = new AllDetailsAdapter(getActivity(),list1);
                    recycler_view_all_appointment.setAdapter(allDetailsAdapter);
                    //setCustomdate();
                    tvCredit.setText("Credit \n ₹" +String.valueOf(countCredit[0]));
                    tvExpense.setText("Expense \n ₹" +String.valueOf(countExpense[0]));
                    tvBorrowTo.setText("Borrow To \n ₹" +String.valueOf(countBorrowTo[0]));
                    tvBorrowFrom.setText("Borrow From\n ₹" +String.valueOf(countBorrowFrom[0]));

                } catch (Exception e1) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    progressBar.dismiss();
                    //Toast.makeText(getActivity(), "Error retrive Data", Toast.LENGTH_LONG).show();

                    Toasty.error(getActivity(), "Error retrive Data").show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                //Toast.makeText(getActivity(), "Error Connecting to server", Toast.LENGTH_SHORT).show();

                Toasty.error(getActivity(), "Error Connecting to server").show();
                //alertBox();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();

                //map.put("sno", editsno.getText().toString());
                //  map.put("filtered_price",filtered_price);
                map.put("mobile", new SharedPref(getActivity()).getMobile());

                return map;
            }
        };
        MyApplication.getInstance().addToReqQueue(postreq);

    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                allDetailsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                allDetailsAdapter.getFilter().filter(query);
                return false;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
