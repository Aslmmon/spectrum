package com.spectrum.services.booking.cleaning;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.spectrum.services.R;
import com.spectrum.services.adapters.BookingExtraServiceAdapter;
import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.models.MainCleanBookModel;
import com.spectrum.services.models.PriceRequestModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BookCleaningActivity extends AppCompatActivity implements BookingExtraServiceAdapter.InterfaceExtraServiceData {
    public static final String TAG = "BookCleaningActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_bar_price)
    ProgressBar progressbar_price;
    @BindView(R.id.spinner_type_of_service)
    Spinner spinner_type_of_service;
    @BindView(R.id.spinner_crew_in)
    Spinner spinner_crew_in;
    @BindView(R.id.recycler_extra_service_items)
    RecyclerView recycler_extra_service;

    @BindView(R.id.tv_booking_charge_per)
    TextView booking_charge_per;
    @BindView(R.id.tv_booking_total_amount)
    TextView booking_total_amount;
    @BindView(R.id.et_promocode)
    EditText promocode;
    @BindView(R.id.tv_booking_promo_amount)
    TextView promocode_amount;
    @BindView(R.id.tv_booking_vat_amount)
    TextView booking_vat_amount;
    @BindView(R.id.tv_vat_label)
    TextView booking_vat_label;
    @BindView(R.id.tv_booking_service_amount)
    TextView booking_service_amount;

    @BindView(R.id.tv_hour)
    TextView booking_hour;
    @BindView(R.id.tv_maids)
    TextView booking_maids;
    @BindView(R.id.tiet_other_crewin)
    TextInputEditText otherway_crewin;
    @BindView(R.id.et_instruction)
    EditText instruction;

    @BindView(R.id.constrain_extra_service)
    ConstraintLayout constrain_extra_service;
    @BindView(R.id.relative_card_container)
    RelativeLayout relative_card_container;

    BookingExtraServiceAdapter extraServiceAdapter;
    Integer h = 4, m = 1;

    //spinner adapters
    ArrayAdapter<String> adapter_services;
    ArrayAdapter<String> adapter_crewin;

    private Boolean isCleaningMatOpted = false, is_promo_applied = false;
    private final String AMOUNTPERSERVICE = "AED 40.00/HR";
    private int total_amount;

    private String str_type_service, str_crew_in, str_instr, str_type_service_id, applied_coupen_val = "";
    private BookCleanViewModel viewModel;
    private MainCleanBookModel cleaningBookModel;
    private CompositeDisposable disposable = new CompositeDisposable();
    private Map<Boolean, CleanExtraService.ExtraServiceData> map_sel_extra_service = new HashMap<>();
    private List<CleanExtraService.ExtraServiceData> list_sel_extra_service = new ArrayList<>();
    private Boolean data_fetched = false;
    private PriceResponseModel priceResponseModel;
    private String getPromocode = "";
    Boolean deeplink;

    private Prefs prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cleaning);
        ButterKnife.bind(this);
        deeplink = getDeepLinkIfPresent();
        someInits();
        setupToolbar();
        initRecycler();
        actionListeners();

        if (!deeplink) apiCallListeners();

        if (getPromocode != null) {
            Log.i("data",getPromocode);
            promocode.setText(getPromocode);
        }


    }

    private boolean getDeepLinkIfPresent() {
        Uri uri = getIntent().getData();
        if (uri != null) {

            List<String> params = uri.getPathSegments();
            String promoCode = params.get(params.size() - 1);
            getPromocode = promoCode;
            Toast.makeText(this, promoCode, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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
                    }

                }));
    }

    private void someInits() {

        viewModel = ViewModelProviders.of(this).get(BookCleanViewModel.class);
        Shared.listSelectedExtraSErvice.clear();
        prefs = Prefs.with(this);
        priceCalcApi();

        total_amount = 100;
        recycler_extra_service.setActivated(false);
        recycler_extra_service.setVisibility(View.GONE);
        booking_hour.setText(h.toString());
        booking_maids.setText(m.toString());


        otherway_crewin.setVisibility(View.GONE);
        //progressbar_price.setVisibility(View.GONE);
    }

    private void apiCallListeners() {

        disposable.add(viewModel.getExtraServiceData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(extraServiceData -> {
                    Log.e(TAG, "apiCallListeners: " + extraServiceData.size());
                    handleExtraServiceData(extraServiceData);

                }, throwable -> {
                    Log.e(TAG, "handleExtraServiceData: " + throwable.getMessage());
                }));

        disposable.add((viewModel.getServiceData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(serviceData -> serviceData)
                .subscribe(this::handleCleaningServiceData, throwable -> {

                    Log.e(TAG, "apiCallListeners: " + throwable.getMessage());

                }));

        disposable.add(viewModel.getCrewinData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(strings -> strings)
                .subscribe(this::handleCrewinData, throwable -> {
                    Log.e(TAG, "apiCallListeners: " + throwable.getMessage());

                }));
    }

    public void handleCleaningServiceData(List<ServiceDetails.ServiceData> data) {
        if (data != null) {
            Log.e(TAG, "handleCleaningServiceData: " + data.size());
            if (data.size() > 0) {
                setupDataServiceSpinner(data);

            }

        }


    }

    public void handleCrewinData(List<String> strings) {
        if (strings != null) {
            Log.e(TAG, "handleCrewinData: " + strings.size());
            if (strings.size() > 0) {
                setupCrewinSpinner(strings);

            }

        }

    }


    private void handleExtraServiceData(List<CleanExtraService.ExtraServiceData> cleanExtraService) {
        Shared.listSelectedExtraSErvice.addAll(cleanExtraService);
        extraServiceAdapter = new BookingExtraServiceAdapter(this, cleanExtraService);
        recycler_extra_service.setAdapter(extraServiceAdapter);
        extraServiceAdapter.notifyDataSetChanged();


    }

    @Override
    public void extraService() {
        Log.e(TAG, "extraService: ");
        //update price for extraservice opt.
        priceCalcApi();

    }

    private List<CleanExtraService.ExtraServiceData> getSelectedExtraService() {
        list_sel_extra_service.clear();

        for (CleanExtraService.ExtraServiceData data : Shared.listSelectedExtraSErvice) {

            if (data.getIs_selected()) {
                list_sel_extra_service.add(data);
            }


        }
        return list_sel_extra_service;
    }

    private void setupDataServiceSpinner(List<ServiceDetails.ServiceData> data) {

        String[] spinner_data_services = new String[data.size()];

        for (int i = 0; i < data.size(); i++) {
            spinner_data_services[i] = data.get(i).getService();
        }
        Log.e(TAG, "setupSpinner: " + spinner_data_services[0]);

        adapter_services = new ArrayAdapter<>(this, R.layout.spinner_item_layout, spinner_data_services);

        adapter_services.setDropDownViewResource(R.layout.spinner_item_layout);

        spinner_type_of_service.setAdapter(adapter_services);

        spinner_type_of_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_type_service_id = data.get(i).getService_id();
                str_type_service = adapterView.getItemAtPosition(i).toString();
                Log.e(TAG, "onItemSelected: " + str_type_service + " " + str_type_service_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                str_type_service = "";
//                str_type_service_id="1";

            }
        });
        //ToDo type service removed from ui,so set home cleaning default
        str_type_service_id = "1";

    }

    private void setupCrewinSpinner(List<String> strings) {
        String[] list3 = {"Mommy", "Daddy", "Mia"};


        String[] spinner_crewin = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            spinner_crewin[i] = strings.get(i);
        }


        adapter_crewin = new ArrayAdapter<>(this, R.layout.spinner_item_layout, spinner_crewin);
        adapter_crewin.setDropDownViewResource(R.layout.spinner_item_layout);


        spinner_crew_in.setAdapter(adapter_crewin);

        spinner_crew_in.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  Log.e("")
                Log.e(TAG, "onItemSelected:crew in " + "gfdgdfgdfg");

                if (Utils.isStringEqual(adapterView.getItemAtPosition(i).toString(), "others")) {
                    otherway_crewin.clearComposingText();
                    otherway_crewin.setVisibility(View.VISIBLE);
                    otherway_crewin.requestFocus();
                    str_crew_in = "other";
                } else {
                    str_crew_in = adapterView.getItemAtPosition(i).toString();
                    otherway_crewin.setVisibility(View.GONE);
                    otherway_crewin.clearFocus();
                    hideKeyboard();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    private void initRecycler() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        recycler_extra_service.setLayoutManager(layoutManager);
    }


    private void setupToolbar() {
        toolbar.setTitle("Cleaning Booking");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onBackPressed();
    }

    @OnClick(R.id.imageView_spinner_type_service_click)
    public void spinnerServiceClick() {

        spinner_type_of_service.performClick();
    }


    @OnClick(R.id.imageView_extra_service_expand_click)
    public void extraServiceExpandClick(View view) {
        if (recycler_extra_service.isActivated()) {
            recycler_extra_service.setVisibility(View.GONE);
            view.setRotation(360);
            recycler_extra_service.setActivated(false);


        } else {
            recycler_extra_service.setVisibility(View.VISIBLE);
            view.setRotation(180);
            recycler_extra_service.setActivated(true);
        }


    }

    @OnClick(R.id.imageView_material_tick)
    public void cleaningMaterialTickClick(View view) {
        if (!isCleaningMatOpted) {
            view.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
            isCleaningMatOpted = true;
            priceCalcApi();
        } else {
            view.setBackground(getResources().getDrawable(R.drawable.round_bg_grey));
            isCleaningMatOpted = false;
            priceCalcApi();
        }
    }

    @OnClick(R.id.imageView_spinner_crew_in_click)
    public void crewInClick(View view) {
        spinner_crew_in.performClick();

    }

    @OnClick(R.id.rect_booking_next_click)
    public void nextBookingClick() {
        if (deeplink){
            Snackbar.make(progressbar_price, "Enter correct promo code", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (checkCrewinValidation()) {
            if (priceResponseModel != null) {
                Intent intent = new Intent(BookCleaningActivity.this, BookCleaningDateActivity.class);
                intent.putExtra("data", setBookingDataToMOdel());
                intent.putExtra("price", Utils.convertmodelToString(priceResponseModel));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            } else {
                priceCalcApi();
                Snackbar.make(progressbar_price, "Price Calculating..please wait", Snackbar.LENGTH_SHORT).show();


            }
        } else {
            Snackbar.make(progressbar_price, "Enter other way to let crew in !", Snackbar.LENGTH_SHORT).show();
        }

    }


    public Boolean checkCrewinValidation() {

        if (str_crew_in.equalsIgnoreCase("other")) {
            return !otherway_crewin.getText().toString().isEmpty();
        }
        return true;
    }

    @OnClick(R.id.imageView_hours_minus)
    public void bookingHoursMinus(View view) {
        h = Integer.parseInt(booking_hour.getText().toString());
        if (h > 2) {
            h--;
            booking_hour.setText("" + h);
            priceCalcApi();
        }

    }

    @OnClick(R.id.imageView_hours_plus)
    public void bookingHoursPlus(View view) {
        h = Integer.parseInt(booking_hour.getText().toString());
        if (h > 0 && h < 10) {
            h++;
            booking_hour.setText("" + h);
            priceCalcApi();
        }

    }

    @OnClick(R.id.imageView_maids_minus)
    public void bookingMaidsMinus(View view) {

        m = Integer.parseInt(booking_maids.getText().toString());
        if (m > 1) {
            m--;
            booking_maids.setText("" + m);
            priceCalcApi();
        }

    }

    @OnClick(R.id.imageView_maids_plus)
    public void bookingMaidsPlus(View view) {
        m = Integer.parseInt(booking_maids.getText().toString());
        if (m > 0 && m < 5) {
            m++;
            booking_maids.setText("" + m);
            priceCalcApi();
        }

    }

    private void priceCalcApi() {
        priceResponseModel = null;
        progressbar_price.setVisibility(View.VISIBLE);
        booking_total_amount.setVisibility(View.INVISIBLE);
        booking_vat_amount.setVisibility(View.INVISIBLE);
        booking_service_amount.setVisibility(View.INVISIBLE);
        promocode_amount.setVisibility(View.INVISIBLE);

        disposable.add(viewModel.getPriceDetailApi(setupPriceRequestModel())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(priceResponseModel -> priceResponseModel)
                .subscribe(this::handlePriceResponce, throwable -> {

                    progressbar_price.setVisibility(View.GONE);
                    booking_total_amount.setVisibility(View.INVISIBLE);
                    booking_vat_amount.setVisibility(View.INVISIBLE);
                    booking_service_amount.setVisibility(View.INVISIBLE);
                    promocode_amount.setVisibility(View.INVISIBLE);

                    Snackbar.make(progressbar_price, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", view -> priceCalcApi())
                            .show();

                }));
    }

    private void handlePriceResponce(PriceResponseModel model) {
        if (model.getStatus().equalsIgnoreCase("success")) {
            priceResponseModel = model;
            progressbar_price.setVisibility(View.GONE);
            booking_total_amount.setVisibility(View.VISIBLE);
            booking_vat_amount.setVisibility(View.VISIBLE);
            booking_service_amount.setVisibility(View.VISIBLE);
            promocode_amount.setVisibility(View.VISIBLE);


            booking_vat_label.setText("VAT " + model.getPrice().getVat_percentage() + "%");
            booking_service_amount.setText("AED " + model.getPrice().getService_rate());
            booking_charge_per.setText("AED " + model.getPrice().getHour_rate() + " /HR");
            booking_vat_amount.setText("AED " + model.getPrice().getVat_charge());
            booking_total_amount.setText("AED " + model.getPrice().getGross_amount());
            promocode_amount.setText("- AED " + model.getPrice().getDiscount());

            //promocode conditions


            if (!promocode.getText().toString().isEmpty()) {
                Log.e(TAG, "handlePriceResponce: inside main ");


                if (!is_promo_applied) {
                    if (model.getCoupen_status().equalsIgnoreCase("applied") && model.getPrice().getCoupen_val().equals(promocode.getText().toString())) {
                        is_promo_applied = true;
                        applied_coupen_val = promocode.getText().toString();

                        Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();
                    } else if (model.getCoupen_status().equalsIgnoreCase("not")) {
                        Log.e(TAG, "handlePriceResponce:in not ");
                        is_promo_applied = false;
                        applied_coupen_val = "";
                        promocode.setText("");
                        Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }


                } else if (model.getCoupen_status().equalsIgnoreCase("applied") && !applied_coupen_val.equalsIgnoreCase(model.getPrice().getCoupen_val())) {
                    Log.e(TAG, "handlePriceResponce: inside ");
                    is_promo_applied = true;
                    applied_coupen_val = promocode.getText().toString();

                    Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();
                } else if (model.getCoupen_status().equalsIgnoreCase("not")) {
                    Log.e(TAG, "handlePriceResponce:in not ");
                    is_promo_applied = false;
                    applied_coupen_val = "";
                    promocode.setText("");
                    Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();

                }


            } else if (model.getCoupen_status().equalsIgnoreCase("applied")) {
                is_promo_applied = true;
                applied_coupen_val = model.getPrice().getCoupen_val();
                promocode.setText(applied_coupen_val);
                Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();

            }


        }

    }

    private PriceRequestModel setupPriceRequestModel() {
        ArrayList<String> extraServiceId = new ArrayList<>();
        for (CleanExtraService.ExtraServiceData data : getSelectedExtraService()) {
            extraServiceId.add(data.getExtra_service_id());
        }
        PriceRequestModel model = new PriceRequestModel(prefs.getUserid() != null ? prefs.getUserid() : "", h.toString(), m.toString(),
                isCleaningMatOpted ? "Y" : "N", str_type_service_id, "1", promocode.getText().toString().isEmpty() ? "" : promocode.getText().toString(),
                extraServiceId, getDayfromDate(Calendar.getInstance().getTime()));
        return model;
    }

    private String getDayfromDate(Date date) {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");
        String date_str = sformat.format(date);
        return date_str;
    }

    @OnClick(R.id.promo_apply_click)
    public void coupenCodeApply(View view) {
        priceCalcApi();
        hideKeyboard();
    }

    private String setBookingDataToMOdel() {

        for (CleanExtraService.ExtraServiceData data : getSelectedExtraService()) {
            Log.e(TAG, "setBookingDataToMOdel: " + data.getExtra_service_name());
        }
        str_crew_in = str_crew_in.equalsIgnoreCase("other") ? otherway_crewin.getText().toString() : str_crew_in;
        Log.e(TAG, "setBookingDataToMOdel:crew in " + str_crew_in);

        cleaningBookModel = new MainCleanBookModel(h.toString(), m.toString(), str_type_service, isCleaningMatOpted.toString(), str_crew_in, instruction.getText().toString(), getSelectedExtraService(), str_type_service_id);
        return Utils.convertmodelToString(cleaningBookModel);

    }


    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
