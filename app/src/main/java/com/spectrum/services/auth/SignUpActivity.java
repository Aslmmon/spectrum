package com.spectrum.services.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.spectrum.services.HomeActivity;
import com.spectrum.services.R;
import com.spectrum.services.models.UserModel;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "message";
    private CallbackManager callbackManager;
    private FirebaseAuth auth;

    @BindView(R.id.signup_text_input_et_name)
    TextInputEditText signup_name;
    @BindView(R.id.signup_text_input_et_mobile)
    TextInputEditText signup_mobile;
    @BindView(R.id.signup_text_input_et_email)
    TextInputEditText signup_email;
    @BindView(R.id.signup_text_input_et_password)
    TextInputEditText signup_password;
    @BindView(R.id.relative_signup_button)
    RelativeLayout signup_button;
    @BindView(R.id.relative_signup_fb_button)
    RelativeLayout fb_signup_button;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Prefs prefs;
    private Bundle bundle;
    private String auth_token = null;
    private String name;

    private AuthViewModel viewModel;
    private CompositeDisposable disposable = new CompositeDisposable();

    String from_status;
    private String dis_name, fb_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        initSome();


    }

    private void initSome() {
        //flow from  content and review via login
        fb_signup_button.setVisibility(View.VISIBLE);
        from_status = getIntent().getStringExtra("from");

        bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.getString("name") != null) {
                signup_name.setText(bundle.getString("name"));
                auth_token = bundle.getString("auth_token");
                fb_signup_button.setVisibility(View.INVISIBLE);
            }
        }
        Log.e(TAG, "initSome: " + from_status);

        progressBar.setVisibility(View.GONE);

        //view model
        viewModel = ViewModelProviders.of(this).get(AuthViewModel.class);
        //initialize fb callbackmanger
        callbackManager = CallbackManager.Factory.create();
        //initialize firebase auth
        auth = FirebaseAuth.getInstance();
        prefs = Prefs.with(this);
        handleClicks();


    }

    private void registerApi() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(viewModel.registerApi(setupDataToSEnd())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(userModel -> userModel).subscribe(this::handleResponce, throwable -> {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();
                }));


    }

    private void handleResponce(UserModel userModel) {
        progressBar.setVisibility(View.GONE);
        if (userModel.getStatus().equalsIgnoreCase("success")) {
            savetoPreference(userModel);
            loginSuccess();


        } else {
            Snackbar.make(progressBar, userModel.getMessage(), Snackbar.LENGTH_SHORT).show();
        }

    }

    private void savetoPreference(UserModel userModel) {

        prefs.setUserid(userModel.getUser().getUser_id());
        prefs.setSignedIn(true);
        prefs.setNumberVerified("false");
        prefs.setUserDetails(Utils.convertmodelToString(userModel.getUser()));
        UserModel.User user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
        Log.e(TAG, "savetoPreference: " + user.getName());


    }

    private Boolean validate(String semail, String spwd, String name, String phone) {
        if (name.isEmpty()) {
            Snackbar.make(signup_email, "Please enter your Name", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.isPhoneValid(signup_mobile.getText().toString())) {
            Snackbar.make(signup_email, "Please enter your valid phone number", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.emailValidate(semail)) {
            Snackbar.make(signup_email, "Please enter a valid email address!", Snackbar.LENGTH_SHORT).show();

            return false;
        } else if (!Utils.isPasswordValid(spwd)) {
            Snackbar.make(signup_email, "Please enter your password", Snackbar.LENGTH_SHORT).show();
            return false;

        }

        return true;
    }

    private JsonObject setupDataToSEnd() {
        JsonObject object = new JsonObject();
        object.addProperty("name", signup_name.getText().toString());
        object.addProperty("email", signup_email.getText().toString());
        object.addProperty("phone", signup_mobile.getText().toString());
        object.addProperty("password", signup_password.getText().toString());
        object.addProperty("device_id", FirebaseInstanceId.getInstance().getToken());
        object.addProperty("device_type", "Android");
        object.addProperty("fb_id", auth_token != null ? auth_token : "");
        return object;

    }

    private void handleClicks() {
        signup_button.setOnClickListener(view -> {
            if (validate(signup_email.getText().toString(), signup_password.getText().toString(), signup_name.getText().toString(), signup_mobile.getText().toString())) {

                registerApi();


            }

        });

        fb_signup_button.setOnClickListener(view -> {
            facebookCall();

        });
    }

    private void loginSuccess() {
        if (from_status != null && from_status.equalsIgnoreCase("reviewconfirm")) {
            ((MyApplication) getApplication()).bus()
                    .send(from_status);
            onBackPressed();
            finish();

        } else {
            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();

        }
    }

    private void facebookCall() {
        progressBar.setVisibility(View.VISIBLE);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

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
                        handleFbUser(user, token);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignUpActivity
                                        .this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        handleFbUser(null, null);
                    }

                    // ...
                });


    }

    private void handleFbUser(FirebaseUser firebaseUser, AccessToken token) {
        if (firebaseUser != null) {
            dis_name = firebaseUser.getDisplayName();
            fb_id = token.getUserId();
            JsonObject object = new JsonObject();
            object.addProperty("name", firebaseUser.getDisplayName());
            object.addProperty("email", firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "");
            object.addProperty("auth_token", token.getUserId());
            object.addProperty("device_id", FirebaseInstanceId.getInstance().getToken());
            object.addProperty("device_type", "Android");
            registerFbApi(object);


        }

    }

    private void registerFbApi(JsonObject jsonObject) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(viewModel.registerFbApi(jsonObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(userModel -> userModel)
                .subscribe(userModel -> {
                    progressBar.setVisibility(View.GONE);
                    if (userModel.getStatus().equalsIgnoreCase("success")) {

                        prefs.setFbSignIn(true);
                        handleResponce(userModel);

                    } else if (userModel.getStatus().equalsIgnoreCase("error")) {
                        Snackbar.make(progressBar, "Cant access your email from Facebook. Please Sign up", Snackbar.LENGTH_SHORT).show();

                        signup_name.setText(dis_name);
                        auth_token = fb_id;
                        fb_signup_button.setVisibility(View.INVISIBLE);

                    } else {
                        Snackbar.make(progressBar, userModel.getMessage(), Snackbar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }


                }, throwable -> {
                    Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
