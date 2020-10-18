package com.spectrum.services.booking.maintenance;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.R;
import com.spectrum.services.adapters.TypeMinPriceAdaptern;
import com.spectrum.services.auth.LoginActivity;
import com.spectrum.services.frags.MaintTypeDetailsFrag;
import com.spectrum.services.models.BookTime;
import com.spectrum.services.models.MaintBookingModel;
import com.spectrum.services.models.PriceRequestModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.profile.ProfileActivity;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BookMaintenanceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.progress_bar_price)
    ProgressBar progressbar_price;
    @BindView(R.id.spinner_type_of_service)
    Spinner spinner_service;
    @BindView(R.id.spinner_priority)
    Spinner spinner_priority;
    @BindView(R.id.tv_mbook_date)
    TextView book_date;
    @BindView(R.id.rg_time)
    RadioGroup radio_group_time;
    @BindView(R.id.rb_flexible)
    RadioButton rb_flexible;
    @BindView(R.id.rb_specific)
    RadioButton rb_specific;
    @BindView(R.id.spinner_specific_time)
    Spinner spinner_stime;
    @BindView(R.id.relative_specific_time)
    RelativeLayout relative_stime;
    @BindView(R.id.et_instruction)
    EditText instrn;

    @BindView(R.id.iv_type_details)
    ImageView type_details_click;
    @BindView(R.id.recy_type_price)
    RecyclerView recycler_type_price;
    @BindView(R.id.tv_type_price_note)
    TextView tv_type_price_note;


    @BindView(R.id.et_promocode)
    EditText promocode;
    @BindView(R.id.tv_booking_promo_amount)
    TextView promocode_amount;

    private ArrayAdapter adapter_services, adapter_priority, adapter_spec_time;
    private String sel_time, sel_service_id, sel_priority, sel_service_type, sel_date, applied_coupen_val = "";
    private Boolean isSepecificTime = false;
    private AlertDialog alertDialog;


    private Calendar calendar, calendar_limit;
    private int year, month, day;
    private SimpleDateFormat simpleDateFormat;
    private BookMaintenanceViewModel viewModel;
    private CompositeDisposable disposable = new CompositeDisposable();
    private List<ServiceDetails.ServiceData> listServiceData;
    private List<String> specificTimeList = new ArrayList<>();

    private String formated_date;


    private UserModel.User userModel;
    private BookTime bookTime;
    private Prefs prefs;
    private Boolean is_book_day = true, is_promo_applied = false;
    private PriceResponseModel priceResponseModel;

    private ServiceDetails.ServiceData selServiceTypeItem;
    private  TypeMinPriceAdaptern typeMinPriceAdapter;

    List<String> stringList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maintenance);
        ButterKnife.bind(this);
        setupToolbar();
        setupSpinner();
        initSome();
        eventListener();
        getMaintServiceData();

    }

    private void setupToolbar() {
        toolbar.setTitle("Maintenance Booking");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp_primary);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

    }

    private void eventListener() {
        disposable.add(((MyApplication) getApplication()).bus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof String) {
                        if (((String) o).equalsIgnoreCase("killmaint")) {
                            this.finish();
                        }
                        if (((String) o).equalsIgnoreCase("reviewconfirm")) {
                            nextClick();

                        }
                    }

                }));
    }

    private void initSome() {
        viewModel = ViewModelProviders.of(this).get(BookMaintenanceViewModel.class);
        prefs = Prefs.with(this);

        relative_stime.setVisibility(View.GONE);
        priceCalcApi();


        //radio button stuff
        rb_flexible.setChecked(true);
        radio_group_time.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_flexible: {
                    relative_stime.setVisibility(View.GONE);
                    isSepecificTime = false;
                    break;
                }
                case R.id.rb_specific: {
                    isSepecificTime = true;
                    relative_stime.setVisibility(View.VISIBLE);
                    break;
                }
            }


        });

        //calender Stuff
        calendar = Calendar.getInstance();
        calendar_limit = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            calendar.add(Calendar.DATE, 1);
        } else {
            calendar.add(Calendar.DATE, 0);
        }
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        formated_date=Utils.getDateFormat(simpleDateFormat.format(calendar.getTime()));
        book_date.setText(formated_date);
        sel_date = simpleDateFormat.format(calendar.getTime());
        getSpecificTime(sel_date);

        type_details_click.setOnClickListener(view -> {
            if (listServiceData!=null){
                MaintTypeDetailsFrag frag=MaintTypeDetailsFrag.init(sel_service_type,Utils.convertmodelToString(selServiceTypeItem));
                frag.show(getFragmentManager(),"");

            }
        });



    }


    private void getMaintServiceData() {
        disposable.add(viewModel.getMaintServiceData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(data -> data)
                .subscribe(this::handleMaintServiceType, throwable -> {
                    Snackbar.make(toolbar, "", Snackbar.LENGTH_SHORT).show();
                }));


    }

    private void handleMaintServiceType(List<ServiceDetails.ServiceData> data) {
        if (data != null && data.size() > 0) {
            listServiceData = data;
            setupServiceTypeSpinner(data);


        }

    }


    private void setupServiceTypeSpinner(List<ServiceDetails.ServiceData> data) {
        List<String> list_data_service = new ArrayList<>();
        for (ServiceDetails.ServiceData serviceData : data) {
            list_data_service.add(serviceData.getService());
        }
        Log.e(TAG, "setupSpinner: " + list_data_service.get(0));
        adapter_services = new ArrayAdapter<>(this, R.layout.spinner_item_layout_blue, list_data_service);
        adapter_services.setDropDownViewResource(R.layout.spinner_item_layout_blue);
        spinner_service.setAdapter(adapter_services);

        tv_type_price_note.setText(data.get(0).minCharge.note);

        selServiceTypeItem=data.get(0);
        setupTypePriceRecycler();

        spinner_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sel_service_id = listServiceData.get(i).getService_id();
                sel_service_type = adapterView.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected: " + sel_service_id);

                if (data.get(i).minCharge.note.equalsIgnoreCase("")){
                    tv_type_price_note.setVisibility(View.INVISIBLE);

                }
                else
                {
                    tv_type_price_note.setVisibility(View.VISIBLE);
                }
                if (data.get(i).minCharge.chargeList.size()<0){
                    recycler_type_price.setVisibility(View.GONE);

                }
                else
                {
                    recycler_type_price.setVisibility(View.VISIBLE);

                }

                tv_type_price_note.setText(data.get(i).minCharge.note);
                selServiceTypeItem=data.get(i);
                stringList.clear();
                stringList.addAll(selServiceTypeItem.minCharge.chargeList);
                typeMinPriceAdapter.notifyDataSetChanged();




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getSpecificTime(String date) {
        specificTimeList = null;
        bookTime=null;
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(viewModel.getSpecificTime(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(bookTime -> bookTime)
                .subscribe(this::handleSpecificTime, throwable -> {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", view -> {
                                if (sel_date != null) {
                                    getSpecificTime(sel_date);
                                }

                            })
                            .show();

                }));

    }

    private void handleSpecificTime(BookTime data)
    {
        progressBar.setVisibility(View.GONE);
        bookTime=data;
        if (bookTime.getStatus().equalsIgnoreCase("success")) {
            is_book_day = true;
            specificTimeList = bookTime.getTimeData();
            setupSpecificTimeSpinner(specificTimeList);

        } else if (bookTime.getStatus().equalsIgnoreCase("error")) {
            is_book_day = false;
            Snackbar.make(progressBar, bookTime.getMessage(), Snackbar.LENGTH_SHORT).show();

        }

    }

    private void setupSpecificTimeSpinner(List<String> stringList) {
        String[] spinner_sdata_time = {"10:00 AM", "11:00 AM", "12:00 AM", "01:00 PM", "02:00 PM", "03:00 PM"};
        adapter_spec_time = new ArrayAdapter<>(this, R.layout.spinner_item_layout_blue, stringList);

        adapter_spec_time.setDropDownViewResource(R.layout.spinner_item_layout_blue);
        spinner_stime.setAdapter(adapter_spec_time);

        spinner_stime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sel_time = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void setupSpinner() {
        String[] spinner_data_priority = {"Normal", "Critical", "Urgent"};


        adapter_priority = new ArrayAdapter<String>(this, R.layout.spinner_item_layout_blue, spinner_data_priority);

        adapter_priority.setDropDownViewResource(R.layout.spinner_item_layout_blue);


        spinner_priority.setAdapter(adapter_priority);


        spinner_priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sel_priority = adapterView.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected: " + sel_priority);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @OnClick(R.id.imageView_spinner_type_service_click)
    public void spinnerTypeServiceClick(View view) {
        spinner_service.performClick();

    }

    @OnClick(R.id.imageView_spinner_priority_click)
    public void spinnerPriorityClick(View view) {
        spinner_priority.performClick();

    }

    @OnClick(R.id.imageView_date_click)
    public void dateSelectClick(View view) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookMaintenanceActivity.this, R.style.DatePickerDialogTheme, BookMaintenanceActivity.this, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar_limit.getTimeInMillis());

        datePickerDialog.setTitle("");
        datePickerDialog.show();


    }

    @OnClick(R.id.imageView_spinner_specific_time_click)
    public void expandSpecificTimeRelative(View view) {
        spinner_stime.performClick();
    }


    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        Log.e(TAG, "onDateSet: " + y + "/" + m + "/" + d);
        if (datePicker.isShown()) {
            calendar.set(y, m, d);
            year = y;
            month = m;
            day = d;
            if ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)) {
                Snackbar.make(toolbar, "No service in Fridays", Snackbar.LENGTH_SHORT).show();
            } else {
                formated_date=Utils.getDateFormat(simpleDateFormat.format(calendar.getTime()));
                book_date.setText(formated_date);
                Log.e(TAG, "onDateSet: " + simpleDateFormat.format(calendar.getTime()));
                sel_date = simpleDateFormat.format(calendar.getTime());
                getSpecificTime(sel_date);

            }
        }

    }

    @OnClick(R.id.relative_booking_next_click)
    public void nextClick() {


        if (priceResponseModel != null) {
            MaintBookingModel model = setupbookingdata();
            if (model != null) {

                if (validUserData()) {
                    Intent intent = new Intent(BookMaintenanceActivity.this, BookMaintenanceConfirmActivity.class);
                    intent.putExtra("bookdata", Utils.convertmodelToString(model));
                    intent.putExtra("price", Utils.convertmodelToString(priceResponseModel));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                }


            }


        }else {
            priceCalcApi();
            Snackbar.make(progressBar, "Getting discount..please wait", Snackbar.LENGTH_SHORT).show();
        }


    }


    private MaintBookingModel setupbookingdata() {

        MaintBookingModel model =
                new MaintBookingModel(prefs.getUserid(), sel_service_type, sel_service_id, sel_priority, sel_date,
                        isSepecificTime ? "specific" : "flexible", isSepecificTime ? sel_time : "08:00 am", instrn.getText().toString(),
                        priceResponseModel.getPrice().getCoupen_id(), priceResponseModel.getPrice().getDiscount());

        return model;

    }

    private Boolean validUserData() {
        if (bookTime==null)
        {
             Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                     .setAction("Retry",view -> getSpecificTime(sel_date) )
                     .show();
             return false;

        }
        if (!is_book_day) {
            Snackbar.make(progressBar, "Cant book for the selected date", Snackbar.LENGTH_SHORT).show();
            return false;

        }
        if (isSepecificTime) {
            if (specificTimeList == null) {
                getSpecificTime(sel_date);
                Snackbar.make(progressBar, "Getting specific time..please wait", Snackbar.LENGTH_SHORT).show();
                return false;

            }

        }
        //user details
        userModel = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
        if (prefs.getSignedIn()) {
            if ((userModel.getArea_id() == null) || userModel.getArea_id().equalsIgnoreCase("")
                    || userModel.getPhone() == null || userModel.getPhone().equalsIgnoreCase("")) {

                alertDialog();
                return false;
            }

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("from", "reviewconfirm");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return false;

        }
        return true;

    }

    private void priceCalcApi() {
        priceResponseModel = null;
        progressbar_price.setVisibility(View.VISIBLE);
        promocode_amount.setVisibility(View.INVISIBLE);

        disposable.add(viewModel.getPriceDetailApi(setupPriceRequestModel())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(priceResponseModel -> priceResponseModel)
                .subscribe(this::handlePriceResponce, throwable -> {

                    progressbar_price.setVisibility(View.GONE);
                    promocode_amount.setVisibility(View.INVISIBLE);

                    if (throwable instanceof IOException) {
                        Snackbar.make(progressbar_price, "Check your connection", Snackbar.LENGTH_SHORT).show();
                    }
                    Snackbar.make(progressbar_price, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry",view -> {
                                priceCalcApi();
                            })
                            .show();

                }));
    }

    private void handlePriceResponce(PriceResponseModel model) {
        if (model.getStatus().equalsIgnoreCase("success")) {
            priceResponseModel = model;
            progressbar_price.setVisibility(View.GONE);
            promocode_amount.setVisibility(View.VISIBLE);


            Log.e(TAG, "handlePriceResponce: " + model.getPrice().getDiscount());

            promocode_amount.setText(model.getPrice().getDiscount() + "% OFF");

            //promocode conditions

            if (!promocode.getText().toString().isEmpty()) {


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


            }


        }

    }

    private PriceRequestModel setupPriceRequestModel() {
        ArrayList<String> extraServiceId = new ArrayList<>();

        PriceRequestModel model = new PriceRequestModel(prefs.getUserid() != null ? prefs.getUserid() : "", "", "",
                "", "", "", promocode.getText().toString().isEmpty() ? "" : promocode.getText().toString(), extraServiceId,
                getDayfromDate(Calendar.getInstance().getTime()));
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

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookMaintenanceActivity.this);
        builder.setTitle("Alert");
        builder.setMessage("Update profile details to complete booking");
        builder.setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("from", "reviewconfirm");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
         alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }

    @Override
    protected void onDestroy() {
        if (alertDialog!=null)
        {
            alertDialog.dismiss();
        }
        super.onDestroy();
        disposable.clear();
    }

    @Override
    protected void onStop() {
        if (alertDialog!=null)
        {
            alertDialog.dismiss();
        }
        super.onStop();
    }

    public void setupTypePriceRecycler(){
        recycler_type_price.setLayoutManager(new LinearLayoutManager(this));
        typeMinPriceAdapter=new TypeMinPriceAdaptern(stringList);
        recycler_type_price.setAdapter(typeMinPriceAdapter);

    }





}
