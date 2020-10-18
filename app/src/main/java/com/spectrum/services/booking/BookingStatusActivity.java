package com.spectrum.services.booking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import com.spectrum.services.R;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BookingStatusActivity extends AppCompatActivity {
    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_ref_id)
    TextView ref_id;
    @BindView(R.id.iv_status_smiley)
    ImageView status_smiley;
    @BindView(R.id.tv_thankyou)
    TextView status_title;
    @BindView(R.id.tv_instrn_details)
    TextView status_instrns;

    @BindView(R.id.tv_booking_total_amount)
    TextView total_charge;
    @BindView(R.id.tv_vat_label)
    TextView vat_label;
    @BindView(R.id.relative_main_total)
    RelativeLayout relative_main_total;
    @BindView(R.id.relative_payment_click)
    RelativeLayout pay_click;
    @BindView(R.id.tv_pay)
    TextView tv_pay;


    Boolean is_clean, is_proceed_pay = false;
    //    CleanBookingResponceModel responceModel;
//    PriceResponseModel priceResponseModel;
    String from = "";
    PayViewModel viewModel;

    String reference, vat_perc, amount;


    CompositeDisposable disposable = new CompositeDisposable();

    Prefs prefs;
    UserModel.User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);
        ButterKnife.bind(this);
        initSome();
        busLIstener();

    }

    private void initSome() {
        prefs = Prefs.with(this);

//        responceModel = Utils.convertStringToModel(getIntent().getStringExtra("paymentstatus"), CleanBookingResponceModel.class);
//        priceResponseModel = Utils.convertStringToModel(getIntent().getStringExtra("paydetails"), PriceResponseModel.class);

        reference = getIntent().getStringExtra("ref");
        amount = getIntent().getStringExtra("amount");
        vat_perc = getIntent().getStringExtra("vat");

        Log.e(TAG, "initSome: " + amount + vat_perc);

        from = getIntent().getStringExtra("type");
        initViews(from);
        initToolbar();
        setData();
        user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);


    }


    private void initToolbar() {
        toolbar.setTitle("Booking Status");

        if (from.equalsIgnoreCase("clean")) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
            toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setNavigationIcon(R.drawable.ic_close_blue_24dp);
        }

        setSupportActionBar(toolbar);


    }


    private void setData() {


        if (reference != null) {
            ref_id.setText("Booking Ref id: " + reference);
            status_smiley.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_sentiment_satisfied_black_24dp));
            status_title.setText(getResources().getString(R.string.status_thankyou));
            status_instrns.setText(getResources().getString(R.string.status_details));


        } else {
            status_smiley.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_sentiment_dissatisfied_24dp));
            status_title.setText("Sorry.Pleae try later");
            ref_id.setVisibility(View.GONE);
            pay_click.setVisibility(View.GONE);
            status_instrns.setVisibility(View.GONE);
        }


    }

    private void initViews(String from) {

        if (from.equalsIgnoreCase("clean")) {
            is_clean = false;

            ref_id.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            relative_main_total.setVisibility(View.VISIBLE);
            pay_click.setBackground(getResources().getDrawable(R.drawable.rect_bg_green));
            tv_pay.setText("Home");
            status_instrns.setVisibility(View.VISIBLE);
            if (amount != null && vat_perc != null) {
                total_charge.setText("AED " + amount);
                vat_label.setText("(Inclusive of " + vat_perc + "% VAT)");

            }


        } else {
            is_clean = false;

            ref_id.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            relative_main_total.setVisibility(View.GONE);
            pay_click.setBackground(getResources().getDrawable(R.drawable.button_bg_blue));
            tv_pay.setText("Home");
            status_instrns.setVisibility(View.GONE);


        }

    }

    @OnClick(R.id.relative_payment_click)
    public void makePaymentClick(View view) {
        if (is_clean) {

//            Intent intent=new Intent(BookingStatusActivity.this, PaymentActivity.class);
//            intent.putExtra("amount",amount);
//            intent.putExtra("refid",reference);
//            startActivity(intent);
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        } else {
            //maint home click
            onBackPressed();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }


    @Override
    public void onBackPressed() {
        if (is_clean) {
            ((MyApplication) getApplication()).bus().send("kill");
        } else {
            ((MyApplication) getApplication()).bus().send("killmaint");
            ((MyApplication) getApplication()).bus().send("kill");
        }

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void busLIstener() {
        disposable.add(((MyApplication) getApplication()).bus()
                .toObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof String) {
                        if (((String) o).equalsIgnoreCase("kill")) {
                            this.finish();
                        }
                    }

                }));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
