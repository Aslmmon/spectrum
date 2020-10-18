package com.spectrum.services.profile;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.spectrum.services.R;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Abins Shaji on 07/02/18.
 */

public class ResetPasswordFrag extends DialogFragment {
    @BindView(R.id.tie_new_pwd)
    TextInputEditText pwd_new;
    @BindView(R.id.tie_old_pwd)
    TextInputEditText pwd_old;
    @BindView(R.id.relative_send_click)
    RelativeLayout relative_send_click;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Prefs prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_reset_pwd, container, false);
        ButterKnife.bind(this, view);
        prefs = Prefs.with(getActivity());
        setCancelable(false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        setCancelable(false);
    }

    @OnClick(R.id.relative_send_click)
    public void emailSend(View view) {
        if (validate(view)) {
            changePwdApi(pwd_old.getText().toString(), pwd_new.getText().toString());

        }


    }

    private boolean validate(View view) {
        if (!Utils.isPasswordValid(pwd_new.getText().toString())) {
            Snackbar.make(view, "Enter valid new password", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (pwd_old.getText().toString().isEmpty()) {
            Snackbar.make(view, "Enter valid old password", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.iv_fpwd_close)
    public void closeDialog(View view) {
        dismiss();
    }

    private void changePwdApi(String old, String newpwd) {
        progressBar.setVisibility(View.VISIBLE);
        relative_send_click.setVisibility(View.INVISIBLE);
        Utils.getApiService().changePwd(setupSendData())
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                 Snackbar.make(progressBar, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                                Completable.timer(3000, TimeUnit.MILLISECONDS)
                                        .subscribe(() -> dismiss());

                            } else {
                                relative_send_click.setVisibility(View.VISIBLE);
                                Snackbar.make(progressBar, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();



                            }

                        } else {
                            relative_send_click.setVisibility(View.VISIBLE);
                            Snackbar.make(progressBar, "Something went wrong", Snackbar.LENGTH_SHORT).show();


                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        relative_send_click.setVisibility(View.VISIBLE);
                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();

                    }
                });

    }

    private JsonObject setupSendData() {
        JsonObject object = new JsonObject();
        object.addProperty("user_id", prefs.getUserid());
        object.addProperty("old_password", pwd_old.getText().toString());
        object.addProperty("new_password", pwd_new.getText().toString());
        return object;
    }

}
