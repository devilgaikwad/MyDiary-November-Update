package com.ajaygaikwad.mydiary.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ajaygaikwad.mydiary.Classes.MyApplication;
import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.MainActivity;
import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.SlidingRootNavActivity;
import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import hari.bounceview.BounceView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements LocationListener {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String url,firstName;

    LinearLayout updateLL;
    Button updateLeter,updateDownload;
    TextView tvAdd,tvView,tvToday,tvMonth,tvDayWish;

    private AdView mAdView;
    LocationManager locationManager;
    TextView tvLoc;
    ImageView ivLoc;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);
        preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        editor=preferences.edit();
        //MobileAds.initialize(getActivity(), "ca-app-pub-3864681863166960~2667252138");

       /* MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        updateLL=v.findViewById(R.id.updateLL);
        updateLeter=v.findViewById(R.id.updateLeter);
        updateDownload=v.findViewById(R.id.updateDownload);
        tvAdd=v.findViewById(R.id.tvAdd);
        tvView=v.findViewById(R.id.tvView);
        tvToday=v.findViewById(R.id.tvToday);
        tvMonth=v.findViewById(R.id.tvMonth);
        tvLoc=v.findViewById(R.id.tvLoc);
        ivLoc=v.findViewById(R.id.ivLoc);

        BounceView.addAnimTo(tvAdd);
        BounceView.addAnimTo(tvView);
        BounceView.addAnimTo(tvToday);
        BounceView.addAnimTo(tvMonth);
        tvDayWish=v.findViewById(R.id.tvDayWish);
        String name = new SharedPref(getActivity()).getUserName();
        String[] splitName = name.split(" ");
        if(splitName.length>1){
            firstName =splitName[0];
        }else{
            firstName = name;
        }


        String ss = preferences.getString("UpdateLL", "");
        if(ss.equals("1")){
            updateLL.setVisibility(View.VISIBLE);
        }else{
            updateLL.setVisibility(View.GONE);
        }

        updateLeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLL.setVisibility(View.GONE);
            }
        });
        updateDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("UpdateLL","0");
                editor.commit();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getResources().getString(R.string.urll)));
                startActivity(intent);

            }
        });

        geturl();

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                if(preferences.getString("view","").equals("2")){
                    ft.replace(R.id.container,  new AddAppointmentFragment()).addToBackStack("").commit();
                }else{
                    ft.replace(R.id.fmain,  new AddAppointmentFragment()).addToBackStack("").commit();
                }

            }
        });
        tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AllAppointmentFragment2();
                Bundle bundle = new Bundle();
                bundle.putString("viewDetails", "month");
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();


                if(preferences.getString("view","").equals("2")){
                    ft.replace(R.id.container,  fragment).addToBackStack("").commit();
                }else{
                    ft.replace(R.id.fmain,  fragment).addToBackStack("").commit();
                }


            }
        });
        tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AllAppointmentFragment2();
                Bundle bundle = new Bundle();
                bundle.putString("viewDetails", "all");
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                if(preferences.getString("view","").equals("2")){
                    ft.replace(R.id.container,  fragment).addToBackStack("").commit();
                }else{
                    ft.replace(R.id.fmain,  fragment).addToBackStack("").commit();
                }


            }
        });
        tvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AllAppointmentFragment2();
                Bundle bundle = new Bundle();
                bundle.putString("viewDetails", "today");
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                if(preferences.getString("view","").equals("2")){
                    ft.replace(R.id.container,  fragment).addToBackStack("").commit();
                }else{
                    ft.replace(R.id.fmain,  fragment).addToBackStack("").commit();
                }


            }
        });

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        dayWish(hour);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        btngetaddress2();

        return v;


    }
    void btngetaddress2() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void onLocationChanged(Location location) {
        ivLoc.setVisibility(View.VISIBLE);
        tvLoc.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tvLoc.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e) {

        }
        //Toast.makeText(getActivity(), "Location Saved",Toast.LENGTH_LONG).show();
    }
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    public void onProviderEnabled(String provider) {

    }


    public void dayWish(int hour){

        int flag = 0;
        if (hour < 4 && flag == 0) {
            flag = 1;
            tvDayWish.setText("Good Night " + firstName + "!");
        }
        if (hour < 12 && flag == 0) {
            flag = 1;
            tvDayWish.setText("Good Morning " + firstName + "!");
        }
        if (hour < 16 && flag == 0) {
            flag = 1;
            tvDayWish.setText("Good Afternoon " + firstName + "!");
        }
        if (hour < 24 && flag == 0) {
            flag = 1;
            tvDayWish.setText("Good Evening " + firstName + "!");
        }
    }


    public void  onResume(){
        super.onResume();
        try{
            ((MainNavActivity)getActivity()).setActionBarTitle("Home");
        }catch (Exception e){
            ((SlidingRootNavActivity)getActivity()).setActionBarTitle("Home");
        }
    }


    private void geturl() {

        StringRequest postreq = new StringRequest(Request.Method.POST, Config.GET_DEALER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //progressBar.dismiss();
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("jsonUrl");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j1 = jsonArray.getJSONObject(i);
                         url = j1.getString("name");

                        getMessage();
                    }
                    if(url.equals("banned") || url.equals("logout")){
                        new SharedPref(getActivity()).clearPref();
                        Intent in = new Intent(getActivity(), MainActivity.class);
                        startActivity(in);
                        getActivity().finish();
                    }

                } catch (Exception e1) {
                    //progressBar.dismiss();
                    Toasty.error(getActivity(), "Error retrive data").show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.dismiss();
                Toasty.error(getActivity(), "Error Connecting To Server").show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();

                //map.put("sno", editsno.getText().toString());
                map.put("mobile",new SharedPref(getActivity()).getMobile());

                return map;
            }
        };
        MyApplication.getInstance().addToReqQueue(postreq);

    }

    private void getMessage() {

        StringRequest postreq = new StringRequest(Request.Method.POST, Config.GET_MESSAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //progressBar.dismiss();
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject obj = jsonObject.getJSONArray("project").getJSONObject(0);

                    String srno  =  obj.getString("srno");
                    String title  =  obj.getString("title");
                    String message  =  obj.getString("message");
                    String btn  =  obj.getString("btn");
                    String project  =  obj.getString("project");

                    AlertForMessage(srno,title,message,btn);

                } catch (Exception e1) {
                    //progressBar.dismiss();
                    Toasty.error(getActivity(), "Error retrive Url").show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.dismiss();
                Toasty.error(getActivity(), "Error Connecting To Server").show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();

                //map.put("sno", editsno.getText().toString());
                map.put("project","diary");

                return map;
            }
        };
        MyApplication.getInstance().addToReqQueue(postreq);

    }

    private void AlertForMessage(final String srno, String title, String message, String btn) {


        if(btn.equalsIgnoreCase("1")){

            if(!preferences.getString("messagesSatus","").equals(srno)){

               AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

               builder.setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("messagesSatus",srno);
                                editor.commit();
                            }
                        });

               AlertDialog alert = builder.create();
               BounceView.addAnimTo(alert);
                alert.show();
            }

        }else {
              AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(false);

                    AlertDialog alertDialog = builder.create();
                    BounceView.addAnimTo(alertDialog);
                    alertDialog.show();
        }

    }

}
