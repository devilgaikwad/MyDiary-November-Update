package com.ajaygaikwad.mydiary.Classes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        this.context = context;
        prefs = this.context.getSharedPreferences("UserStatus", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public  void  setUserName(String name){
        editor.putString("username", name);
        editor.commit();

    }
    public String getUserName(){
        if (prefs.contains("username"))
            return prefs.getString("username", "User");
        else
            return "user";
    }

    public  void setMobile(String mobile){
        editor.putString("mobile",mobile );
        editor.commit();
    }
    public  String getMobile(){
        if (prefs.contains("mobile"))
            return prefs.getString("mobile", "User");
        else
            return "user";
    }

    public void setCity(String selectedCity) {
        editor.putString("selectedCity",selectedCity );
        editor.commit();
    }
    public  String getCity(){
        if (prefs.contains("selectedCity"))
            return prefs.getString("selectedCity", "User");
        else
            return "user";
    }

    public void login(int i) {
        editor.putString("login", String.valueOf(i));
        editor.commit();
    }
    public  String getLogin(){
        if (prefs.contains("login"))
            return prefs.getString("login", "User");
        else
            return "user";
    }
    public boolean clearPref(){
        editor.clear();
        editor.commit();
        return true;
    }
}

