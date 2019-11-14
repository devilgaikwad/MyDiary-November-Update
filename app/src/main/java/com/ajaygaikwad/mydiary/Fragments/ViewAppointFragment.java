package com.ajaygaikwad.mydiary.Fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ajaygaikwad.mydiary.Classes.MyApplication;
import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.SlidingRootNavActivity;
import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.ajaygaikwad.mydiary.pojo.AppointmentItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAppointFragment extends Fragment {
    CaldroidFragment caldroidFragment;
    ArrayList<String> datesList;
    private ProgressDialog progressBar;
    private int progressBarStatus;
    View v;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<AppointmentItem> list;
    private AdView mAdView;

    public ViewAppointFragment() {
        // Required empty public constructor
    }

    public void onResume(){
        super.onResume();

        try{
            ((MainNavActivity)getActivity()).setActionBarTitle("View Diary");
        }
        catch (Exception e){
            ((SlidingRootNavActivity)getActivity()).setActionBarTitle("View Diary");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_view_appoint, container, false);
        list = new ArrayList<AppointmentItem>();
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor=preferences.edit();
        datesList = new ArrayList<String>();
        datesList.clear();
        caldroidFragment = new CaldroidFragment();
        Button listView= v.findViewById(R.id.listView);
        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("ARRAYLIST", list);

                Fragment fragment = new AllAppointmentFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                try {
                    ft.replace(R.id.fmain,  fragment).addToBackStack("tag").commit();
                }catch (Exception e){
                    ft.replace(R.id.container,  fragment).addToBackStack("tag").commit();
                }
            }
        });
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        getAppointmentMethod();

        InitCalendar(savedInstanceState);

        //setCustomdate();
        return v;
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

    private void InitCalendar(Bundle savedInstanceState) {
        if (savedInstanceState != null)
        {
            caldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
        }
        Bundle args = new Bundle();

        Calendar cal = Calendar.getInstance();

        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
        caldroidFragment.setArguments(args);

        try {
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.replace(R.id.assignmentCalendar, caldroidFragment).disallowAddToBackStack();
            t.commit();
        }catch (Exception e){}
        // Setup listener
        final CaldroidListener listener = new CaldroidListener()
        {

            @Override
            public void onSelectDate(Date date, View view)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String selectedDate = sdf.format(date);

               /* new SessionManager(getActivity()).setAssignmentDate(selectedDate);
                getFragmentManager().beginTransaction().addToBackStack("").replace(R.id.content_frame, new AssignmentDetailFragment()).commit();*/
                editor.putString("SelecterDate",selectedDate);
                editor.commit();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();


                if(preferences.getString("view","").equals("2")){
                    ft.replace(R.id.container,  new AppointmentDetailFragment()).addToBackStack("tag").commit();
                }else{
                    ft.replace(R.id.fmain,  new AppointmentDetailFragment()).addToBackStack("tag").commit();
                }

            }

            @Override
            public void onChangeMonth(int month, int year)
            {
                String text = "month: " + month + " year: " + year;
            }

            @Override
            public void onLongClickDate(Date date, View view)
            {
            }

            @Override
            public void onCaldroidViewCreated()
            {
                if (caldroidFragment.getLeftArrowButton() != null)
                {
                }
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

    }

    private void  setCustomdate(){

        for (int i=0;i<datesList.size();i++)
        {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate= null;
            try
            {
                startDate = df.parse(datesList.get(i));
                /*String newDateString = df.format(startDate);
                System.out.println(newDateString);*/
            } catch (java.text.ParseException e)
            {
                e.printStackTrace();
            }

            try {
                if (caldroidFragment != null)
                {
                    ColorDrawable calendarEventdate = new ColorDrawable(getActivity().getResources().getColor(R.color.greenn));
                    caldroidFragment.setBackgroundDrawableForDate(calendarEventdate, startDate);
                    caldroidFragment.setTextColorForDate(R.color.white, startDate);
                    caldroidFragment.refreshView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        caldroidFragment.refreshView();

        if(progressBar.isShowing()){
            progressBar.dismiss();
        }

    }



    public  void getAppointmentMethod(){
        final int[] countCredit = {0};
        final int[] countExpense = { 0 };
        final int[] countBorrowTo = { 0 };
        final int[] countBorrowFrom = { 0 };
        progressDiaglog();
        datesList.clear();

        StringRequest postreq = new StringRequest(Request.Method.POST, Config.GET_APPOINTMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

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



                        AppointmentItem obj =
                                new AppointmentItem(date, name,  amount,  businessType,  desc,  city, photo,onlytime);
                        list.add(obj);

                        if(businessType.equalsIgnoreCase("Credit")){
                            countCredit[0] = countCredit[0] +Integer.parseInt(amount);
                            datesList.add(date);
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

                        editor.putString("prefCredit","Credit \n ₹" +String.valueOf(countCredit[0]));
                        editor.putString("prefExpense","Expense \n ₹" +String.valueOf(countExpense[0]));
                        editor.putString("prefBorrowTo","Borrow To \n ₹" +String.valueOf(countBorrowTo[0]));
                        editor.putString("prefBorrowFrom","Borrow From\n ₹" +String.valueOf(countBorrowFrom[0]));
                        editor.commit();
                    }


                    setCustomdate();

                } catch (Exception e1) {
                    progressBar.dismiss();
                    Toasty.error(getActivity(), "Error retrive Data").show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();

                Toasty.error(getActivity(), "Error Connecting To Server").show();
                //alertBox();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();

                map.put("mobile", new SharedPref(getActivity()).getMobile());
                //  map.put("filtered_price",filtered_price);

                return map;
            }
        };
        MyApplication.getInstance().addToReqQueue(postreq);


    }
}

