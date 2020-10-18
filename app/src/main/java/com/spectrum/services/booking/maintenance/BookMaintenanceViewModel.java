package com.spectrum.services.booking.maintenance;


import androidx.lifecycle.ViewModel;

import com.spectrum.services.models.BookTime;
import com.spectrum.services.models.CleanBookingResponceModel;
import com.spectrum.services.models.MaintBookingModel;
import com.spectrum.services.models.PriceRequestModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Abins Shaji on 05/03/18.
 */

public class BookMaintenanceViewModel extends ViewModel {


    public Single<List<ServiceDetails.ServiceData>> getMaintServiceData()
    {
        return Shared.maintenanceServiceDEtails;
    }

    public Single<BookTime>getSpecificTime(String date)
    {
        return Utils.getApiService().getSpecificTime( date);
    }
    public Single<CleanBookingResponceModel> getMaintBookingApi(MaintBookingModel model)
    {
        return Utils.getApiService().bookMaintenance(model);
    }
    public Observable<PriceResponseModel> getPriceDetailApi(PriceRequestModel model)
    {
        return Utils.getApiService().getMaintPrice(model);
    }
}
