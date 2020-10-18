package com.spectrum.services.profile.phone_verify;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.spectrum.services.R;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.profile.ProfileActivity;
import com.spectrum.services.profile.ProfileViewModel;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PhoneVerifyActivity extends AppCompatActivity implements Observer {
    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_otp_one)
    EditText otp_one;
    @BindView(R.id.et_otp_two)
    EditText otp_two;
    @BindView(R.id.et_otp_three)
    EditText otp_three;
    @BindView(R.id.et_otp_four)
    EditText otp_four;
    @BindView(R.id.et_otp_five)
    EditText otp_five;
    @BindView(R.id.tv_user_phone)
    TextView user_phone;
    @BindView(R.id.tv_resend_counter)
    TextView counter_text;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    UserModel.User user;
    Prefs prefs;
    CompositeDisposable disposable = new CompositeDisposable();
    Boolean is_from = false,is_counter=true;
    String from;
    ProfileViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        ButterKnife.bind(this);
        setupToolbar();
        initSome();
        setupEdittext();
    }

    private void initSome() {
        prefs = Prefs.with(this);



        user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
        from = getIntent().getStringExtra("from");
        if (from!=null && from.equalsIgnoreCase("reviewconfirm")) {
            is_from = true;
        }

        if (user.getPhone() != null) {
            user_phone.setText(user.getPhone());

        }
//        requestPermission();
//        SmsReceiver.bindListener(messageText -> {
//
//            Log.e(TAG, "initSome: "+messageText.charAt(0) );
//
//            if (messageText != null && !messageText.equalsIgnoreCase("")) {
//                otp_one.setText(""+messageText.charAt(0));
//                otp_two.setText(""+messageText.charAt(1));
//                otp_three.setText(""+messageText.charAt(2));
//                otp_four.setText(""+messageText.charAt(3));
//                otp_five.setText(""+messageText.charAt(4));
//
//            }
//
//        });
        counter();


    }

    private void counter() {
        sendRequestforSms();
        is_counter=false;
        disposable.add(io.reactivex.Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .take(30)
                .map(aLong -> 29 - aLong)
                .doOnComplete(() -> {
                    counter_text.setVisibility(View.GONE);
                    counter_text.setText("");
                    is_counter=true;
                })
                .subscribe(aLong -> {
                    counter_text.setText("in " + aLong.toString() + " seconds");
                }));

    }

    private void sendRequestforSms() {
        if (user != null) {
            PhoneVerifyModel model = new PhoneVerifyModel(user.getPhone(), prefs.getUserid());
//            JsonObject object=new JsonObject();
//            object.addProperty("customer_id",prefs.getUserid());
//            object.addProperty("phone",user.getPhone());

            disposable.add(Utils.getApiService().requestSMS(model)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(phoneVerifyModel -> phoneVerifyModel)
                    .subscribe(phoneVerifyModel -> {
                        Log.e(TAG, "sendRequestforSms: "+phoneVerifyModel.getStatus());
                        Snackbar.make(progressBar, phoneVerifyModel.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }, throwable -> {
                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", v -> {
                                    sendRequestforSms();
                                })
                                .show();
                    }));

        }

    }


    private void setupEdittext() {
        otp_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    otp_two.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otp_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    otp_three.requestFocus();
                } else {
                    otp_one.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otp_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    otp_four.requestFocus();
                } else {
                    otp_two.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        otp_four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    otp_five.requestFocus();

                } else {
                    otp_three.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        otp_five.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                } else {
                    otp_four.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(PhoneVerifyActivity.this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(PhoneVerifyActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    0);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void setupToolbar() {
        toolbar.setTitle("");
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
        if (!is_from)
        {
            finish();
            Intent intent = new Intent(PhoneVerifyActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }
        else
        {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            super.onBackPressed();

        }

    }

    @OnClick(R.id.relative_verify_submit_click)
    public void verifySubmit(View view) {
        otpSubmit();

    }

    @OnClick(R.id.relative_verify_retry_click)
    public void verifyRetry(View view) {
        if (is_counter)
        {
            counter_text.setVisibility(View.VISIBLE);
            counter();

        }


    }

    @Override
    public void update(Observable o, Object arg) {

    }

    private void otpSubmit() {

        if (!validdateOtp()) {
            progressBar.setVisibility(View.VISIBLE);
            Log.e(TAG, "otpSubmit: " + getEnteredotp());
            JsonObject object = new JsonObject();
            object.addProperty("customer_id", prefs.getUserid());
            object.addProperty("otp", getEnteredotp());
            disposable.add(Utils.getApiService().submitOTP(object)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(phoneVerifyModel -> phoneVerifyModel)
                    .subscribe(this::handleResponce, throwable -> {
                        progressBar.setVisibility(View.GONE);

                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();
                    }));

        } else {
            Snackbar.make(progressBar, "Enter otp", Snackbar.LENGTH_SHORT).show();

        }
    }
    private void handleResponce(PhoneVerifyModel model) {
        progressBar.setVisibility(View.GONE);
        if (model.getStatus().equalsIgnoreCase("success")) {
            getupdatedProfile();


        } else {
            Snackbar.make(progressBar, model.getMessage(), Snackbar.LENGTH_SHORT).show();

        }
    }

    private void getupdatedProfile()
    {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(Utils.getApiService().getUserDetails(setupDataToSend(prefs.getUserid()))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(userModel ->  userModel)
        .subscribe(this::handleUserData,throwable -> {
            Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT)
                    .setAction("Retry",v -> {
                        getupdatedProfile();
                    })
                    .show();

        }));
    }
    public JsonObject setupDataToSend(String uid) {
        JsonObject object = new JsonObject();
        object.addProperty("user_id", uid);
        return object;
    }
    private void handleUserData(UserModel userModel)
    {
        progressBar.setVisibility(View.GONE);
        if (userModel.getStatus().equalsIgnoreCase("success"))
        {
            prefs.setUserDetails(Utils.convertmodelToString(userModel.getUser()));
            prefs.setNumberVerified("true");
            alertDialog("Your phone is verified successfully");

        }
    }

    private void alertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhoneVerifyActivity.this);
        builder.setTitle("Alert");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setCancelable(false)
                .setPositiveButton("ok", (dialog, which) -> {
                    dialog.dismiss();
                    onBackPressed();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private Boolean validdateOtp() {
        return otp_one.getText().toString().isEmpty() && otp_two.getText().toString().isEmpty() &&
                otp_three.getText().toString().isEmpty() && otp_four.getText().toString().isEmpty() && otp_five.getText().toString().isEmpty();
    }

    private String getEnteredotp() {
        return otp_one.getText().toString() + otp_two.getText().toString() + otp_three.getText().toString() +
                otp_four.getText().toString() + otp_five.getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
