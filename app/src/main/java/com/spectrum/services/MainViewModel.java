package com.spectrum.services;


import androidx.lifecycle.ViewModel;

import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.models.CrewinModel;
import com.spectrum.services.models.ServiceArea;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Abins Shaji on 02/03/18.
 */

public class MainViewModel extends ViewModel {
    public Observable<CleanExtraService> getExtraServiceApi()
    {
        return Utils.getApiService().getExtraService();
    }

    public Single<ServiceArea> getServiceAreaApi()
    {
        return Utils.getApiService().getProfileServiceArea();
    }

    public Single<ServiceDetails> getCleaningServiceDetails()
    {
        return Utils.getApiService().getCleaningServiceDetails();
    }
    public Single<ServiceDetails> getMaintenanceServiceDetails()
    {
        return Utils.getApiService().getMaintenanceServiceDetails();
    }


    public Single<CrewinModel> getCrewin() {
        return Utils.getApiService().getCrewinData();
    }
}
