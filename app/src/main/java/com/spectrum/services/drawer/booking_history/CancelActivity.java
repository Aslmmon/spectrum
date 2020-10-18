package com.spectrum.services.drawer.booking_history;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.spectrum.services.R;
import com.spectrum.services.models.CommonResponse;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelActivity extends AppCompatActivity {

    Prefs prefs;
    TextView cancelBooking;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);
        cancelBooking = findViewById(R.id.cancelBooking);
        checkBox = findViewById(R.id.checkBox);

        setupToolbar();
        prefs = Prefs.with(this);

        cancelBooking.setOnClickListener(v -> {

            if (checkBox.isChecked()) {

                cancelBooking();


            } else {
                Snackbar.make(v, "Please agree the terms and conditions", Snackbar.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return super.onSupportNavigateUp();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Booking History");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
    }

    private void cancelBooking() {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait..");
        dialog.show();

        JsonObject object = new JsonObject();
        object.addProperty("reference_id", getIntent().getStringExtra("booking_id"));
        object.addProperty("customer_id", prefs.getUserid());
        object.addProperty("cancel", "yes");


        Utils.getApiService().cancelBooking(object)
                .enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {

                        Log.e("Response ", new GsonBuilder().setPrettyPrinting().create().toJson(response.body()));


                        if (response.isSuccessful()) {

                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                ((MyApplication) getApplication()).bus().send("cancel_success");
                                finish();
                            }

                        } else {

                            Toast.makeText(CancelActivity.this,"Please, try again later.",Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();

                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {

                        Log.e("bookinghistoryactivity", "onFailure: " + t.getMessage());
                        Snackbar.make(cancelBooking, "Check your connection", Snackbar.LENGTH_LONG)
                                .setAction("Retry", view -> cancelBooking())
                                .show();

                        dialog.dismiss();

                    }
                });
    }
}
