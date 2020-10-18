package com.spectrum.services.drawer.service_area;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.R;
import com.spectrum.services.models.drawer.ServiceAreaModel;
import com.spectrum.services.utils.KeyboardUtil;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceAreaActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = "message";
    private GoogleMap mMap;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.relative_bottom_sheet_sarea)
    RelativeLayout bottom_sheet;
    @BindView(R.id.recycler_service_area)
    RecyclerView recycler_sarea;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.et_serach)
    EditText serach_text;

    private ServiceAreaAdapter sareaAdapter;
    private List<ServiceAreaModel.SAreaData> areaData = new ArrayList<>();

    private BottomSheetBehavior bottomSheetBehavior;
    private ProgressDialog progressDialog;
    private CompositeDisposable disposable=new CompositeDisposable();
    private Map<String,Marker>markerStringMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_area);
        ButterKnife.bind(this);
        setupToolbar();

        initSome();

        //initProgressDilaog();

        Completable.fromRunnable(() -> {

            setupBottomsheet();
            initRecycler();
            getServiceAreaApi();
            listenforObservables();



        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();



    }

    private void initSome() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(ServiceAreaActivity.this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


    }
    private void setupBottomsheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        serach_text.setEnabled(true);

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        serach_text.setEnabled(false);

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        new KeyboardUtil(this, bottom_sheet);

        //serach stuff
        serach_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sareaAdapter.searchArea(s.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bottom_sheet.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));


    }

    @OnClick(R.id.relative_sarea_search)
    public void relativeSerachClick(View view)
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    private void initRecycler() {
        recycler_sarea.setLayoutManager(new LinearLayoutManager(this));
        if (sareaAdapter == null) {
            sareaAdapter = new ServiceAreaAdapter(areaData, this);
            recycler_sarea.setAdapter(sareaAdapter);
        }


    }

    private void getServiceAreaApi() {
        progressBar.setVisibility(View.VISIBLE);
        Utils.getApiService().getServiceAreas()
                .enqueue(new Callback<ServiceAreaModel>() {
                    @Override
                    public void onResponse(Call<ServiceAreaModel> call, Response<ServiceAreaModel> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                areaData.addAll(response.body().getDataList());
                                sareaAdapter = new ServiceAreaAdapter(areaData, ServiceAreaActivity.this);
                                recycler_sarea.setAdapter(sareaAdapter);
                                sareaAdapter.notifyDataSetChanged();

                                setMarker(response.body());



                            } else {
                                Log.e(TAG, "onResponse: fail");


                            }

                        } else {

                        }

                    }

                    @Override
                    public void onFailure(Call<ServiceAreaModel> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "onResponse: fail" + t.getMessage());
                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_LONG)
                                .setAction("Retry",view -> {
                                    getServiceAreaApi();
                                })
                                .show();

                    }
                });
    }

    private void setMarker(ServiceAreaModel areaData) {

        for (int i=0;i<areaData.getDataList().size();i++)
        {
            if (areaData.getDataList().get(i).getLongitude()!=null&&areaData.getDataList().get(i).getLatitude()!=null)
            {
                MarkerOptions options = new MarkerOptions();
                options.title(areaData.getDataList().get(i).getName())
                        .position(new LatLng(Double.parseDouble(areaData.getDataList().get(i).getLatitude()), Double.parseDouble(areaData.getDataList().get(i).getLongitude())))
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this,R.drawable.service_area_map_marker)));
                markerStringMap.put(areaData.getDataList().get(i).getName(), mMap.addMarker(options));

            }



        }

    }

    private void focusMarker(LatLng latLng,String title) {


        CameraPosition position=new CameraPosition.Builder().
                target((new LatLng(latLng.latitude,latLng.longitude)))
                .zoom(10)
                .build();
        markerStringMap.get(title).showInfoWindow();

        CameraUpdate update= CameraUpdateFactory.newCameraPosition(position);
        mMap.animateCamera(update);


    }
    private void listenforObservables()
    {
        disposable.add(((MyApplication)getApplication())
                .bus()
                .toObservable()
                .subscribe(o -> {
                    if(o instanceof ServiceAreaModel.SAreaData)
                    {
                        if (((ServiceAreaModel.SAreaData) o).getLatitude()!=null&&((ServiceAreaModel.SAreaData) o).getLongitude()!=null)
                        {
                            focusMarker(new LatLng(Double.parseDouble(((ServiceAreaModel.SAreaData) o).getLatitude()),
                                    Double.parseDouble(((ServiceAreaModel.SAreaData) o).getLongitude())),((ServiceAreaModel.SAreaData) o).getName());
                            hideSoftKeyboard();

                        }





                    }

                }));

    }
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void initProgressDilaog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
    }

    private void setupToolbar() {
        toolbar.setTitle("Service Area");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onBackPressed();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // move the camera to UAE
        LatLng uae = new LatLng(23.4241, 53.8478);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uae,6));
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
        super.onDestroy();
    }
}
