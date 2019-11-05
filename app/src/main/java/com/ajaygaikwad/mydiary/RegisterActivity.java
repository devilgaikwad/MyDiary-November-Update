package com.ajaygaikwad.mydiary;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ajaygaikwad.mydiary.Classes.MyApplication;
import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import hari.bounceview.BounceView;

public class RegisterActivity extends AppCompatActivity {

    Button btn_reg;
    TextView login;
    EditText et_pass_repeat,et_pass,et_email,et_mobile,et_name;
    private ProgressDialog progressBar;
    private int progressBarStatus;
    String count;
    boolean emailValidate = false;
    private static final int REQUEST_CODE_EMAIL = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_email=findViewById(R.id.et_email);
        et_pass_repeat=findViewById(R.id.et_pass_repeat);
        et_pass=findViewById(R.id.et_pass);
        et_mobile=findViewById(R.id.et_mobile);
        et_name=findViewById(R.id.et_name);
        login=findViewById(R.id.login);
        btn_reg=findViewById(R.id.btn_reg);
        BounceView.addAnimTo(btn_reg);
        BounceView.addAnimTo(login);

        et_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                            new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
                    startActivityForResult(intent, REQUEST_CODE_EMAIL);
                } catch (ActivityNotFoundException e) {
                    // TODO
                }
            }
        });



        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = et_mobile.length();
                count=String.valueOf(len);
                if(len==10){
                    et_mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done,0 );
                    et_email.requestFocus();
                }
                else{
                    et_mobile.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        et_email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (et_email.getText().toString().trim().matches(emailPattern) && s.length() > 0)
                {
                    et_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done,0 );
                    emailValidate = true;
                }
                else
                {
                    et_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0 );
                    emailValidate = false;

                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regmethod();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            et_email.setText(accountName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void regmethod() {

        if(et_name.getText().toString().trim().isEmpty()){
            et_name.requestFocus();
            et_name.setError("Enter Name No.");
            return;
        }

        if(et_mobile.getText().toString().trim().isEmpty()){
            et_mobile.requestFocus();
            et_mobile.setError("Enter Mobile No.");
            return;
        }
        if(et_pass.getText().toString().trim().isEmpty()){
            et_pass.requestFocus();
            et_pass.setError("Enter Password");
            return;
        }

        if(et_pass_repeat.getText().toString().trim().isEmpty()){
            et_pass_repeat.requestFocus();
            et_pass_repeat.setError("Enter Repeat Password");
            return;
        }

        if(!et_pass_repeat.getText().toString().trim().equals(et_pass.getText().toString().trim())){
            et_pass_repeat.requestFocus();
            et_pass_repeat.setError("Password Does not Match");
            return;
        }

        if(!emailValidate){
            et_email.requestFocus();
            et_email.setError("Enter Valid Email Address");
            return;
        }
        if(!count.equals("10")){
            et_mobile.requestFocus();
            et_mobile.setError("Enter Valid Mobile No.");
            return;
        }

        progressDiaglog();
        StringRequest postRequest1 = new StringRequest(Request.Method.POST, Config.REGISTRATION,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.dismiss();
                            JSONObject object=new JSONObject(response);
                            String result=object.getString("success");

                            if (result.equals("1")) {

                                //Toast.makeText(RegisterActivity.this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                                Toasty.success(getApplicationContext(), "Registration Successfull").show();

                                Intent in = new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(in);
                                finish();

                            }
                            else{
                                progressBar.dismiss();

                                //Toast.makeText(RegisterActivity.this, "Email/Number Already registered. Please Register From Another Crediantials", Toast.LENGTH_SHORT).show();
                                Toasty.error(getApplicationContext(), "Email/Number Already registered. Please Register From Another Crediantials").show();
                            }


                        }
                        catch (Exception e) {
                            progressBar.dismiss();
                            e.printStackTrace();
                            //Toast.makeText(RegisterActivity.this, "Error in Register", Toast.LENGTH_SHORT).show();
                            Toasty.error(getApplicationContext(), "Error in Register").show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                //Toast.makeText(RegisterActivity.this,"Error Connecting To Server", Toast.LENGTH_LONG).show();
                Toasty.error(getApplicationContext(), "Error Connecting To Server").show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("et_name",et_name.getText().toString());
                params.put("et_email", et_email.getText().toString());
                params.put("et_pass", et_pass.getText().toString());
                params.put("et_mobile", et_mobile.getText().toString());


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

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage(getString(R.string.pleaseWait));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
    }
}
