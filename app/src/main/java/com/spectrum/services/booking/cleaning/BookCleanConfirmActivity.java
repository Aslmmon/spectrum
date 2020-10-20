package com.spectrum.services.booking.cleaning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.BaseActivity;
import com.spectrum.services.R;
import com.spectrum.services.auth.LoginActivity;
import com.spectrum.services.booking.BookingStatusActivity;
import com.spectrum.services.frags.PaymentOptionFrag;
import com.spectrum.services.models.CleanBookingRequestModel;
import com.spectrum.services.models.CleanBookingResponceModel;
import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.models.MainCleanBookModel;
import com.spectrum.services.models.PriceRequestModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.profile.phone_verify.PhoneVerifyActivity;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;


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

import static com.spectrum.services.utils.Shared.cleaningBookModelList;

public class BookCleanConfirmActivity extends BaseActivity {
    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear_card_container)
    LinearLayout linera_card_container;
    @BindView(R.id.booking_nested_scroll)
    NestedScrollView booking_nested_scroll;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    @BindView(R.id.relative_bsummary_content)
    RelativeLayout bsummary_content;
    @BindView(R.id.tv_booking_type)
    TextView booking_type;
    @BindView(R.id.tv_booking_hours)
    TextView booking_hour;
    @BindView(R.id.tv_booking_maids)
    TextView booking_maid;
    @BindView(R.id.tv_booking_type_service)
    TextView booking_type_service;
    @BindView(R.id.tv_booking_date_time)
    TextView booking_date_time;
    @BindView(R.id.relative_bsummary_title)
    RelativeLayout bsummary_title;
    @BindView(R.id.relative_bsummary_container)
    RelativeLayout bsummary_container;

    @BindView(R.id.relative_badditional_content)
    RelativeLayout badditional_content;
    @BindView(R.id.tv_booking_extra_name)
    TextView booking_extra;
    @BindView(R.id.tv_booking_material)
    TextView booking_material;
    @BindView(R.id.tv_booking_crewin)
    TextView booking_crewin;

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

    @BindView(R.id.et_promocode)
    EditText promocode;
    @BindView(R.id.tv_booking_promo_amount)
    TextView promocode_amount;
    @BindView(R.id.tv_booking_total_amount)
    TextView booking_total_amount;
    @BindView(R.id.tv_booking_vat_amount)
    TextView booking_vat_amount;
    @BindView(R.id.tv_vat_label)
    TextView booking_vat_label;
    @BindView(R.id.tv_booking_charge_per)
    TextView booking_charge_per;
    @BindView(R.id.tv_booking_service_amount)
    TextView booking_service_amount;
    @BindView(R.id.progress_bar_price)
    ProgressBar progress_bar_price;

    @BindView(R.id.iv_expandable_bsummaryclick)
    ImageView bsummary_exp;
    @BindView(R.id.iv_expandable_badditionalclick)
    ImageView badditional_exp;
    @BindView(R.id.iv_expandable_contactclick)
    ImageView bcontact_exp;


    private MainCleanBookModel mainCleanBookModel;
    private Prefs prefs;
    private Boolean first = true;
    private UserModel.User userModel;
    private PriceResponseModel priceData;

    private CompositeDisposable disposable = new CompositeDisposable();
    private BookCleanViewModel viewModel;

    private Boolean is_promo_applied = false;
    private String applied_coupen_val = "";


    private final String VAT = "VAT(5%)";
    private final String SERVICE_RATE = "(AED 50/hr)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_clean_confirm);
        ButterKnife.bind(this);
        initToolbar();
        initSome();
        actionlisteners();
        //logpurchase();
    }

    private void initSome() {
        viewModel = ViewModelProviders.of(this).get(BookCleanViewModel.class);

        prefs = Prefs.with(this);
        setData();

        progressBar.setVisibility(View.GONE);

    }

    private void setData() {
        mainCleanBookModel = Utils.convertStringToModel(getIntent().getStringExtra("basicData"), MainCleanBookModel.class);
        priceData = Utils.convertStringToModel(getIntent().getStringExtra("priceData"), PriceResponseModel.class);


        //bsummary stuff
        bsummary_content.setVisibility(View.VISIBLE);
        bsummary_content.setActivated(true);
        booking_type.setText("Cleaning Schedule");
        booking_hour.setText(mainCleanBookModel.getNo_hours());
        booking_maid.setText(mainCleanBookModel.getNo_maids());
        booking_type_service.setText(mainCleanBookModel.getType_service());

        Log.e("message", "setData: " + Shared.cleaningBookModelList.size());
        for (int i = 0; i < Shared.cleaningBookModelList.size(); i++) {
            booking_date_time.append(Utils.getDateFormat(Shared.cleaningBookModelList.get(i).getDate()) + " " + Shared.cleaningBookModelList.get(i).getTime() + "\n");

        }

        //badditioanl stuff
        badditional_content.setVisibility(View.GONE);
        badditional_content.setActivated(false);
        badditional_exp.setRotation(180);

        if (mainCleanBookModel.getCleanExtraServices().size() < 1) {
            booking_extra.setText("Not opted");
        } else {
            for (CleanExtraService.ExtraServiceData data : mainCleanBookModel.getCleanExtraServices()) {
                if (first) {
                    first = false;
                    booking_extra.append(data.getExtra_service_name());

                } else {
                    booking_extra.append("\n");
                    booking_extra.append(data.getExtra_service_name());

                }


            }

        }


        booking_material.setText(mainCleanBookModel.getWant_materials().equalsIgnoreCase("true") ? "Yes" : "No");
        booking_crewin.setText(mainCleanBookModel.getCrew_in());

        //price stuff
//        booking_total_amount.setText("AED "+priceData.getGross_amount());
//        booking_service_amount.setText("AED "+priceData.getService_rate());
//        booking_vat_amount.setText("AED "+priceData.getVat_charge());
//        booking_charge_per.setText("AED "+priceData.getHour_rate()+"/HR");
//        booking_vat_label.setText("VAT "+priceData.getVat_percentage()+"%");


        if (priceData.getCoupen_status().equalsIgnoreCase("applied")) {
            if (priceData.getPrice().getCoupen_val() != null && !priceData.getPrice().getCoupen_val().equalsIgnoreCase("")) {
                promocode.setText(priceData.getPrice().getCoupen_val());
                is_promo_applied = true;
                applied_coupen_val = priceData.getPrice().getCoupen_val();
            }

        }


        priceCalcApi();
        updateProfile();


    }

    private void updateProfile() {
        //contact stuff
        contact_content.setVisibility(View.VISIBLE);
        contact_content.setActivated(true);
        if (prefs.getSignedIn()) {
            userModel = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
            user_name.setText(userModel.getName());
            user_email.setText(userModel.getEmail());
            user_phone.setText(userModel.getPhone());
            user_building.setText(userModel.getBuilding());
            user_unit.setText(userModel.getUnit());
            user_street.setText(userModel.getStreet());
            if (userModel.getArea_name() != null) {
                if (userModel.getArea_name().equalsIgnoreCase("Other")) {
                    user_area.setText(userModel.getOther_area());
                } else {
                    user_area.setText(userModel.getArea_name());
                }

            }

        }

    }

    private void initToolbar() {

        toolbar.setTitle("Review and Confirm");
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
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }

    @OnClick({R.id.iv_expandable_bsummaryclick, R.id.relative_bsummary_title})
    public void bSummaryExpandable(View view) {
        if (bsummary_content.isActivated()) {

            bsummary_exp.setRotation(180);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(linera_card_container, new android.transition.AutoTransition());
            }
            bsummary_content.setVisibility(View.GONE);
            bsummary_content.setActivated(false);
        } else {
            bsummary_exp.setRotation(360);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(linera_card_container, new android.transition.AutoTransition());
            }
            bsummary_content.setVisibility(View.VISIBLE);
            bsummary_content.setActivated(true);
        }

    }


    @OnClick({R.id.iv_expandable_badditionalclick, R.id.relative_badditional_title})
    public void bAdditionalExpandable(View view) {
        if (badditional_content.isActivated()) {

            badditional_exp.setRotation(180);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(linera_card_container, new android.transition.AutoTransition());
            }
            badditional_content.setActivated(false);
            badditional_content.setVisibility(View.GONE);
        } else {
            badditional_exp.setRotation(360);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(linera_card_container, new android.transition.AutoTransition());
            }
            badditional_content.setActivated(true);
            badditional_content.setVisibility(View.VISIBLE);

        }
    }

    @OnClick({R.id.iv_expandable_contactclick, R.id.relative_contact_title})
    public void contactExpandable(View view) {
        if (contact_content.isActivated()) {

            bcontact_exp.setRotation(180);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(linera_card_container, new android.transition.AutoTransition());
            }
            contact_content.setActivated(false);
            contact_content.setVisibility(View.GONE);
        } else {
            bcontact_exp.setRotation(360);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(linera_card_container, new android.transition.AutoTransition());
            }
            contact_content.setActivated(true);
            contact_content.setVisibility(View.VISIBLE);
            if (bsummary_content.isActivated() || badditional_content.isActivated()) {
                booking_nested_scroll.requestChildFocus(view, contact_content);
            }


        }
    }

    @OnClick(R.id.promo_apply_click)
    public void promoApply(View view) {

    }

    @OnClick(R.id.booking_confirm_click)
    public void confirmClick(View view) {
        if (validUserData()) {
            PaymentOptionFrag frag = PaymentOptionFrag.initData(Utils.convertmodelToString(setupDataForBooking()), Utils.convertmodelToString(priceData));
            frag.show(getSupportFragmentManager(), "");

//            progressBar.setVisibility(View.VISIBLE);
//            disposable.add(viewModel.bookClean(setupDataForBooking())
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(this::handleBookingResponce,throwable -> {
//                progressBar.setVisibility(View.GONE);
//                 Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();
//
//            }));


        }

    }

    private void handleBookingResponce(CleanBookingResponceModel model) {
        progressBar.setVisibility(View.GONE);
        if (model.getStatus() != null) {
            Intent intent = new Intent(BookCleanConfirmActivity.this, BookingStatusActivity.class);
            intent.putExtra("paymentstatus", Utils.convertmodelToString(model));
            intent.putExtra("paydetails", Utils.convertmodelToString(priceData));
            intent.putExtra("type", "clean");
            startActivity(intent);
        } else {
            Snackbar.make(progressBar, model.getMessage(), Snackbar.LENGTH_SHORT).show();
        }

    }

    private Boolean validUserData() {
        if (priceData == null) {
            Snackbar.make(progressBar, "Price Calculating..please wait", Snackbar.LENGTH_SHORT).show();
            priceCalcApi();
            return false;
        }

        if (prefs.getSignedIn()) {
            if (!Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class).getIs_verified()) {

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

    private CleanBookingRequestModel setupDataForBooking() {
        List<String> extra_services = new ArrayList<>();
        for (CleanExtraService.ExtraServiceData data : mainCleanBookModel.getCleanExtraServices()) {
            extra_services.add(data.getExtra_service_id());

        }
        CleanBookingRequestModel model = new CleanBookingRequestModel(prefs.getUserid(), mainCleanBookModel.getType_service_id(),
                mainCleanBookModel.getInstruction().equalsIgnoreCase("") ? "" : mainCleanBookModel.getInstruction(),
                mainCleanBookModel.getNo_maids(), mainCleanBookModel.getNo_hours(), mainCleanBookModel.getWant_materials().equalsIgnoreCase("true") ? "Y" : "N",
                priceData.getPrice().getHour_rate(), priceData.getPrice().getService_rate(), priceData.getPrice().getVat_charge(),
                priceData.getPrice().getGross_amount(), mainCleanBookModel.getCrew_in(),
                priceData.getPrice().getCoupen_id(), priceData.getPrice().getCoupen_val(), priceData.getPrice().getDiscount(),
                Shared.cleaningBookModelList, extra_services, "card");
        return model;
    }

    private void priceCalcApi() {
        priceData = null;
        progress_bar_price.setVisibility(View.VISIBLE);
        booking_total_amount.setVisibility(View.INVISIBLE);
        booking_vat_amount.setVisibility(View.INVISIBLE);
        booking_service_amount.setVisibility(View.INVISIBLE);
        promocode_amount.setVisibility(View.INVISIBLE);
        disposable.add(viewModel.getPriceDetailApi(setupPriceRequestModel())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(priceResponseModel -> priceResponseModel)
                .subscribe(this::handlePriceResponce, throwable -> {
                    progress_bar_price.setVisibility(View.GONE);
                    booking_total_amount.setVisibility(View.INVISIBLE);
                    booking_vat_amount.setVisibility(View.INVISIBLE);
                    promocode_amount.setVisibility(View.INVISIBLE);
                    Snackbar.make(progress_bar_price, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", view -> priceCalcApi())
                            .show();

                }));
    }

    private PriceRequestModel setupPriceRequestModel() {
        ArrayList<String> extraServiceId = new ArrayList<>();
        for (CleanExtraService.ExtraServiceData data : mainCleanBookModel.getCleanExtraServices()) {
            extraServiceId.add(data.getExtra_service_id());
        }
        int size;
        if (cleaningBookModelList.size() < 1) {
            size = 1;
        } else {
            size = cleaningBookModelList.size();
        }

        PriceRequestModel model = new PriceRequestModel(prefs.getUserid(), mainCleanBookModel.getNo_hours(), mainCleanBookModel.getNo_maids(),
                mainCleanBookModel.getWant_materials().equalsIgnoreCase("true") ? "Y" : "N", mainCleanBookModel.getType_service_id(), "" + size,
                promocode.getText().toString().isEmpty() ? "" : promocode.getText().toString(), extraServiceId, cleaningBookModelList.size() > 0 ? cleaningBookModelList.get(0).getDate() : getDayfromDate(Calendar.getInstance().getTime()));
        return model;
    }

    private String getDayfromDate(Date date) {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");
        String date_str = sformat.format(date);
        return date_str;
    }

    private void handlePriceResponce(PriceResponseModel model) {
        if (model.getStatus().equalsIgnoreCase("success")) {
            priceData = model;
            progress_bar_price.setVisibility(View.GONE);
            booking_total_amount.setVisibility(View.VISIBLE);
            booking_service_amount.setVisibility(View.VISIBLE);
            booking_vat_amount.setVisibility(View.VISIBLE);
            promocode_amount.setVisibility(View.VISIBLE);


            Log.e(TAG, "handlePriceResponce: " + model.getPrice().getGross_amount());
            booking_vat_label.setText("VAT " + model.getPrice().getVat_percentage() + "%");
            booking_service_amount.setText("AED " + model.getPrice().getService_rate());
            booking_charge_per.setText("AED " + model.getPrice().getHour_rate() + " /HR");
            booking_vat_amount.setText("AED " + model.getPrice().getVat_charge());
            booking_total_amount.setText("AED " + priceData.getPrice().getGross_amount());
            promocode_amount.setText("- AED " + model.getPrice().getDiscount());

            //promocode conditions

            if (!promocode.getText().toString().isEmpty()) {


                if (!is_promo_applied) {
                    if (model.getCoupen_status().equalsIgnoreCase("applied") && model.getPrice().getCoupen_val().equals(promocode.getText().toString())) {
                        is_promo_applied = true;
                        applied_coupen_val = promocode.getText().toString();

                        Snackbar.make(progress_bar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();
                    } else if (model.getCoupen_status().equalsIgnoreCase("not")) {
                        Log.e(TAG, "handlePriceResponce:in not ");
                        is_promo_applied = false;
                        applied_coupen_val = "";
                        promocode.setText("");
                        Snackbar.make(progress_bar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }


                } else if (model.getCoupen_status().equalsIgnoreCase("applied") && !applied_coupen_val.equalsIgnoreCase(model.getPrice().getCoupen_val())) {
                    is_promo_applied = true;
                    applied_coupen_val = promocode.getText().toString();
                    Snackbar.make(progress_bar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();
                } else if (model.getCoupen_status().equalsIgnoreCase("not")) {
                    Log.e(TAG, "handlePriceResponce:in not ");
                    is_promo_applied = false;
                    applied_coupen_val = "";
                    promocode.setText("");
                    Snackbar.make(progress_bar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();

                }


            } else if (model.getCoupen_status().equalsIgnoreCase("applied")) {
                is_promo_applied = true;
                applied_coupen_val = model.getPrice().getCoupen_val();
                promocode.setText(applied_coupen_val);
                Snackbar.make(progress_bar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();

            }
        }

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

    @SuppressLint("CheckResult")
    private void actionlisteners() {
        ((MyApplication) getApplication())
                .bus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof String) {
                        if (((String) o).equalsIgnoreCase("reviewconfirm")) {
                            updateProfile();
                        }
                        if (((String) o).equalsIgnoreCase("kill")) {
                            this.finish();
                        }
                    }

                });


    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookCleanConfirmActivity.this);
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
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
