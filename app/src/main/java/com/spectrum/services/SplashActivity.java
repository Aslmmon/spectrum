package com.spectrum.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.models.CrewinModel;
import com.spectrum.services.models.ServiceArea;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity implements ProviderInstaller.ProviderInstallListener {
    public static final String TAG = "meassage";
//    @BindView(R.id.iv_splash_gif)
//    ImageView splash_gif;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.button_error)
    Button error_button;
    @BindView(R.id.tv_error)
    TextView error_text;
    @BindView(R.id.tv_wait)
    TextView error_wait;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MainViewModel mainViewModel;
    private Boolean is_internet_connected = false;
    private Boolean extra_dat = false, cre_data = false, maint_data = false, clean_data = false, area_data = false, ver_data = false, ver_checked = false;

    private static final int ERROR_DIALOG_REQUEST_CODE = 1;

    private boolean mRetryProviderInstall;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        ProviderInstaller.installIfNeededAsync(this, SplashActivity.this);
        initMap();
        initSome();
        delay();




    }

    @Override
    public void onProviderInstalled() {
        Log.e(TAG, "onProviderInstalled: success" );
        apiCallListeners();

    }

    @Override
    public void onProviderInstallFailed(int i, Intent intent) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        if (availability.isUserResolvableError(i)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            availability.showErrorDialogFragment(
                    this,
                    i,
                    ERROR_DIALOG_REQUEST_CODE,
                    dialog -> {
                        // The user chose not to take the recovery action
                        onProviderInstallerNotAvailable();
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            mRetryProviderInstall = true;
        }
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRetryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        mRetryProviderInstall = false;
    }



    private void onProviderInstallerNotAvailable() {
        Toast.makeText(this, "Spectrum needs latest Google Play Services to Work", Toast.LENGTH_SHORT).show();
    }

    private void initSome() {

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);


//        Glide.with(this)
//                .load(R.drawable.spectrum_logo_gif)
//                .into(new DrawableImageViewTarget(splash_gif) {
//                    @Override
//                    public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        if (resource instanceof GifDrawable) {
//                            ((GifDrawable) resource).setLoopCount(1);
//                        }
//                        super.onResourceReady(resource, transition);
//                    }
//                });

        error_wait.setVisibility(View.GONE);
        error_button.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        error_text.setVisibility(View.GONE);


        error_button.setOnClickListener(v -> {
            apiCallListeners();
            delay();

        });
    }




    private void apiCallListeners() {
        progressBar.setVisibility(View.VISIBLE);
        error_button.setVisibility(View.GONE);
        error_text.setVisibility(View.GONE);
        if (Utils.isNetworkAvailable(this)) {
            is_internet_connected = true;
        }
        disposable.add(mainViewModel.getExtraServiceApi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(cleanExtraService -> cleanExtraService)
                .subscribe(this::handleExtraServiceData, throwable -> {
                    if (throwable instanceof IOException) {
                        is_internet_connected = false;
                    }
                    extra_dat = false;


                }));

        disposable.add(mainViewModel.getServiceAreaApi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(serviceArea -> serviceArea)
                .subscribe(this::handleServiceAreaData, throwable -> {
                    if (throwable instanceof IOException) {
                        is_internet_connected = false;
                    }
                    area_data = false;


                }));

        disposable.add(mainViewModel.getCleaningServiceDetails()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(serviceDetails -> serviceDetails)
                .subscribe(this::handleCleaningServicDetails, throwable -> {
                    if (throwable instanceof IOException) {
                        is_internet_connected = false;
                    }
                    clean_data = false;


                }));

        disposable.add(mainViewModel.getMaintenanceServiceDetails()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(serviceDetails -> serviceDetails)
                .subscribe(this::handleMaintenanceServiceDetails, throwable -> {
                    if (throwable instanceof IOException) {
                        is_internet_connected = false;
                    }
                    maint_data = false;

                }));

        disposable.add(mainViewModel.getCrewin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(crewinModel -> crewinModel)
                .subscribe(this::handleCrewinData, throwable -> {
                    if (throwable instanceof IOException) {
                        is_internet_connected = false;
                    }
                    cre_data = false;

                }));


    }

    private void handleCrewinData(CrewinModel crewinModel) {
        if (crewinModel.getStatus().equalsIgnoreCase("success")) {
            cre_data = true;
            Shared.crewinModelSingle = Single.create(emitter -> emitter.onSuccess(crewinModel.getStrings()));

        }

    }

    private void handleCleaningServicDetails(ServiceDetails serviceDetails) {
        if (serviceDetails.getMessage().equalsIgnoreCase("success")) {
            clean_data = true;
            Shared.cleaningServiceDEtails = Single.create(emitter -> emitter.onSuccess(serviceDetails.getServiceData()));
        }


    }

    private void handleMaintenanceServiceDetails(ServiceDetails serviceDetails) {
        if (serviceDetails.getMessage().equalsIgnoreCase("success")) {
            maint_data = true;
            Shared.maintenanceServiceDEtails = Single.create(emitter -> emitter.onSuccess(serviceDetails.getServiceData()));

        }


    }

    private void handleExtraServiceData(CleanExtraService cleanExtraService) {
        if (cleanExtraService.getStatus().equalsIgnoreCase("success")) {
            Log.e(TAG, "handleExtraServiceData: ");
            extra_dat = true;

            for (CleanExtraService.ExtraServiceData service : cleanExtraService.getExtraServiceData()) {
                service.setIs_selected(false);
            }

            Shared.extraServiceModels = Single.create(emitter -> emitter.onSuccess(cleanExtraService.getExtraServiceData()));


        }


    }

    private void handleServiceAreaData(ServiceArea serviceArea) {
        if (serviceArea.getStatus().equalsIgnoreCase("success")) {
            Log.e(TAG, "handleExtraServiceData: ");

            Shared.serviceArea = Single.create(emitter -> emitter.onSuccess(serviceArea));


        }


    }

    @SuppressLint("CheckResult")
    private void delay() {
        disposable.add(Completable.timer(4000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> startHome()));




    }

    public void startHome() {

        if (is_internet_connected && maint_data && clean_data && extra_dat && cre_data) {
            error_wait.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);


            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();


        } else {

            if (Utils.isNetworkAvailable(this)) {
                error_text.setVisibility(View.GONE);
                error_wait.setVisibility(View.VISIBLE);
                error_wait.setText("wait..!");
                apiCallListeners();
                delay();


            } else {
                error_wait.setVisibility(View.GONE);
                error_text.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                error_button.setVisibility(View.VISIBLE);
                error_text.setText("Check your connection !");
            }

            Log.e(TAG, "startHome: No internet");
        }


    }

    private void initMap() {

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
