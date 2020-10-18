package com.spectrum.services.booking.cleaning;


import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.spectrum.services.models.CleanBookingRequestModel;
import com.spectrum.services.models.CleanBookingResponceModel;
import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.models.CrewinModel;
import com.spectrum.services.models.MainCleanBookModel;
import com.spectrum.services.models.CleaningBookModel;
import com.spectrum.services.models.PriceRequestModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Abins Shaji on 02/03/18.
 */

public class BookCleanViewModel extends ViewModel {
    private Observable<MainCleanBookModel>bookModelObservable;
    private CleaningBookModel model;
    private Observable<PriceResponseModel>priceObservable;

    public Observable<MainCleanBookModel>getBasicBookinDetail()
    {
        if (bookModelObservable==null)
        {
            bookModelObservable=getBookModelObservable();

        }
        return bookModelObservable;
    }

    public Observable<MainCleanBookModel> getBookModelObservable() {
        return bookModelObservable;
    }

    public void setBookModelObservable(Observable<MainCleanBookModel> bookModelObservable) {
        this.bookModelObservable = bookModelObservable;
    }


    public Single<List<CleanExtraService.ExtraServiceData>> getExtraServiceData()
    {
        return Shared.extraServiceModels;
    }

    public  Single<List<ServiceDetails.ServiceData>> getServiceData()
    {
        return Shared.cleaningServiceDEtails;
    }
    public Single<List<String>> getCrewinData()
    {
        return Shared.crewinModelSingle;
    }
    public Single<ServiceDetails> getCleaningServiceDetails()
    {
        return Utils.getApiService().getCleaningServiceDetails();
    }
    public Single<CrewinModel> getCrewin() {
        return Utils.getApiService().getCrewinData();
    }

    public Observable<PriceResponseModel>getPriceDetailApi(PriceRequestModel model)
    {
        return Utils.getApiService().getPrice(model);
    }

    public Observable<CleanBookingResponceModel>bookClean(CleanBookingRequestModel model)
    {
        return Utils.getApiService().bookCleanService(model);
    }


    public Single<UserModel>informPayStatus(JsonObject object)
    {
        return Utils.getApiService().informPayStatus(object);
    }


}
