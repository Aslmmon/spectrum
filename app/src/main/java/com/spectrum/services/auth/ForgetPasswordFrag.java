package com.spectrum.services.auth;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.spectrum.services.R;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.utils.Utils;

import org.jetbrains.annotations.Nullable;

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

public class ForgetPasswordFrag extends DialogFragment {
    @BindView(R.id.fpwd_text_input)
    TextInputEditText email;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.relative_send_click)
    RelativeLayout relative_send_click;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_forget_pwd,container,false);
        ButterKnife.bind(this,view);
        setCancelable(false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
        setCancelable(false);
    }
    @OnClick(R.id.relative_send_click)public void emailSend(View view)
    {
        if(Utils.emailValidate(email.getText()))
        {
            forgetPwdApi(email.getText().toString());

        }
        else
        {
             Snackbar.make(view, "Enter the mail", Snackbar.LENGTH_SHORT).show();
        }


    }
    @OnClick(R.id.iv_fpwd_close)public void closeDialog(View view)
    {
        dismiss();
    }

    public void forgetPwdApi(String email)
    {
        relative_send_click.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Utils.getApiService().forgetPwd(email)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body().getStatus().equalsIgnoreCase("success"))
                            {
                                 Snackbar.make(progressBar, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                                Completable.timer(3000, TimeUnit.MILLISECONDS)
                                        .subscribe(() -> {dismiss();});

                            }
                            else
                            {
                                relative_send_click.setVisibility(View.VISIBLE);
                                 Snackbar.make(progressBar, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
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

}
