package com.ajaygaikwad.mydiary.Fragments;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ajaygaikwad.mydiary.Classes.MyApplication;
import com.ajaygaikwad.mydiary.Classes.SharedPref;
import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.SlidingRootNavActivity;
import com.ajaygaikwad.mydiary.WebHelper.Config;
import com.ajaygaikwad.mydiary.db.AppDatabase;
import com.ajaygaikwad.mydiary.db.ContactDAO;
import com.ajaygaikwad.mydiary.pojo.Contact;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import gun0912.tedbottompicker.TedBottomPicker;
import hari.bounceview.BounceView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAppointmentFragment extends Fragment {
    View v;
    String onlyDate;
    String mimeType;
    Bitmap bitmap;
    Uri selectedUri;
    String encodedProfilePhotoString = "";
    ContentResolver cr;

    TextView dateSelected;
    TextView timeselect;
    List<String>propList;
    List<String>nameList;
    List<String>discList;
    AutoCompleteTextView nameAuto,et_desc;
    Spinner bTypeSpinner;
    EditText et_amount;
    ActionProcessButton btn_add;
    private ProgressDialog progressBar;
    private int progressBarStatus;
    private DatePickerDialog.OnDateSetListener mdateSetListener;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String result22;
    String businessType;
    String date;
    String timeStamp;
    String timeStamp1;
    String currentDate;
    String updateDate;
    String format;
    Button btnSelectImage;
    ImageView imageView;
    private ContactDAO mContactDAO;
    List<Contact> contacts;
    //StatusView statusView;
    LinearLayout llAddImage,llSignature;
    ImageView ivClose;

    private InterstitialAd mInterstitialAd;

    public AddAppointmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_appointment, container, false);
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor= preferences.edit();
        dateSelected = v.findViewById(R.id.dateselect);
        timeselect = v.findViewById(R.id.timeselect);
        btnSelectImage = v.findViewById(R.id.btnSelectImage);
        BounceView.addAnimTo(btnSelectImage);
        imageView = v.findViewById(R.id.imageView);
        llAddImage = v.findViewById(R.id.llAddImage);
        llSignature = v.findViewById(R.id.llSignature);
        ivClose = v.findViewById(R.id.ivClose);
        llSignature.setVisibility(View.GONE);

        MobileAds.initialize(getActivity(), "ca-app-pub-3864681863166960~2667252138");
        mInterstitialAd = new InterstitialAd(getActivity());
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
        mInterstitialAd.setAdUnitId("ca-app-pub-3864681863166960/6199893339");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                mInterstitialAd.show();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("encodedSignString","");
                editor.commit();


                Fragment fragment = new AddAppointmentFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                if(preferences.getString("view","").equals("2")){
                    ft.replace(R.id.container,  fragment).addToBackStack("").commit();
                }else{
                    ft.replace(R.id.fmain,  fragment).addToBackStack("").commit();
                }

            }
        });
        //statusView = v.findViewById(R.id.status);

        String encodedSignString = preferences.getString("encodedSignString","");
        if(!encodedSignString.trim().equals(""))
        {
            llSignature.setVisibility(View.VISIBLE);
            encodedProfilePhotoString = encodedSignString;

            byte[] bytes = Base64.decode(encodedSignString, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            Glide.with(getActivity().getApplicationContext())
                    .load(bitmap)
                    //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(imageView);
        }



        cr = getActivity().getApplicationContext().getContentResolver();
        mContactDAO = Room.databaseBuilder(getActivity(), AppDatabase.class, "db-contacts")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getContactDAO();



        bTypeSpinner=v.findViewById(R.id.bTypeSpinner);
        nameAuto=v.findViewById(R.id.nameAuto);
        nameAuto.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        et_desc=v.findViewById(R.id.et_desc);
        et_desc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        btn_add=v.findViewById(R.id.btn_add);
        BounceView.addAnimTo(btn_add);
        et_amount=v.findViewById(R.id.et_amount);

        propList = new ArrayList<>();
        nameList = new ArrayList<>();
        contacts = new ArrayList<>();
        discList = new ArrayList<>();
        nameList = mContactDAO.getContacts();

        //setAutoCompleteTextView(nameAuto, listOfProduct);

        nameAuto.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,nameList));
        nameAuto.setThreshold(1);
        nameAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                discList = mContactDAO.getContactsAgainstName(nameAuto.getText().toString());
                et_desc.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, discList));
            }
        });


        propList.clear();
        list();

        ArrayAdapter <String> propAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,propList);

        bTypeSpinner.setAdapter(propAdapter);

        bTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 businessType = bTypeSpinner.getItemAtPosition(bTypeSpinner.getSelectedItemPosition()).toString();
                 nameAuto.requestFocus();
             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        timeStamp = new SimpleDateFormat("hh:mm a").format(new Date());
        timeStamp1 = new SimpleDateFormat("HH:mm").format(new Date());

        if(!preferences.getString("UpdateDate","").equals(currentDate)){
            autoUpdator();
            updateDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            editor.putString("UpdateDate",updateDate );
            editor.commit();
        }



        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        showTime(hour, minute);



        dateSelected.setText(currentDate+" ");
        timeselect.setText(timeStamp );
        onlyDate=date;

        timeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {



                        if (selectedHour == 0) {
                            selectedHour += 12;
                            format = "AM";
                        } else if (selectedHour == 12) {
                            format = "PM";
                        } else if (selectedHour > 12) {
                            selectedHour -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }

                        //showTime(selectedHour, selectedMinute);
                        timeselect.setText(selectedHour + ":" + selectedMinute+" "+ format);
                        timeStamp1 = selectedHour + ":" + selectedMinute;
                        timeStamp = selectedHour + ":" + selectedMinute+" "+ format;
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        dateSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog
                        (getActivity(), mdateSetListener,
                                year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                //dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });


        mdateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;

                //timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                date =year+"-"+month+"-"+day;
                String date1 =day+"/"+month+"/"+year;
                dateSelected.setText(date1+" ");
                onlyDate= date;
            }
        };


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAppointMethod();
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu  = new PopupMenu(getActivity(),btnSelectImage);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.menuPhoto:

                                checkpermission();
                                showImagePicker();

                                return true;
                            case R.id.menuSignature:

                                /*Intent in = new Intent(getActivity(), SignActivity.class);
                                startActivity(in);*/

                                Fragment fragment = new SignFragment();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                                if(preferences.getString("view","").equals("2")){
                                    ft.replace(R.id.container,  fragment).addToBackStack("").commit();
                                }else{
                                    ft.replace(R.id.fmain,  fragment).addToBackStack("").commit();
                                }
                                return true;
                        }

                        return false;
                    }
                });
                popupMenu.show();


            }
        });


        return v;
    }

    public void showTime(int hour, int minute) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
    }

    private void addAppointMethod() {

        if(dateSelected.getText().toString().equals("Select Date")){
            Snackbar snake = Snackbar.make(getView()," Please Select Date" , Snackbar.LENGTH_SHORT);
            snake.show();
            return;
        }
        if(businessType.equals("Setect Type")){
            Snackbar snake = Snackbar.make(getView()," Please Select Type" , Snackbar.LENGTH_SHORT);
            snake.show();
            return;
        }
        if(nameAuto.getText().toString().equals("")){
            Snackbar snake = Snackbar.make(getView()," Please Add Name" , Snackbar.LENGTH_SHORT);
            snake.show();

            nameAuto.setError("Enter Name");
            return;
        }
        if(et_amount.getText().toString().trim().equals("")){
            Snackbar snake = Snackbar.make(getView()," Please Enter Amount" , Snackbar.LENGTH_SHORT);
            snake.show();
            et_amount.setError("Enter Amount");
            return;
        }

        Contact contact = new Contact();
        contact.setFirstName(nameAuto.getText().toString().trim());
        contact.setDisc(et_desc.getText().toString().trim());
        //Insert to database
        try {
            mContactDAO.insert(contact);
            getActivity().setResult(RESULT_OK);

        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //statusView.setStatus(Status.LOADING);
        btn_add.setProgress(1);
        btn_add.setText("Loading...");
        btn_add.setEnabled(false);
        //progressDiaglog();
        StringRequest postRequest1 = new StringRequest(Request.Method.POST, Config.ADD_APPOINTMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //progressBar.dismiss();
                            JSONObject object = new JSONObject(response);
                             result22 = object.getString("success");


                            if (result22.equals("1")) {
                                //statusView.setStatus(Status.COMPLETE);
                                btn_add.setProgress(100);
                                btn_add.setText("Added Successfully");
                                btn_add.setEnabled(false);
                                nameList.add(nameAuto.getText().toString());

                                editor.putString("encodedSignString","");
                                editor.commit();
                                int amountBal = preferences.getInt("AMOUNT_BALANCE",  100);
                                int amounttoadd = Integer.parseInt(et_amount.getText().toString());

                                if(businessType.equals("Expense")) {
                                    int finalAmountBal = amountBal - amounttoadd;
                                    editor.putInt("AMOUNT_BALANCE", finalAmountBal);
                                    editor.commit();
                                    try {
                                        ((SlidingRootNavActivity)getActivity()).publicMethod();
                                    } catch (Exception e) { }
                                }
                                else if(businessType.equals("Credit")) {
                                    int finalAmountBal = amountBal + amounttoadd;
                                    editor.putInt("AMOUNT_BALANCE", finalAmountBal);
                                    editor.commit();
                                    try {
                                        ((SlidingRootNavActivity)getActivity()).publicMethod();
                                    } catch (Exception e) {

                                    }
                                }




                                //Toast.makeText(getActivity(), "Added to Diary Successfully", Toast.LENGTH_SHORT).show();

                                AlertBox();
                            }

                            else {
                                //statusView.setStatus(Status.ERROR);
                                btn_add.setProgress(0);
                                btn_add.setText("Diary Set Error");
                                btn_add.setEnabled(true);
                                //progressBar.dismiss();
                                AlertBox();
                                //Toast.makeText(getActivity(), "Diary Set Error", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            //progressBar.dismiss();
                            e.printStackTrace();
                            //statusView.setStatus(Status.ERROR);
                            btn_add.setProgress(0);
                            btn_add.setText("Error...");
                            btn_add.setEnabled(true);
                            //Toast.makeText(getActivity(), "Error...", Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
                    {
                //progressBar.dismiss();
                        //statusView.setStatus(Status.ERROR);
                        btn_add.setProgress(0);
                        btn_add.setText("Error Connecting Server");
                        btn_add.setEnabled(true);
                //Toast.makeText(getActivity(), "Error Connecting To Server", Toast.LENGTH_LONG).show();
                        Toasty.error(getActivity(), "Error Connecting To Server").show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("name",et_name.getText().toString());
                params.put("date", date+" "+timeStamp1);
                params.put("dealerName",nameAuto.getText().toString());
                params.put("propType",businessType);
                params.put("custoNo",et_amount.getText().toString());
                params.put("desc",et_desc.getText().toString());
                params.put("onlyDate",onlyDate);
                params.put("city", new SharedPref(getActivity()).getCity());
                params.put("mobile", new SharedPref(getActivity()).getMobile());
                params.put("encodedString", encodedProfilePhotoString);
                params.put("onlytime", timeStamp);

                //params.put("firebase_id",firebase_id);

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(90000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest1.setRetryPolicy(policy);
        postRequest1.setShouldCache(false);
        MyApplication.getInstance().addToReqQueue(postRequest1);
    }

    private void AlertBox() {
        if(result22.equals("1")) {
            try {
                final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                pDialog.setTitle("Successfully Added");
                pDialog.setContentText("Successfully added to diary");
                pDialog.setCancelable(false);
                pDialog.show();
                pDialog.setConfirmButton("Okey", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pDialog.dismissWithAnimation();
                        mInterstitialAd.setAdListener(new AdListener(){
                            public void onAdLoaded(){
                                mInterstitialAd.show();
                            }
                        });
                        Fragment fragment = new AddAppointmentFragment();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        if(preferences.getString("view","").equals("2")){
                            ft.replace(R.id.container,  fragment).addToBackStack("").commit();
                        }else{
                            ft.replace(R.id.fmain,  fragment).addToBackStack("").commit();
                        }

                    }
                });
            } catch (Exception e) {
            }
        }else{
            try {
                final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                pDialog.setTitle("Error");
                if(result22.equals("2")){
                    pDialog.setContentText("Appointment already added for this user");
                }else{
                    pDialog.setContentText("Error while adding appointment");
                }
                pDialog.show();
                pDialog.setConfirmButton("Okey", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pDialog.dismissWithAnimation();
                    }
                });
            } catch (Exception e) {
            }
        }

    }

    private void list() {
        propList.add("Setect Type");
        propList.add("Credit");
        propList.add("Expense");
        propList.add("Borrow To");
        propList.add("Borrow From");

    }

    public void onResume(){
        super.onResume();

        try{
            ((MainNavActivity)getActivity()).setActionBarTitle("Add to Diary");
        }catch (Exception e){
            ((SlidingRootNavActivity)getActivity()).setActionBarTitle("Add to Diary");
        }
    }

    public void autoUpdator() {


        final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Config.URL_APPUpdator,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stringResponse) {
                        try {
                            Log.v("stringResponse", "verosn " + stringResponse);

                            JSONObject response = new JSONObject(stringResponse);
                            String aa = response.getString("versioncode");
                            String bb = response.getString("importance");
                            Double var = 0.0;
                            try {
                                var = Double.parseDouble(aa);

                            } catch (Exception e) {
                                var = 0.0;
                            }


                            int version = 0;
                            try {
                                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                                version = pInfo.versionCode;

                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }


                            Double currentver = 0.0;
                            try {
                                currentver = Double.parseDouble(version + "");

                            } catch (Exception e) {
                                currentver = 0.0;

                            }

                            Log.v("version", "current" + currentver + " new :" + var);
                            if (var > currentver) {

                                updateDialog(bb);
                            }else{
                                editor.putString("UpdateLL","0");
                                editor.commit();
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Toast.makeText(getActivity(), "Could not connect", Toast.LENGTH_LONG).show();
                        Toasty.error(getActivity(), "Could not connect").show();
                        Log.e("Volley", error.toString());
                    }
                }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("adminid", "12");
                params.put("appPackageName", appPackageName);

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringPostRequest.setRetryPolicy(policy);
        stringPostRequest.setShouldCache(false);
        MyApplication.getInstance().addToReqQueue(stringPostRequest);

    }

    private void updateDialog(final String bb) {
        editor.putString("UpdateLL","1");
        editor.commit();
        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle("New Update available");
        dialog.setMessage("Please update your app ..!");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                editor.putString("UpdateLL","0");
                editor.commit();
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getResources().getString(R.string.urll)));
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        })
                .setNegativeButton("No ", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(bb.equals("0")){
                            dialog.dismiss();
                        }else {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                            getActivity().finishAffinity();
                            System.exit(0);
                            return;
                        }
                    }
                });

        final androidx.appcompat.app.AlertDialog alert = dialog.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
    }

    private void showImagePicker() {

        try{
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    TedBottomPicker bottomSheetDialog = new TedBottomPicker.Builder(getActivity())
                            .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                                @Override
                                public void onImageSelected(final Uri uri) {
                                    Log.d("ted", "uri: " + uri);
                                    Log.d("ted", "uri.getPath(): " + uri.getPath());

                                    try {
                                        mimeType = cr.getType(uri);
                                        // String mm = getActivity().getContentResolver().getType(uri);

                                        //  String mimeType = MediaStore.Images.Media.getBitmap();
                                        long size = 0;
                                        if (mimeType == null) {
                                            size = new File(uri.getPath()).length();
                                        } else {
                                            Cursor returnCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                            returnCursor.moveToFirst();
                                            size = returnCursor.getLong(sizeIndex);
                                        }

                                        long length = size / 1024; // Size in KB
                                        if (length <= 20000) {

                                            final Dialog dialog;
                                            ImageView ivSelectedProfilePhoto;
                                            Button dgBtnCancel, dgBtnOk;

                                            dialog = new Dialog(getActivity());
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog.setContentView(R.layout.dialog_change_profile_photo);
                                            dialog.setCanceledOnTouchOutside(false);

                                            // Find Views On Dialog
                                            ivSelectedProfilePhoto = (ImageView) dialog.findViewById(R.id.ivSelectedProfilePhoto);
                                            dgBtnCancel = (Button) dialog.findViewById(R.id.dgBtnCancel);
                                            dgBtnOk = (Button) dialog.findViewById(R.id.dgBtnOk);


                                            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                                            bitmap = Bitmap.createScaledBitmap(bitmap, 900, 900, true);
                                            selectedUri = uri;

                                            Glide.with(getActivity().getApplicationContext())
                                                    .load(uri)
                                                    //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                                    .into(ivSelectedProfilePhoto);


                                            dgBtnCancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            dgBtnOk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {


                                                        Glide.with(getActivity().getApplicationContext())
                                                                .load(uri)
                                                                //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                                                .into(imageView);

                                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                        //  bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedUri));
                                                        bitmap.compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream);
                                                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                                                        encodedProfilePhotoString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                                                    //new EncodePhoto().execute();
                                                    dialog.dismiss();
                                                }
                                            });

                                            Window window = dialog.getWindow();
                                            window.setLayout(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
                                            dialog.show();


                                        } else {
                                            //Toast.makeText(getActivity(), "Please select photo less than 20 MB in size.", Toast.LENGTH_SHORT).show();
                                            Toasty.error(getActivity(), "Please select photo less than 20 MB in size.").show();
                                        }

                                    } catch (Exception e) {
                                        // if any error occurs
                                        e.printStackTrace();
                                        //  Toast.makeText(HomeNavActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            })
                            .setTitle("Select Image")
                            .setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
                            .create();

                    bottomSheetDialog.show(getActivity().getSupportFragmentManager());
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    //Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    Toasty.error(getActivity(), "Permission Denied\n" + deniedPermissions.toString()).show();
                }

            };

            new TedPermission(getActivity().getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
            checkpermission();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkpermission() {

        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int CAMERA = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();


        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (CAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }//checkispermisison

}
