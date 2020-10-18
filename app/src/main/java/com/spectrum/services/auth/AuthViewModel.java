package com.spectrum.services.auth;


import androidx.lifecycle.ViewModel;

import com.spectrum.services.models.UserModel;
import com.spectrum.services.utils.Utils;
import com.google.gson.JsonObject;

import io.reactivex.Observable;

/**
 * Created by Abins Shaji on 28/02/18.
 */

public class AuthViewModel extends ViewModel {


    public Observable<UserModel> registerApi(JsonObject jsonObject)
    {
        return Utils.getApiService().registerUser(jsonObject);


    }

    public Observable<UserModel> loginApi(JsonObject jsonObject)
    {
        return Utils.getApiService().loginUser(jsonObject);


    }

    public Observable<UserModel> registerFbApi(JsonObject jsonObject)
    {
        return Utils.getApiService().registerFbUser(jsonObject);

    }
    public Observable<UserModel> loginFbApi(JsonObject jsonObject)
    {
        return Utils.getApiService().loginFbUser(jsonObject);

    }




}
