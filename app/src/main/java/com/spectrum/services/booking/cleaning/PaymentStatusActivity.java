package com.spectrum.services.booking.cleaning;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.spectrum.services.R;
import com.spectrum.services.booking.BookingStatusActivity;
import com.spectrum.services.booking.PaymentActivity;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PaymentStatusActivity extends AppCompatActivity {
    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_order)
    TextView order_id;
    @BindView(R.id.tv_amount)
    TextView amount;
    @BindView(R.id.tv_fortid)
    TextView fortid;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.relative_bottom_button)
    RelativeLayout bottom_button;
    @BindView(R.id.tv_bottom)
    TextView bottom_textview;

    @BindView(R.id.relative_cash_payment)
    RelativeLayout cash_pay;


    Prefs prefs;
    UserModel.User user;
    Boolean is_pay_success = false, close = false,is_card_pay=true;

    String str_amount, str_refid, vat_perc,str_traid;
    String is_cash_retry="N";

    BookCleanViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        setupToolbar();
        initSome();
        // initPaymentSdk();
    }

    private void initSome() {
        prefs = Prefs.with(this);
        user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);

        viewModel= ViewModelProviders.of(this).get(BookCleanViewModel.class);

        str_amount = getIntent().getStringExtra("amount");
        str_refid = getIntent().getStringExtra("ref");
        vat_perc = getIntent().getStringExtra("vat");
        str_traid=getIntent().getStringExtra("fort_id");

        //paystatus
        is_pay_success = getIntent().getBooleanExtra("paystatus", false);

        if (is_pay_success) {

            bottom_textview.setText("Home");

            animationView.setAnimation(R.raw.success);
            animationView.playAnimation();

            tv_status.setText("Success");
            amount.setText("AED " + str_amount);
            order_id.setText(str_refid);
            fortid.setText(getIntent().getStringExtra("fort_id"));

            cash_pay.setVisibility(View.GONE);


        } else {
            bottom_textview.setText("Retry Payment");

            amount.setText("AED " + getIntent().getStringExtra("amount"));
            order_id.setText(getIntent().getStringExtra("ref"));
            tv_status.setText(getIntent().getStringExtra("fail_message"));
            fortid.setText("Not available");

            animationView.setAnimation(R.raw.error);
            animationView.playAnimation();

            cash_pay.setVisibility(View.VISIBLE);



        }

        apiCallToInformPayStatus();



    }
    @SuppressLint("CheckResult")
    private void apiCallToInformPayStatus()
    {

        viewModel.informPayStatus(setupInformPayData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userModel -> {
                },throwable -> {
                    Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();

                });

    }

    private JsonObject setupInformPayData()
    {
        JsonObject object=new JsonObject();
        object.addProperty("customer_id",prefs.getUserid());
        object.addProperty("booking_refid",str_refid);
        object.addProperty("payment_method",is_card_pay?"card":"cash");
        object.addProperty("transaction_id",str_traid);
        object.addProperty("payment_status",is_pay_success?"success":"failed");
        object.addProperty("amount",str_amount);
        object.addProperty("is_from_retry",is_cash_retry);
        return object;

    }

    @OnClick(R.id.relative_bottom_button)
    public void bottmButtonClick(View view) {
        if (is_pay_success) {
            onBackPressed();

        } else {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("amount", str_amount);
            intent.putExtra("ref", str_refid);
            intent.putExtra("vat", vat_perc);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            ((MyApplication) getApplication()).bus().send("killpay");
            this.finish();

        }
    }

    @OnClick(R.id.relative_cash_payment)
    public void cashPay() {

        is_card_pay=false;
        is_cash_retry="Y";
        apiCallToInformPayStatus();

        Intent intent = new Intent(PaymentStatusActivity.this, BookingStatusActivity.class);
        intent.putExtra("ref", str_refid);
        intent.putExtra("amount", str_amount);
        intent.putExtra("vat", vat_perc);
        intent.putExtra("type", "clean");


        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finish();


    }


    private void setupToolbar() {
        toolbar.setTitle("Payment");

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }

    @Override
    public void onBackPressed() {
        ((MyApplication) getApplication()).bus().send("kill");
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
