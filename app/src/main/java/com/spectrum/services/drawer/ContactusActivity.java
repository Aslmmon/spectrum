package com.spectrum.services.drawer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.R;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ContactusActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_cus_email)
    EditText user_email;
    @BindView(R.id.et_cus_name)
    EditText user_name;
    @BindView(R.id.et_cus_phone)
    EditText user_phone;
    @BindView(R.id.et_cus_message)
    EditText message;
    @BindView(R.id.tv_cus_spectrum_address)
    TextView spectrum_address;
    @BindView(R.id.tv_cus_booking_no_one)
    TextView spectrum_booking_no_one;
    @BindView(R.id.tv_cus_booking_no_two)
    TextView spectrum_booking_no_two;
    @BindView(R.id.tv_cus_spectrum_email)
    TextView spectrum_email;
    @BindView(R.id.tv_cus_pobox)
    TextView spectrum_pobox;
    @BindView(R.id.tv_cus_spectrum_website)
    TextView spectrum_website;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    Prefs prefs;
    UserModel.User user;
    CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        ButterKnife.bind(this);
        setupToolbar();
        initSome();
    }

    private void initSome() {
        prefs = Prefs.with(this);

        user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
        if (user != null) {
            user_email.setText(user.getEmail());
            user_name.setText(user.getName());
            user_phone.setText(user.getPhone());

        }

        spectrum_booking_no_one.setText(getResources().getString(R.string.cus_spectrum_booking_no_one));
        spectrum_booking_no_two.setText(getResources().getString(R.string.cus_spectrum_booking_no_two));
        spectrum_email.setText(getResources().getString(R.string.cus_spectrum_mail));
        spectrum_pobox.setText(getResources().getString(R.string.cus_spectrum_pobo));
        spectrum_website.setText(getResources().getString(R.string.cus_spectrum_website));


    }

    private void setupToolbar() {
        toolbar.setTitle("Contact us");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);


    }

    private void contactusApi() {
        if (validate()) {
            progressBar.setVisibility(View.VISIBLE);
            JsonObject object = new JsonObject();
            object.addProperty("name", user_name.getText().toString());
            object.addProperty("phone", user_phone.getText().toString());
            object.addProperty("email", user_email.getText().toString());
            object.addProperty("message", message.getText().toString());

            disposable.add(Utils.getApiService().contactUs(object)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(userModel -> userModel)
                    .subscribe(this::handleContactusResponce, throwable -> {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();

                    }));


        }

    }

    private void handleContactusResponce(UserModel model) {
        progressBar.setVisibility(View.GONE);
        if (model.getStatus().equalsIgnoreCase("success")) {
            Snackbar.make(progressBar, model.getMessage(), Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(progressBar, model.getMessage(), Snackbar.LENGTH_SHORT).show();

        }

    }

    private Boolean validate() {
        if (user_name.getText().toString().isEmpty()) {
            Snackbar.make(progressBar, "Enter your name", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (user_phone.getText().length() < 10) {
            Snackbar.make(progressBar, "Enter valid phone number", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.emailValidate(user_email.getText())) {
            Snackbar.make(progressBar, "Enter valid email", Snackbar.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }

    @OnClick(R.id.relative_submit_click)
    public void submitClick(View view) {
        contactusApi();
    }


    @OnClick(R.id.tv_cus_booking_no_one)
    public void bookingNoOne(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: 800 7274"));
        startActivity(intent);
    }

    @OnClick(R.id.tv_cus_booking_no_two)
    public void bookingNoTwo(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: 04 431 0113"));
        startActivity(intent);

    }

    @OnClick(R.id.tv_cus_spectrum_email)
    public void emailClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto: info@spectrumservices.ae"));
        startActivity(intent);
    }

    @OnClick(R.id.tv_cus_spectrum_website)
    public void websiteClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https:www.spectrumservices.ae"));
        startActivity(intent);

    }

    @OnClick(R.id.iv_cus_fb)
    public void fbClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.facebook.com/spectrumservices.ae/"));
        startActivity(intent);
    }

    @OnClick(R.id.iv_cus_twitter)
    public void twitterClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://twitter.com/Spectrumservic"));
        startActivity(intent);

    }

    @OnClick(R.id.iv_cus_linkedin)
    public void linkedinClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.linkedin.com/in/spectrum-services-467b79155/"));
        startActivity(intent);

    }

    @OnClick(R.id.iv_cus_google)
    public void googleClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://plus.google.com/u/0/108039587008784345822"));
        startActivity(intent);

    }

    @OnClick(R.id.iv_cus_instagram)
    public void instagramClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.instagram.com/spectrum_services/"));
        startActivity(intent);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
