package com.spectrum.services.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.spectrum.services.retrofit.ApiClient;
import com.spectrum.services.retrofit.ApiInterface;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Abins Shaji on 24/01/18.
 */

public class Utils {
    public static ApiInterface getApiService() {
        return ApiClient.getClient().create(ApiInterface.class);
    }

    public static boolean emailValidate(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isPasswordValid(String password) {

        return password.length() >= 6;
    }

    public static boolean isPhoneValid(String phone) {
        return phone.length() >= 10 && phone.length() <= 12;
    }

    public static boolean isStringEqual(String one, String two) {
        return one.equalsIgnoreCase(two);
    }

    public static String getDay(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.ENGLISH);
        return sdf.format(date);
    }


    public static String checkIfchoosenDateIsSameDateToday(String dateChoosen){
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = Calendar.getInstance().getTime();
        String formattedTodayDate = curFormater.format(todayDate);
        return formattedTodayDate;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;


    }

    public static <T> String convertmodelToString(T user) {
        return new Gson().toJson(user);
    }

    public static <T> T convertStringToModel(String s, Class<T> aClass) {
        return new Gson().fromJson(s, aClass);
    }

    public static String getDateFormat(String data) {
        String newdate = "";
        Date date;
        SimpleDateFormat old = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = old.parse(data);
            newdate = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newdate;
    }


}
