package com.spectrum.services.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Abins Shaji on 29/01/18.
 */

public class Prefs {
    private static final String PREF_NAME ="prefName" ;
    private static final String PREF_SIGNED ="prefSigned";
    private static final String PREF_FBSIGNIN = "prefFbSigned";
    private static final String PREF_USERNAME ="prefUserName" ;
    private static final String PREF_USEREMAIL ="prefUserEmail";
    private static final String PREF_USERID ="prefUid" ;
    private static final String PREF_PHONE = "prefPhone";
    private static final String PREF_USERDETAILS ="pref_user" ;
    private static Prefs instance;
    private final SharedPreferences sharedPreferences;
    private static final String PREF_NUMBVERIF="pref_numbverif";

    public Prefs(Context context) {
        sharedPreferences=context.getApplicationContext().getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public static void clearSession(Context context)
    {
        final SharedPreferences.Editor editor=context.getApplicationContext().getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static Prefs with(Context context)
    {
        if(instance==null)
        {
            instance=new Prefs(context);
        }


        return instance;
    }

    public void setSignedIn(Boolean signedin)
    {
        sharedPreferences
                .edit()
                .putBoolean(PREF_SIGNED,signedin)
                .apply();

    }

    public Boolean getSignedIn()
    {
        return  sharedPreferences.getBoolean(PREF_SIGNED,false);
    }

    public void setFbSignIn(Boolean fbSignIn)
    {
        sharedPreferences
                .edit()
                .putBoolean(PREF_FBSIGNIN,fbSignIn)
                .apply();
    }
    public Boolean getFbSignedIn()
    {
        return sharedPreferences.getBoolean(PREF_FBSIGNIN,false);
    }

    public void setName(String name)
    {
        sharedPreferences
                .edit()
                .putString(PREF_USERNAME,name)
                .apply();


    }

    public String getName()
    {
        return sharedPreferences.getString(PREF_USERNAME,"null");
    }

    public void setEmail(String email)
    {
        sharedPreferences
                .edit()
                .putString(PREF_USEREMAIL,email)
                .apply();
    }

    public String getEmail()
    {
        return sharedPreferences.getString(PREF_USEREMAIL,"null");
    }
    public  void  setNumberVerified(String verified)
    {
        sharedPreferences
                .edit()
                .putString(PREF_NUMBVERIF,verified)
                .apply();
    }

    public String getNumbVerified()
    {
        return sharedPreferences.getString(PREF_NUMBVERIF,"null");
    }

    public void setUserid(String id)
    {
        sharedPreferences.edit()
                .putString(PREF_USERID,id)
                .apply();
    }
    public String getUserid()
    {
        return sharedPreferences.getString(PREF_USERID,"null");
    }

    public void setPhone(String phone)
    {
        sharedPreferences
                .edit()
                .putString(PREF_PHONE,phone)
                .apply();
    }
    public String getPrefPhone()
    {
        return sharedPreferences.getString(PREF_PHONE,"null");
    }


    public void setUserDetails(String user)
    {
        sharedPreferences
                .edit()
                .putString(PREF_USERDETAILS,user)
                .apply();
    }
    public String getUserDetails()
    {
        return sharedPreferences.getString(PREF_USERDETAILS,"null");
    }


}
