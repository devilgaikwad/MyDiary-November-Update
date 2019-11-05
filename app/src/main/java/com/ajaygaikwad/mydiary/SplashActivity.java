package com.ajaygaikwad.mydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import com.ajaygaikwad.mydiary.Classes.CheckInternet;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity {

    ImageView l1;
    TextView l2;
    Animation updown, downup;
    Handler handler;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        l2 = (TextView)findViewById(R.id.l2);
        l1 = (ImageView) findViewById(R.id.l1);

        internetPopup();


    }

    private void internetPopup() {

        if (new CheckInternet(this).isNetworkConnected()) {
            // locationcheck(SplashActivity.this);
            set_animation();
        } else {
            //Toast.makeText(this,"No Internet Connection Available",Toast.LENGTH_LONG).show();

            try{

                SweetAlertDialog pDialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE);

                pDialog.setTitleText("Error Internet Connection");
                pDialog.setContentText("Please check your internet connection");
                pDialog.setCancelable(false);
                pDialog.setConfirmText("Retry");
                pDialog.show();

                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent in = new Intent(getApplicationContext(),SplashActivity.class);
                        startActivity(in);
                        finish();
                    }
                });
            }
            catch (Exception e){

                new AlertDialog.Builder(this).setMessage("You do not have internet connection.")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show().setCanceledOnTouchOutside(false);
            }
        }
    }

    private void set_animation() {

        updown= AnimationUtils.loadAnimation(this,R.anim.downtoup);
        l1.setAnimation(updown);
        downup= AnimationUtils.loadAnimation(this,R.anim.uptodown);
        l2.setAnimation(downup);


        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        },1000);
    }
    }
