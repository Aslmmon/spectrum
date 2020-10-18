package com.spectrum.services.profile;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.spectrum.services.models.ServiceArea;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;

/**
 * Created by Abins Shaji on 01/03/18.
 */

public class ProfileViewModel extends ViewModel {
    public static final String TAG = "messgae";
    private io.reactivex.Observable<UserModel> user;
    private Single<ServiceArea> areaObservable;
    private String uid;
    private Map<String, Integer> MapServicArea = new HashMap<>();

    public io.reactivex.Observable<UserModel> getUser(String uid) {
        this.uid = uid;
        if (user == null) {
            Log.e(TAG, "getUser: " );
            user=getUserApi();

        }
        return user;


    }

    private io.reactivex.Observable<UserModel> getUserApi() {
        return Utils.getApiService().getUserDetails(setupDataToSend(uid));

    }

    public JsonObject setupDataToSend(String uid) {
        JsonObject object = new JsonObject();
        object.addProperty("user_id", uid);
        return object;
    }

    public Single<ServiceArea> getServiceArea() {
        if (areaObservable == null) {
            Log.e(TAG, "getServiceArea: " );
           areaObservable= getServiceAreaApi();


        }
        return areaObservable;


    }

    private Single<ServiceArea> getServiceAreaApi() {
        return Shared.serviceArea;

    }

    public String[] setUpSpinnerData(ServiceArea serviceArea) {
        if (serviceArea.getAreaData().size() > 1) {
            String[] data_area = new String[serviceArea.getAreaData().size() + 1];
            data_area[0] = "Select Area";
            for (int i = 1, j = 0; j < serviceArea.getAreaData().size(); i++, j++) {
                data_area[i] = serviceArea.getAreaData().get(j).getArea_name();
                MapServicArea.put(serviceArea.getAreaData().get(j).getArea_name(), j);
            }
            return data_area;


        }
        return null;


    }

    public Map<String, Integer> getMapServicArea() {
        return MapServicArea;
    }

    public Single<UserModel> updateProfile(JsonObject jsonObject)
    {
        return Utils.getApiService().editProfile(jsonObject);
    }


}
