package com.spectrum.services.retrofit;

import com.google.gson.JsonObject;
import com.spectrum.services.models.BookTime;
import com.spectrum.services.models.BookingHistoryModel;
import com.spectrum.services.models.CleanBookingRequestModel;
import com.spectrum.services.models.CleanBookingResponceModel;
import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.models.CommonResponse;
import com.spectrum.services.models.CrewinModel;
import com.spectrum.services.models.MaintBookingModel;
import com.spectrum.services.models.PriceRequestModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.models.ServiceArea;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.models.VUpdateModel;
import com.spectrum.services.models.drawer.FaqModel;
import com.spectrum.services.models.drawer.ServiceAreaModel;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.models.payfort.FortTokenResponce;
import com.spectrum.services.profile.phone_verify.PhoneVerifyModel;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Abins Shaji on 24/01/18.
 */

public interface ApiInterface {
    //dummy
//    @GET("https://spectrum-d498e.firebaseio.com/booking_time/.json")
//    Call<BookTime> getBookTimeForDate();

    @GET("https://spectrum-d498e.firebaseio.com/clean_detail/.json")
    Call<ServiceDetails> getCleanDetails();

//    @GET("https://spectrum-d498e.firebaseio.com/faq/.json")
//    Call<FaqModel> getFaq();

//    @GET("https://spectrum-d498e.firebaseio.com/service_area/.json")
//    Call<ServiceAreaModel> getServiceAreas();

    @GET("https://spectrum-d498e.firebaseio.com/maintenance_service/.json")
    Call<ServiceDetails> getMaintenanceDetails();

//    @GET("https://spectrum-d498e.firebaseio.com/booking_history/.json")
//    Call<BookingHistoryModel> getBookingHistory();



    //actual
    @POST("customerLogin?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<UserModel> loginUser(@Body JsonObject jsonObject);

    @POST("cust_registration?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<UserModel> registerUser(@Body JsonObject jsonObject );

    @POST("facebook_registration?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<UserModel> registerFbUser(@Body JsonObject jsonObject);

    @POST("facebookLogin?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<UserModel> loginFbUser(@Body JsonObject jsonObject);

    @POST("userDetails?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<UserModel> getUserDetails(@Body JsonObject jsonObject);

    @GET("get_all_areas?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<ServiceArea> getProfileServiceArea();

    @POST("editUserDetails?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<UserModel> editProfile(@Body JsonObject user);

    @GET("extraservices?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<CleanExtraService> getExtraService();

    @GET("cleaning_services?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<ServiceDetails> getCleaningServiceDetails();

    @GET("maintenance_services?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<ServiceDetails> getMaintenanceServiceDetails();
//    @GET("https://spectrum-d498e.firebaseio.com/maint.json")
//    Single<ServiceDetails> getMaintenanceServiceDetails();

    @GET("getfaq?ws=spec09645d17359d1ac3d2516d0b7f2")
    Call<FaqModel> getFaq();

    @GET("forgot_password?ws=spec09645d17359d1ac3d2516d0b7f2")
    Call<UserModel>forgetPwd(@Query("email") String email);

    @POST("change_password?ws=spec09645d17359d1ac3d2516d0b7f2")
    Call<UserModel>changePwd(@Body JsonObject jsonObject);

    @GET("crew_in?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<CrewinModel> getCrewinData();

    @GET("service_time?ws=spec09645d17359d1ac3d2516d0b7f2")
    Call<BookTime>getBookTimeForDate(@Query("date")String date,@Query("hours") String hours);

    @POST("price_calculation?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<PriceResponseModel>getPrice(@Body PriceRequestModel model);

    @POST("add_booking?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<CleanBookingResponceModel> bookCleanService(@Body CleanBookingRequestModel model);

    @POST("contact_us?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<UserModel> contactUs(@Body JsonObject jsonObject);

    @GET("maintenance_service_time?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<BookTime> getSpecificTime(@Query("date") String date);

    @POST("maintenance_add_booking?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<CleanBookingResponceModel> bookMaintenance(@Body MaintBookingModel model);

    @POST("booking_history?ws=spec09645d17359d1ac3d2516d0b7f2")
    Call<BookingHistoryModel> getBookingHistory(@Body JsonObject jsonObject);

    @POST("maint_price_calculation?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<PriceResponseModel>getMaintPrice(@Body PriceRequestModel model);

    @GET("customer_app_update?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<VUpdateModel>getVersion();

    @GET("get_service_area_detail?ws=spec09645d17359d1ac3d2516d0b7f2")
    Call<ServiceAreaModel> getServiceAreas();

    @POST("sms_send?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<PhoneVerifyModel> requestSMS(@Body PhoneVerifyModel model);

    @POST("verify_otp?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<PhoneVerifyModel> submitOTP(@Body JsonObject model);

    //payfort
    @POST("get_sdk_token?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<FortTokenResponce> getPayFortToken(@Body JsonObject tokenParams);

    //Not using this
    @POST("payment_update?ws=spec09645d17359d1ac3d2516d0b7f2")
    Observable<UserModel> updateServerPayment(@Body JsonObject object);


    @POST("payment_update?ws=spec09645d17359d1ac3d2516d0b7f2")
    Single<UserModel> informPayStatus(@Body JsonObject object);

    //Cancel Booking

    @POST("booking_cancel?ws=spec09645d17359d1ac3d2516d0b7f2")
    Call<CommonResponse> cancelBooking(@Body JsonObject jsonObject);
}
