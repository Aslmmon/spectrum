package com.spectrum.services.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.payfort.fort.android.sdk.base.FortSdk;
import com.payfort.fort.android.sdk.base.callbacks.FortCallBackManager;
import com.payfort.fort.android.sdk.base.callbacks.FortCallback;
import com.payfort.sdk.android.dependancies.base.FortInterfaces;
import com.payfort.sdk.android.dependancies.models.FortRequest;
import com.spectrum.services.R;
import com.spectrum.services.booking.cleaning.PaymentStatusActivity;
import com.spectrum.services.models.payfort.FortTokenResponce;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PaymentActivity extends AppCompatActivity {
    public static final String TAG = "message";
    PayViewModel viewModel;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    //pay_fort
    String deviceId = "";
    String sdkToken = "";
    CompositeDisposable disposable = new CompositeDisposable();

    String amount;
    String ref_id;
    String vat_perc;

    Boolean is_proceed_pay = false;


    FortRequest fortrequest;
    private FortCallBackManager fortCallback = null;
    Prefs prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_payment);
        ButterKnife.bind(this);
        initSome();
        actionListeners();
    }

    private void initSome() {
        prefs = Prefs.with(this);
        viewModel = ViewModelProviders.of(this).get(PayViewModel.class);

        amount = getIntent().getStringExtra("amount");
        ref_id = getIntent().getStringExtra("ref");
        vat_perc=getIntent().getStringExtra("vat");

        if (amount != null && ref_id != null) {
            initPaymentSdk();

        } else {
            finish();
        }


    }

    private void initPaymentSdk() {
        viewModel.initData(amount, ref_id);
        // create Fort callback instance
        fortCallback = FortCallback.Factory.create();

        deviceId = FortSdk.getDeviceId(PaymentActivity.this);
        Log.e(TAG, "device id " + deviceId);

        requestTokenVM();
        //callPayment("690637406E580C48E053321E320AAAAB");


    }


    private void callSdk(FortRequest fortrequest) {
        is_proceed_pay = true;

        try {
            FortSdk.getInstance().registerCallback(PaymentActivity.this, fortrequest, FortSdk.ENVIRONMENT.PRODUCTION, 5,
                    fortCallback, new FortInterfaces.OnTnxProcessed() {
                        @Override
                        public void onCancel(Map<String, Object> requestParamsMap, Map<String,
                                Object> responseMap) {

                            //TODO: handle me
                            Log.e(TAG, "cancelled " + responseMap.toString());
                            is_proceed_pay = false;
                            onBackPressed();
                            handlePaymentStatus(responseMap);
                        }

                        @Override
                        public void onSuccess(Map<String, Object> requestParamsMap, Map<String,
                                Object> fortResponseMap) {
                            //TODO: handle me
                            Log.e(TAG, "success " + fortResponseMap.toString());
                            //updateServerPayApi(true, fortResponseMap);
                            handlePaymentStatus(fortResponseMap);
                        }

                        @Override
                        public void onFailure(Map<String, Object> requestParamsMap, Map<String,
                                Object> fortResponseMap) {
                            //TODO: handle me
                            Log.e(TAG, "failure " + fortResponseMap.toString());
                           // updateServerPayApi(false, fortResponseMap);
                            handlePaymentStatus(fortResponseMap);
                        }

                    });
        } catch (Exception e) {
            Log.e(TAG, "call FortSdk " + e);
        }

    }

    private void handlePaymentStatus(Map<String, Object> fortResponseMap) {

        Intent intent = new Intent(PaymentActivity.this, PaymentStatusActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("ref", ref_id);
        intent.putExtra("fort_id", fortResponseMap.get("fort_id") == null ? "" : fortResponseMap.get("fort_id").toString());
        intent.putExtra("vat",vat_perc);

        is_proceed_pay = false;


        if (fortResponseMap.get("response_message").toString().equalsIgnoreCase("Success")) {
            intent.putExtra("paystatus", true);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            //this.finish();


        } else {

            intent.putExtra("fail_message", fortResponseMap.get("response_message") == null ? "" : fortResponseMap.get("response_message").toString());
            intent.putExtra("paystatus", false);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            //this.finish();

        }


    }

    public void updateServerPayApi(Boolean b, Map<String, Object> fortResponce) {
        JsonObject object = new JsonObject();
        object.addProperty("customer_id", prefs.getUserid());
        object.addProperty("transaction_id", fortResponce.get("fort_id") == null ? "" : fortResponce.get("fort_id").toString());
        object.addProperty("booking_id", ref_id);
        object.addProperty("amount", amount);
        if (b) {
            object.addProperty("payment_status", "success");
        } else {
            object.addProperty("payment_status", "failed");

        }

        disposable.add(viewModel.updateServerPaymentApi(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
        //this.finish();
    }


    public void requestTokenVM() {
        is_proceed_pay = true;
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(viewModel.getTokenApi(viewModel.getTokenParams(deviceId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleTokenRequest, throwable -> {
                    is_proceed_pay = false;
                    Log.e(TAG, "requestToken: " + throwable.getMessage());
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();

                }));
    }

    public void handleTokenRequest(FortTokenResponce responce) {

        progressBar.setVisibility(View.GONE);
        if (responce.getStatus().equalsIgnoreCase("22")) {
            Log.e(TAG, "handleTokenRequest: success");
            sdkToken = responce.getSdk_token();

            if(viewModel.checkShaResponce(responce))
            {
                callPayment(sdkToken);

            }
            else
            {
                 Snackbar.make(progressBar, "Something went wrong while setting up your payment, Please try again.", Snackbar.LENGTH_SHORT).show();
                disposable.add(Completable.timer(3,TimeUnit.SECONDS).subscribe(()->finish()));
            }




        } else {
            is_proceed_pay = false;
        }


    }



    public void callPayment(String sdkToken) {
        // prepare payment request
        fortrequest = new FortRequest();
        fortrequest.setRequestMap(viewModel.collectRequestMap(sdkToken));
        fortrequest.setShowResponsePage(true); // to [display/use] the SDK response page

        callSdk(fortrequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            fortCallback.onActivityResult(requestCode, resultCode, data);
        } else {
            this.finish();
        }

    }

    private void actionListeners() {
        disposable.add(((MyApplication) getApplication()).bus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof String) {
                        if (((String) o).equalsIgnoreCase("kill")) {
                            this.finish();
                        }
                        if (((String) o).equalsIgnoreCase("killpay")) {
                            this.finish();
                        }
                    }

                })

        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onBackPressed() {
        if (is_proceed_pay)
            return;
        //super.onBackPressed();
    }
}
