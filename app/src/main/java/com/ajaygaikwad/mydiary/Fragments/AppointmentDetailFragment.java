package com.ajaygaikwad.mydiary.Fragments;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ajaygaikwad.mydiary.Adapter.AdapterDateDetails;
import com.ajaygaikwad.mydiary.Classes.MyApplication;
import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.SlidingRootNavActivity;
import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.ajaygaikwad.mydiary.pojo.DetailsItem;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentDetailFragment extends Fragment {

    private ProgressDialog progressBar;
    private int progressBarStatus;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    RecyclerView recyclerView;
    List<DetailsItem> list1;
    AdapterDateDetails adapterDateDetails;
    LinearLayout linearlayout1,linearlayout2;
    String nodata="";
    SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;
    ShimmerFrameLayout shimmerFrameLayout;


    TextView tvCredit,tvExpense,tvBorrowTo,tvBorrowFrom;

    public AppointmentDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_appointment_detail, container, false);
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor=preferences.edit();
        linearlayout1=v.findViewById(R.id.linearlayout1);
        linearlayout2=v.findViewById(R.id.linearlayout2);
        recyclerView = v.findViewById(R.id.recycler_view);
        tvBorrowTo=v.findViewById(R.id.tvBorrowTo);
        tvBorrowFrom=v.findViewById(R.id.tvBorrowFrom);
        tvCredit=v.findViewById(R.id.tvCredit);
        tvExpense=v.findViewById(R.id.tvExpense);

        shimmerFrameLayout = v.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        list1 = new ArrayList<DetailsItem>();
        setHasOptionsMenu(true);
        getDetails();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shimmerFrameLayout.startShimmer();
                adapterDateDetails.clear();
                adapterDateDetails.notifyDataSetChanged();
                getDetails();
                nodata="";
            }
        });
    return v;}
    
    private void getDetails(){
        final int[] countCredit = {0};
        final int[] countExpense = { 0 };
        final int[] countBorrowFrom = { 0 };
        final int[] countBorrowTo = { 0 };


        StringRequest postRequest1 = new StringRequest(Request.Method.POST, Config.GET_DATE_DETAIL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            JSONObject object=new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray("details");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = jsonArray.getJSONObject(i);
                                String  srno = j1.getString("srno");
                                String  date = j1.getString("date");
                                String  dealerName = j1.getString("dealer_name");

                                String  customerMobile = j1.getString("customer_mobile");
                                String  propType = j1.getString("prop_type");
                                String  description = j1.getString("description");
                                String  city = j1.getString("city");
                                String  photo = j1.getString("photo");
                                String  onlytime = j1.getString("onlytime");
                                nodata="1";



                                DetailsItem obj = new DetailsItem(srno,date,customerMobile,city,dealerName,description,propType,photo,onlytime);
                                list1.add(obj);

                                if(propType.equalsIgnoreCase("Credit")){
                                    countCredit[0] = countCredit[0] +Integer.parseInt(customerMobile);
                                }
                                if(propType.equalsIgnoreCase("Expense")){
                                    countExpense[0] = countExpense[0] +Integer.parseInt(customerMobile);
                                }
                                if(propType.equalsIgnoreCase("Borrow To")){
                                    countBorrowTo[0] = countBorrowTo[0] +Integer.parseInt(customerMobile);
                                }
                                if(propType.equalsIgnoreCase("Borrow From")){
                                    countBorrowFrom[0] = countBorrowFrom[0] +Integer.parseInt(customerMobile);
                                }

                                tvCredit.setText("Credit \n ₹" +String.valueOf(countCredit[0]));
                                tvExpense.setText("Expense \n ₹" +String.valueOf(countExpense[0]));
                                tvBorrowTo.setText("Borrow To \n ₹" +String.valueOf(countBorrowTo[0]));
                                tvBorrowFrom.setText("Borrow From\n ₹" +String.valueOf(countBorrowFrom[0]));

                            }

                            if(nodata.equals("")){
                                linearlayout1.setVisibility(View.GONE);
                                linearlayout2.setVisibility(View.VISIBLE);
                            }
                            adapterDateDetails=new AdapterDateDetails(getActivity(),list1);
                            recyclerView.setAdapter(adapterDateDetails);


                        }
                        catch (Exception e) {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            e.printStackTrace();
                            //Toast.makeText(getActivity(), "No Details on this date", Toast.LENGTH_SHORT).show();
                            Toasty.error(getActivity(), "No Details on this date").show();


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                Toasty.error(getActivity(), "Error Connecting To Server").show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("name",et_name.getText().toString());
                params.put("date", preferences.getString("SelecterDate",""));
                params.put("mobile", new SharedPref(getActivity()).getMobile());


                //params.put("firebase_id",firebase_id);

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

        progressBar = new ProgressDialog(getActivity());
        progressBar.setCancelable(false);
        progressBar.setMessage(getString(R.string.pleaseWait));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
    }

    public void onResume(){
        super.onResume();

        try{
            ((MainNavActivity)getActivity()).setActionBarTitle(preferences.getString("SelecterDate",""));
        }catch (Exception e){
            ((SlidingRootNavActivity)getActivity()).setActionBarTitle(preferences.getString("SelecterDate",""));
        }
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
                adapterDateDetails.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapterDateDetails.getFilter().filter(query);
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