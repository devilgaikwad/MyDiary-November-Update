package com.ajaygaikwad.mydiary;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ajaygaikwad.mydiary.Classes.MyApplication;
import com.ajaygaikwad.mydiary.Classes.NotificationHelper;
import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.Fragments.AddAppointmentFragment;
import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import hari.bounceview.BounceView;

public class MainActivity extends AppCompatActivity {
    EditText et_pass;
    EditText et_login;
    ActionProcessButton btn_login;

    String selectedCity,user_id,name,mobile,count ;
    ArrayList <String> cities;
    private ProgressDialog progressBar;
    private int progressBarStatus;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView eye;
    private static final int notification_one = 101;
    private static final int notification_two = 102;
    private NotificationHelper notificationHelper;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=preferences.edit();
        et_login=findViewById(R.id.et_login);
        et_pass=findViewById(R.id.et_pass);
        btn_login=findViewById(R.id.btn_login);
        BounceView.addAnimTo(btn_login);
        btn_login.setMode(ActionProcessButton.Mode.ENDLESS);
        TextView register = findViewById(R.id.register);
        BounceView.addAnimTo(register);

        MobileAds.initialize(this, "ca-app-pub-3864681863166960~2667252138");
        mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
        mInterstitialAd.setAdUnitId("ca-app-pub-3864681863166960/6199893339");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                mInterstitialAd.show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(in);
            }
        });

        try {
            notificationHelper = new NotificationHelper(this);
        }catch (Exception e){}



        et_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = et_login.length();
                count=String.valueOf(len);
                if(len==10){
                    et_login.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done,0 );
                    et_pass.requestFocus();
                }
                else{
                    et_login.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                }
                btn_login.setProgress(0);
                btn_login.setText("Login");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginMethod();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

            }

        });

        et_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_login.setProgress(0);
                btn_login.setText("Login");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loginMethod() {

        if(et_login.getText().toString().trim().isEmpty()){
            et_login.requestFocus();
            et_login.setError("Enter Mobile No.");
            return;
        }
        if(et_pass.getText().toString().trim().isEmpty()){
            et_pass.requestFocus();
            et_pass.setError("Enter Password");
            return;
        }
        if(et_login.getText().toString().trim().contains("+")){
            et_login.requestFocus();
            et_login.setError("Enter Valid Mobile No.");
            return;
        }
        if(!count.equals("10")){
            et_login.requestFocus();
            et_login.setError("Enter Valid Mobile No.");
            return;
        }
        btn_login.setText("Loading...");
        btn_login.setEnabled(false);
        btn_login.setProgress(1);
        et_login.setFocusable(true);
        et_pass.setFocusable(true);
        //progressDiaglog();
        StringRequest postRequest1 = new StringRequest(Request.Method.POST, Config.SIGN_IN,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object=new JSONObject(response);
                            String result=object.getString("success");

                            if(result.equals("2")){
                                AlertBox();
                                btn_login.setEnabled(true);
                                btn_login.setProgress(-1);
                                btn_login.setText("Error");
                                return;
                            }
                            JSONArray jsonArray = object.getJSONArray("success3");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = jsonArray.getJSONObject(i);
                                user_id  = j1.getString("srno");
                                name     = j1.getString("name");
                                mobile   = j1.getString("mobile");
                            }
                            postNoti();
                            new SharedPref(MainActivity.this).setUserName(name);
                            new SharedPref(MainActivity.this).setMobile(mobile);
                            new SharedPref(MainActivity.this).setCity(selectedCity);
                            new SharedPref(MainActivity.this).login(1);

                            if (result.equals("1")) {
                                btn_login.setProgress(100);
                                btn_login.setText("Success!");

                                Intent in = new Intent(MainActivity.this,MainNavActivity.class);
                                startActivity(in);
                                finish();

                            }

                            else{
                                btn_login.setEnabled(true);
                                btn_login.setProgress(-1);
                                btn_login.setText("Error");
                                Toasty.error(getApplicationContext(), "Error Sign in").show();

                            }
                        }
                        catch (Exception e) {
                            btn_login.setEnabled(true);
                            btn_login.setProgress(-1);
                            btn_login.setText("Error");

                            e.printStackTrace();
                            Toasty.error(getApplicationContext(), "Number not registered. Please Register First..").show();


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btn_login.setEnabled(true);
                btn_login.setProgress(-1);
                btn_login.setText("Error");
                Toasty.error(getApplicationContext(), "Error Connecting To Server").show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("name",et_name.getText().toString());
                params.put("login", et_login.getText().toString());
                params.put("password", et_pass.getText().toString());
                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(90000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest1.setRetryPolicy(policy);
        postRequest1.setShouldCache(false);
        MyApplication.getInstance().addToReqQueue(postRequest1);
    }

    private void postNoti() {
        try {
            Notification.Builder notificationBuilder = null;
            notificationBuilder = notificationHelper.getNotification2("Welcome!!", getString(R.string.channel_two_body));
            if (notificationBuilder != null) {
                notificationHelper.notify(notification_two, notificationBuilder);
            }
        }
        catch (Exception e){}

    }

    public void progressDiaglog(){

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage(getString(R.string.pleaseWait));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
    }

    private void AlertBox() {
            try {
                final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                //pDialog.setTitle("User Banned");
                pDialog.setTitle("Incorrect credentials.");
                //pDialog.setContentText("You are temporarily banned for using My Diary App. Please contact our customer executive for further details");
                pDialog.setContentText("Mobile or password is incorrect.");
                pDialog.show();
                /*pDialog.setConfirmButton("Contact", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pDialog.dismissWithAnimation();
                        try {
                            Intent in = new Intent(Intent.ACTION_SEND);
                            in.setData(Uri.parse("mailto:"+"ajaygaikwad945@gmail.com"));
                            in.putExtra(Intent.EXTRA_EMAIL, new String[]{"ajaygaikwad945@gmail.com"});
                            in.putExtra(Intent.EXTRA_SUBJECT,"I am getting banned from using My Diary App");
                            in.putExtra(Intent.EXTRA_TEXT, "Mobile No. = "+et_login.getText().toString());
                            in.setType("message/rfc822");
                            startActivity(in);
                        }catch (Exception e){
                            Toasty.info(getApplicationContext(), "Contact to ajaygaikwad945@gmail.com");
                        }

                    }
                });*/

                pDialog.setConfirmButton("OKEY", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pDialog.dismissWithAnimation();

                    }
                });
            } catch (Exception e) {
                e.getStackTrace();
            }
    }

    public void onBackPressed() {

        try{
            SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
            pDialog.setTitleText("Really Exit");
            pDialog.setContentText("Sure you want to exit");
            pDialog.setConfirmText("Yes");
            pDialog.setCancelText("No");
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                    finishAffinity();
                }
            });
            pDialog.setCancelClickListener(null);
            pDialog.show();
            pDialog.setCancelable(false);
        }
        catch(Exception x) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit.")
                    .setMessage("Sure you want to exit.")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                            finishAffinity();
                        }
                    }).create().show();


        }
    }

}
