package com.spectrum.services.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.GsonBuilder;
import com.spectrum.services.BaseActivity;
import com.spectrum.services.HomeActivity;
import com.spectrum.services.R;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.profile.phone_verify.PhoneVerifyActivity;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    public static final String TAG = "message";
    private CallbackManager callbackManager;
    private FirebaseAuth auth;
    @BindView(R.id.login_text_input_et_email)
    TextInputEditText login_email;
    @BindView(R.id.login_text_input_et_password)
    TextInputEditText login_password;
    @BindView(R.id.relative_login_button)
    RelativeLayout login_button;
    @BindView(R.id.relative_fb_login_button)
    RelativeLayout fb_button;
    @BindView(R.id.tv_create_account)
    TextView create_account_click;
    @BindView(R.id.tv_forget_pwd)
    TextView forget_pwd_click;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Prefs prefs;
    private AuthViewModel viewModel;
    private CompositeDisposable disposable=new CompositeDisposable();
    private String from_status;

    private String dis_name,fb_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        initSome();
        handleClicks();


    }

    private void initSome() {
        //flow from  content and review via confirm and review
        from_status=getIntent().getStringExtra("from");
        Log.e(TAG, "initSome: "+from_status );

        progressBar.setVisibility(View.GONE);
        viewModel = ViewModelProviders.of(this).get(AuthViewModel.class);
        //initialize fb callbackmanger
        callbackManager = CallbackManager.Factory.create();
        //initialize firebase auth
        auth = FirebaseAuth.getInstance();

        prefs = Prefs.with(this);


    }

    private JsonObject setupDataToSEnd() {
        JsonObject object = new JsonObject();
        object.addProperty("email", login_email.getText().toString());
        object.addProperty("password", login_password.getText().toString());
        object.addProperty("device_id", FirebaseInstanceId.getInstance().getToken());
        object.addProperty("device_type", "Android");
        return object;

    }

    private void handleClicks() {
        login_button.setOnClickListener(view -> {
            if (validate(login_email.getText().toString(), login_password.getText().toString())) {
                loginApi();
                logSubscribeEvent("order","USD",210);

            }

        });

        fb_button.setOnClickListener(view -> {
            logSubscribeEvent("order","USD",210);
            facebookCall();

        });

        create_account_click.setOnClickListener(view -> {
            if (from_status!=null&&from_status.equalsIgnoreCase("reviewconfirm"))
            {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("from","reviewconfirm");
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }else
            {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }



        });

        forget_pwd_click.setOnClickListener(view -> {
            ForgetPasswordFrag frag = new ForgetPasswordFrag();
            frag.show(getFragmentManager(), "forget password fragment");

        });

    }

    private void loginApi() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(viewModel.loginApi(setupDataToSEnd())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(userModel -> userModel)
                .subscribe(this::handleResponce, throwable -> {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(progressBar, throwable.getMessage(), Snackbar.LENGTH_SHORT).show();

                }));

    }

    private void handleResponce(UserModel userModel) {
        progressBar.setVisibility(View.GONE);
        if (userModel.getStatus().equalsIgnoreCase("success")) {
            savetoPreference(userModel);
            loginSuccess();

        } else if (userModel.getStatus().equalsIgnoreCase("invalid")){
            alertDialog();
        }
        else
        {
            Snackbar.make(progressBar, userModel.getMessage(), Snackbar.LENGTH_SHORT).show();
        }



    }
    private void alertDialog( ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Alert");
        builder.setMessage("Please Register");
        builder.setCancelable(true)
                .setPositiveButton("Register", (dialog, which) -> {
                    dialog.dismiss();
                    create_account_click.performClick();

                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void loginSuccess() {
        if (from_status!=null&&from_status.equalsIgnoreCase("reviewconfirm"))
        {
            ((MyApplication)getApplication()).bus()
                    .send(from_status);
            onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        }else
        {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();

        }

    }

    private void savetoPreference(UserModel userModel) {

        prefs.setUserid(userModel.getUser().getUser_id());
        prefs.setSignedIn(true);
        prefs.setNumberVerified(userModel.getUser().getIs_verified().toString());
        prefs.setUserDetails(Utils.convertmodelToString(userModel.getUser()));
        UserModel.User user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
        Log.e(TAG, "savetoPreference: " + user.getName());



    }


    private Boolean validate(String semail, String spwd) {
        if (!Utils.emailValidate(semail)) {
            Snackbar.make(login_email, "Please enter a valid email address!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.isPasswordValid(spwd)) {
            Snackbar.make(login_email, "Please enter your password", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void facebookCall() {
        progressBar.setVisibility(View.VISIBLE);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.e(TAG, "onSuccess: "+loginResult.getAccessToken().getUserId() );
            }

            @Override
            public void onCancel() {
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onError(FacebookException error) {
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        handleFbUser(user,token);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity
                                        .this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        handleFbUser(null,null);
                    }

                    // ...
                });


    }

    private void handleFbUser(FirebaseUser firebaseUser,AccessToken token) {
        if (firebaseUser != null) {
            dis_name=firebaseUser.getDisplayName();
            fb_id=token.getUserId();

                JsonObject object = new JsonObject();
                object.addProperty("name", firebaseUser.getDisplayName());
                object.addProperty("email", firebaseUser.getEmail()!=null?firebaseUser.getEmail():"");
                object.addProperty("auth_token", token.getUserId());
                object.addProperty("device_id", FirebaseInstanceId.getInstance().getToken());
                object.addProperty("device_type", "Android");
                loginFbApi(object);


        }
        else {
            Log.e(TAG, "handleFbUser: null" );
        }

    }

    private void loginFbApi(JsonObject jsonObject) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(viewModel.loginFbApi(jsonObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(userModel -> userModel)
                .subscribe(userModel -> {
                    progressBar.setVisibility(View.GONE);
                    if (userModel.getStatus().equalsIgnoreCase("success"))
                    {
                        prefs.setFbSignIn(true);
                        handleResponce(userModel);

                    }
                    else if (userModel.getStatus().equalsIgnoreCase("error"))
                    {
                        Bundle bundle=new Bundle();
                        bundle.putString("name",dis_name);
                        bundle.putString("auth_token",fb_id);
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        intent.putExtras(bundle);

                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        disposable.add(Completable.timer(2000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            startActivity(intent);
                                        }




                                ));
                        Snackbar.make(progressBar, "Cant access your email from Facebook.  Please Sign Up", Snackbar.LENGTH_SHORT).show();




                    }
                    else
                    {
                        Snackbar.make(progressBar, userModel.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }

                }, throwable -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "loginFbApi: "+throwable.getMessage() );
                    Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();




                }));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}
