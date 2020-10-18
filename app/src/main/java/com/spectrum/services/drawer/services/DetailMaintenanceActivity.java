package com.spectrum.services.drawer.services;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.BaseActivity;
import com.spectrum.services.R;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMaintenanceActivity extends BaseActivity {
    public static final String TAG = "message";
    @BindView(R.id.recycler_clean_service)
    RecyclerView cservice_recycler;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private DetailServiceAdapter detailServiceAdapter;
    private List<ServiceDetails.ServiceData>serviceData=new ArrayList<>();

    ProgressDialog progressDialog;
    private CompositeDisposable disposable=new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_maintenance);
        ButterKnife.bind(this);
        initToolbar();
        //initProgressDilaog();
        initRecycler();
        //getCleanServiceDetail();
        getCleanDeatils();

        if (!Utils.isNetworkAvailable(this))
        {
             Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();
        }
        logViewContentEvent("Maintainance Service","data","id","USD",0);




    }
    private void initRecycler()
    {

        cservice_recycler.setLayoutManager(new GridLayoutManager(this,2));
        if(detailServiceAdapter==null)
        {
            detailServiceAdapter=new DetailServiceAdapter(serviceData,this,false);
            cservice_recycler.setAdapter(detailServiceAdapter);
        }

    }
    private void initProgressDilaog()
    {
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
    }
    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitle("Maintenance Service");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp_primary);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void getCleanServiceDetail()
    {
        progressBar.setVisibility(View.VISIBLE);

        Utils.getApiService().getMaintenanceDetails()
                .enqueue(new Callback<ServiceDetails>() {
                    @Override
                    public void onResponse(Call<ServiceDetails> call, Response<ServiceDetails> response) {
                        progressBar.setVisibility(View.GONE);
                        if(response.isSuccessful())
                        {

                            if(response.body().getStatus().equalsIgnoreCase("success"))
                            {

                                serviceData.addAll(response.body().getServiceData());
                                Log.e(TAG, "onResponse: "+serviceData.get(0).getService() );
                                detailServiceAdapter=new DetailServiceAdapter(serviceData,DetailMaintenanceActivity.this,false);
                                cservice_recycler.setAdapter(detailServiceAdapter);

                                detailServiceAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: failure" );
                                 Snackbar.make(cservice_recycler, "Failure", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServiceDetails> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        Snackbar.make(cservice_recycler, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                    }
                });
    }
    private void getCleanDeatils() {
        serviceData.clear();
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(Shared.maintenanceServiceDEtails
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleServiceDetails,throwable -> {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(cservice_recycler, "Failure", Snackbar.LENGTH_SHORT).show();
                }));
    }

    private void handleServiceDetails(List<ServiceDetails.ServiceData> data) {
        if (serviceData!=null) {
            progressBar.setVisibility(View.GONE);
            serviceData.addAll(data);
            Log.e(TAG, "onResponse: " + serviceData.size());
            detailServiceAdapter = new DetailServiceAdapter(serviceData, DetailMaintenanceActivity.this, false);
            cservice_recycler.setAdapter(detailServiceAdapter);

            detailServiceAdapter.notifyDataSetChanged();
        }
        else
        {

        }
    }

}
