package com.ajaygaikwad.mydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.Fragments.AboutUsFragment;
import com.ajaygaikwad.mydiary.Fragments.AddAppointmentFragment;
import com.ajaygaikwad.mydiary.Fragments.HomeFragment;
import com.ajaygaikwad.mydiary.Fragments.ViewAppointFragment;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SlidingRootNavActivity extends AppCompatActivity {

    private SlidingRootNav slidingRootNav;
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView homeTab,addToDiaryTab,viewDiaryTab,aboutTab,changeUI,logOutTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_root_nav);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        if(slidingRootNav.isMenuOpened()){
            slidingRootNav.closeMenu();
        }

        homeTab = findViewById(R.id.homeTab);
        addToDiaryTab = findViewById(R.id.addToDiaryTab);
        viewDiaryTab = findViewById(R.id.viewDiaryTab);
        aboutTab = findViewById(R.id.aboutTab);
        changeUI = findViewById(R.id.changeUI);
        logOutTab = findViewById(R.id.logOutTab);

        setAttributes(homeTab);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container,  new HomeFragment()).addToBackStack("tag").commit();

        logOutTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SharedPref(getApplicationContext()).clearPref();
                Intent in = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
                finish();
            }
        });

        homeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttributes(homeTab);
                slidingRootNav.closeMenu();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,  new HomeFragment()).addToBackStack("tag").commit();


            }
        });
        addToDiaryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttributes(addToDiaryTab);
                slidingRootNav.closeMenu();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,  new AddAppointmentFragment()).addToBackStack("tag").commit();
            }
        });
        viewDiaryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttributes(viewDiaryTab);
                slidingRootNav.closeMenu();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,  new ViewAppointFragment()).addToBackStack("tag").commit();
            }
        });
        aboutTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttributes(aboutTab);
                slidingRootNav.closeMenu();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,  new AboutUsFragment()).addToBackStack("tag").commit();
            }
        });
        changeUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttributes(changeUI);
                slidingRootNav.closeMenu();
                editor.putString("view","1");
                editor.commit();
                Intent in = new Intent(getApplicationContext(),MainNavActivity.class);
                startActivity(in);
            }
        });


    }

    public void setActionBarTitle (String title){
        getSupportActionBar().setTitle(title);
    }

    private void setAttributes(TextView id) {
        homeTab.setTextColor(getResources().getColor(R.color.black1));
        addToDiaryTab.setTextColor(getResources().getColor(R.color.black1));
        viewDiaryTab.setTextColor(getResources().getColor(R.color.black1));
        aboutTab.setTextColor(getResources().getColor(R.color.black1));
        changeUI.setTextColor(getResources().getColor(R.color.black1));

        id.setTextColor(getResources().getColor(R.color.blue));

    }



    @Override
    public void onBackPressed() {

        if(slidingRootNav.isMenuOpened()){
            slidingRootNav.closeMenu();
        }
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
            if (f instanceof HomeFragment) {
                try {
                    SweetAlertDialog pDialog = new SweetAlertDialog(SlidingRootNavActivity.this, SweetAlertDialog.WARNING_TYPE);
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
                } catch (Exception x) {
                    new AlertDialog.Builder(this)
                            .setTitle("Really Exit")
                            .setMessage("Sure you want to exit")
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
            }else{
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,  new HomeFragment()).addToBackStack("").commit();
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            new SharedPref(getApplicationContext()).clearPref();
            Intent in = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(in);



            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public  void  onResume(){
        super.onResume();

        if(slidingRootNav.isMenuOpened()){
            slidingRootNav.closeMenu();
        }
    }

}
