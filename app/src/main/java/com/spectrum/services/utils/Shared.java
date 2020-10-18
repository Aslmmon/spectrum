package com.spectrum.services.utils;

import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.models.CleaningBookModel;
import com.spectrum.services.models.ServiceArea;
import com.spectrum.services.models.drawer.ServiceDetails;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by Abins Shaji on 06/02/18.
 */

public class Shared {
    public static ArrayList<CleaningBookModel> cleaningBookModelList = new ArrayList<>();
    public static Single<List<CleanExtraService.ExtraServiceData>> extraServiceModels;
    public static Single<ServiceArea> serviceArea;
    public static Single<List<ServiceDetails.ServiceData>> cleaningServiceDEtails;
    public static Single<List<ServiceDetails.ServiceData>> maintenanceServiceDEtails;
    public static Single<List<String>> crewinModelSingle;


    public static ArrayList<CleanExtraService.ExtraServiceData> listSelectedExtraSErvice = new ArrayList<>();


}
