package com.spectrum.services.booking.maintenance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.R;
import com.spectrum.services.booking.BookingStatusActivity;
import com.spectrum.services.models.CleanBookingResponceModel;
import com.spectrum.services.models.MaintBookingModel;
import com.spectrum.services.models.PriceRequestModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.profile.phone_verify.PhoneVerifyActivity;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BookMaintenanceConfirmActivity extends AppCompatActivity {
    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.relative_card_container)
    RelativeLayout relative_card_container;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.progress_bar_price)
    ProgressBar progressbar_price;

    @BindView(R.id.relative_msummary_content)
    RelativeLayout msummary_content;
    @BindView(R.id.tv_selected_service)
    TextView selected_service;
    @BindView(R.id.tv_priority)
    TextView selected_priority;
    @BindView(R.id.tv_selected_date)
    TextView selected_date;
    @BindView(R.id.tv_selected_time)
    TextView selected_time;
    @BindView(R.id.tv_selected_btype)
    TextView booking_type;

    @BindView(R.id.relative_contact_content)
    RelativeLayout contact_content;
    @BindView(R.id.tv_user_name)
    TextView user_name;
    @BindView(R.id.tv_user_email)
    TextView user_email;
    @BindView(R.id.tv_user_phone)
    TextView user_phone;
    @BindView(R.id.tv_user_area)
    TextView user_area;
    @BindView(R.id.tv_user_building)
    TextView user_building;
    @BindView(R.id.tv_user_unit)
    TextView user_unit;
    @BindView(R.id.tv_user_street)
    TextView user_street;

    @BindView(R.id.iv_expandable_msummaryclick)
    ImageView msummary_exp;
    @BindView(R.id.iv_expandable_contactclick)
    ImageView mcontact_exp;

    @BindView(R.id.et_promocode)
    EditText promocode;
    @BindView(R.id.tv_booking_promo_amount)
    TextView promocode_amount;

    private BookMaintenanceViewModel viewModel;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MaintBookingModel bookingModel;
    private UserModel.User user;
    private Boolean is_promo_applied = false;
    private String applied_coupen_val = "", formate_date;
    private PriceResponseModel priceResponseModel;

    private Prefs prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_maintenance_confirm);
        ButterKnife.bind(this);
        initToolbar();
        initSome();
        eventListener();
    }

    private void initSome() {
        viewModel = ViewModelProviders.of(this).get(BookMaintenanceViewModel.class);
        prefs = Prefs.with(this);
        getBookingDetails();


    }

    private void getBookingDetails() {
        bookingModel = Utils.convertStringToModel(getIntent().getStringExtra("bookdata"), MaintBookingModel.class);
        priceResponseModel = Utils.convertStringToModel(getIntent().getStringExtra("price"), PriceResponseModel.class);

        //msummary
        msummary_content.setVisibility(View.VISIBLE);
        msummary_content.setActivated(true);
        booking_type.setText("Maintenance Service");
        selected_service.setText(bookingModel.getService_type_name());
        selected_priority.setText(bookingModel.getPriority());
        formate_date = Utils.getDateFormat(bookingModel.getDate());
        selected_date.setText(formate_date);
        if (bookingModel.getTime_type().equalsIgnoreCase("specific")) {
            selected_time.setText(bookingModel.getTime());
            //selected_time.setVisibility(View.VISIBLE);

        } else {
            selected_time.setText("Flexible time");
            //selected_time.setVisibility(View.GONE);
        }

        if (prefs.getUserDetails() != null) {
            user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
            //contact stuff
            contact_content.setVisibility(View.VISIBLE);
            contact_content.setActivated(true);
            user_name.setText(user.getName());
            user_email.setText(user.getEmail());
            user_phone.setText(user.getPhone());
            if (user.getArea_name() != null) {
                if (user.getArea_name().equalsIgnoreCase("Other")) {
                    user_area.setText(user.getOther_area());
                } else {
                    user_area.setText(user.getArea_name());
                }

            }
            user_building.setText(user.getBuilding());
            user_unit.setText(user.getUnit());
            user_street.setText(user.getStreet());


        }

        if (priceResponseModel != null && priceResponseModel.getCoupen_status().equalsIgnoreCase("applied")) {
            if (priceResponseModel.getPrice().getCoupen_val() != null && !priceResponseModel.getPrice().getCoupen_val().equalsIgnoreCase("")) {
                promocode.setText(priceResponseModel.getPrice().getCoupen_val());
                is_promo_applied = true;
                applied_coupen_val = priceResponseModel.getPrice().getCoupen_val();

            }

        }
        priceCalcApi();


    }

    @SuppressLint("CheckResult")
    private void eventListener() {
        ((MyApplication) getApplication()).bus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof String) {
                        if (((String) o).equalsIgnoreCase("killmaint")) {
                            this.finish();
                        }
                    }

                });
    }


    private void initToolbar() {

        toolbar.setTitle("Review and Confirm");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp_primary);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

    }

    @OnClick(R.id.booking_confirm_click)
    public void confirmBook(View view) {

        if (validatePhone()) {
            bookingModel = setupbookingdata();
            if (bookingModel != null) {
                progressBar.setVisibility(View.VISIBLE);
                disposable.add(viewModel.getMaintBookingApi(bookingModel)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(cleanBookingResponceModel -> cleanBookingResponceModel)
                        .subscribe(this::handleBooking, throwable ->
                        {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();
                        }));
            }

        }


    }

    private Boolean validatePhone() {
        if (prefs.getSignedIn()) {
            if (!Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class).getIs_verified()) {

                alertDialog();
                return false;
            }

        }
        return true;

    }

    private MaintBookingModel setupbookingdata() {


        if (priceResponseModel != null) {
            MaintBookingModel model =
                    new MaintBookingModel(prefs.getUserid(), bookingModel.getService_type_name(), bookingModel.getService_type_id(),
                            bookingModel.getPriority(), bookingModel.getDate(),
                            bookingModel.getTime_type(), bookingModel.getTime(), bookingModel.getInstruction(),
                            priceResponseModel.getPrice().getCoupen_id(), priceResponseModel.getPrice().getDiscount());
            return model;

        }
        return null;

    }

    private void handleBooking(CleanBookingResponceModel model) {
        progressBar.setVisibility(View.GONE);
        if (model.getStatus() != null) {
            Intent intent = new Intent(BookMaintenanceConfirmActivity.this, BookingStatusActivity.class);
            intent.putExtra("ref", model.getReference_id());
            intent.putExtra("type", "maint");
            startActivity(intent);
        } else {
            Snackbar.make(progressBar, model.getMessage(), Snackbar.LENGTH_SHORT).show();
        }

    }


    @OnClick({R.id.iv_expandable_msummaryclick, R.id.relative_msummary_title})
    public void expandSummary(View view) {
        if (msummary_content.isActivated()) {

            msummary_exp.setRotation(180);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(relative_card_container, new AutoTransition());
            }
            msummary_content.setVisibility(View.GONE);
            msummary_content.setActivated(false);
        } else {

            msummary_exp.setRotation(360);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(relative_card_container, new AutoTransition());
            }
            msummary_content.setVisibility(View.VISIBLE);
            msummary_content.setActivated(true);
        }

    }

    @OnClick({R.id.iv_expandable_contactclick, R.id.relative_contact_title})
    public void contactExpandable(View view) {
        if (contact_content.isActivated()) {
            if (view instanceof ImageView) {
                mcontact_exp.setRotation(180);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(relative_card_container, new AutoTransition());
            }
            contact_content.setActivated(false);
            contact_content.setVisibility(View.GONE);
        } else {
            if (view instanceof ImageView) {
                mcontact_exp.setRotation(360);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(relative_card_container, new AutoTransition());
            }
            contact_content.setActivated(true);
            contact_content.setVisibility(View.VISIBLE);
        }
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
                    Snackbar.make(progressbar_price, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", view -> priceCalcApi())
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
        AlertDialog.Builder builder = new AlertDialog.Builder(BookMaintenanceConfirmActivity.this);
        builder.setTitle("Alert");
        builder.setMessage("Verify your phone to complete booking");
        builder.setCancelable(false)
                .setPositiveButton("Verify now", (dialog, which) -> {
                    Intent intent = new Intent(this, PhoneVerifyActivity.class);
                    intent.putExtra("from", "reviewconfirm");
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
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
        super.onDestroy();
        disposable.clear();
    }
}
