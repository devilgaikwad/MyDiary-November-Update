package com.ajaygaikwad.mydiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.Fragments.AboutUsFragment;
import com.ajaygaikwad.mydiary.Fragments.AddAppointmentFragment;
import com.ajaygaikwad.mydiary.Fragments.HomeFragment;
import com.ajaygaikwad.mydiary.Fragments.ViewAppointFragment;
import com.google.android.material.navigation.NavigationView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class MainNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View hView;
    String currentDate,updateDate="";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
//    private AdView adView;
//    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();


        if(!new SharedPref(MainNavActivity.this).getLogin().equals("1")){
            Intent in = new Intent(MainNavActivity.this,SplashActivity.class);
            startActivity(in);
            finish();
            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hView=navigationView.getHeaderView(0);
        TextView nameNav=hView.findViewById(R.id.nameNav);
        TextView mobileNav=hView.findViewById(R.id.mobileNav);
        nameNav.setText(new SharedPref(getApplicationContext()).getUserName());
        mobileNav.setText(new SharedPref(getApplicationContext()).getMobile());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fmain,  new HomeFragment()).addToBackStack("").commit();

        Calendar mcurrentTime = Calendar.getInstance();

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        updateDate = preferences.getString("Date55","");
        if(!updateDate.equals(currentDate)){
            //do whatever you want for only once for today.......
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            dayWish(hour);
            editor.putString("getMessage","0");


            //do whatever you want for only once for today.......
            editor.putString("Date55",currentDate);
            editor.commit();

        }else{}

        if(preferences.getString("view","").equals("2")){
            Intent in = new Intent(getApplicationContext(),SlidingRootNavActivity.class);
            startActivity(in);
        }

    }

    public void dayWish(int hour){

        int flag = 0;
        if (hour < 4 && flag == 0) {
            flag = 1;
            showThoughts("1");
        }

        if (hour < 12 && flag == 0) {
            flag = 1;
            showThoughts("2");
        }
        if (hour < 16 && flag == 0) {
            flag = 1;
            showThoughts("3");
        }

        if (hour < 24 && flag == 0) {
            flag = 1;
            showThoughts("4");
        }
    }

    private void showThoughts(String s) {
        if (s.equals("1")){
            Toasty.normal(getApplicationContext(), "Good Night!").show();
        }
        if (s.equals("2")){
            Toasty.normal(getApplicationContext(), "Good Morning!").show();
        }
        if (s.equals("3")){
            Toasty.normal(getApplicationContext(), "Good Afternoon!").show();
        }
        if (s.equals("4")){
            Toasty.normal(getApplicationContext(), "Good Evening!").show();
        }
    }


    public void setActionBarTitle (String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fmain);
            if (f instanceof HomeFragment) {
                try {
                    SweetAlertDialog pDialog = new SweetAlertDialog(MainNavActivity.this, SweetAlertDialog.WARNING_TYPE);
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
                ft.replace(R.id.fmain,  new HomeFragment()).addToBackStack("").commit();
            }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fmain,  new AddAppointmentFragment()).addToBackStack("tag").commit();

        } else if (id == R.id.nav_gallery) {

            try{

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fmain,  new ViewAppointFragment()).addToBackStack("tag").commit();

            }catch (Exception w){
                w.printStackTrace();
            }


        }
        else if (id == R.id.nav_home) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fmain,  new HomeFragment()).addToBackStack("tag").commit();

        }
        else if (id == R.id.nav_about) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fmain,  new AboutUsFragment()).addToBackStack("tag").commit();

        }
        else if (id == R.id.nav_ui_change) {
            editor.putString("view","2");
            editor.commit();
           Intent in = new Intent(getApplicationContext(),SlidingRootNavActivity.class);
           startActivity(in);

        }
        else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            new SharedPref(getApplicationContext()).clearPref();
            Intent in = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(in);
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
