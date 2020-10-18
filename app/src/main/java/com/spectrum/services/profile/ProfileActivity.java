package com.spectrum.services.profile;

import android.accounts.NetworkErrorException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.R;
import com.spectrum.services.models.ServiceArea;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.profile.phone_verify.PhoneVerifyActivity;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_profile_image)
    ImageView profile_image;
    @BindView(R.id.relative_profile_save)
    RelativeLayout rel_profile_save;
    @BindView(R.id.iv_edit_profile_pic)
    ImageView edit_profile_pic;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_reset_pwd_click)
    TextView tv_reset_pwd_click;
    @BindView(R.id.tv_pmob_verfication)
    TextView phone_verify_click;
    @BindView(R.id.iv_profile_edit_click)
    ImageView profile_full_edit_click;

    @BindView(R.id.tv_pname)
    EditText profile_name;
    @BindView(R.id.tv_pmob)
    EditText profile_mob;
    @BindView(R.id.tv_pemail)
    EditText profile_email;
    @BindView(R.id.tv_parea)
    EditText profile_area;
    @BindView(R.id.tv_pbuilding)
    EditText profile_building;
    @BindView(R.id.tv_punit)
    EditText profile_unit;
    @BindView(R.id.tv_pstreet)
    EditText profile_street;
    @BindView(R.id.tv_parea_other)
    EditText profile_area_other;


    ArrayAdapter<String> adapter_area;
    @BindView(R.id.spinner_profile_area)
    Spinner spinner_area;
    @BindView(R.id.relative_parea_spinner_click)
    RelativeLayout spinner_relative_click;


    private Bitmap bitmap_pro_pic;
    private String sel_area;
    private Boolean is_area_other = false;
    private Boolean is_editable = false;
    private String sel_area_id;
    private ServiceArea serviceAreaData;

    private ProfileViewModel viewModel;
    private CompositeDisposable disposable = new CompositeDisposable();
    private Prefs prefs;
    private String from_status;
    private Boolean edited = false, is_from = false;
    private UserModel.User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setupToolbar();
        initSome();
        getSrviceArea();
        getUserDetails();
        spinnerListener();
    }

    private void initSome() {


        //flow from confirm and review
        from_status = getIntent().getStringExtra("from");
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        prefs = Prefs.with(this);

        //TODO:next update
        // phone_verify_click.setVisibility(View.GONE);


        if (prefs.getFbSignedIn()) {
            tv_reset_pwd_click.setVisibility(View.GONE);
        } else {
            tv_reset_pwd_click.setVisibility(View.VISIBLE);

        }

        rel_profile_save.setVisibility(View.GONE);
        profile_area_other.setVisibility(View.GONE);
        edit_profile_pic.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.GONE);
        profile_area.setClickable(false);
        profile_email.setEnabled(false);


        controlEditableFields(false);

        //profile image
        edit_profile_pic.setVisibility(View.INVISIBLE);
        is_editable = false;
        profile_image.setOnClickListener(v -> {
            if (is_editable) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setActivityTitle("Crop")
                        .setCropMenuCropButtonTitle("Done")
                        .setMultiTouchEnabled(true)
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);
            }
        });

        //save click
        rel_profile_save.setOnClickListener(v -> {
            if (validate()) {
                updateProfile(setupProfileUPdateData());

            }

        });


    }

    private void testEditFielEmpty() {
        if (profile_name.getText().toString().isEmpty()) {
            //Snackbar.make(progressBar, "Enter your name", Snackbar.LENGTH_SHORT).show();
            profile_name.setError("Enter your name");

        }
        if (profile_mob.getText().length() < 10) {
            //Snackbar.make(progressBar, "Enter valid phone number", Snackbar.LENGTH_SHORT).show();
            profile_mob.setError("Enter valid phone number");


        }
        if (profile_building.getText().toString().isEmpty()) {
            //Snackbar.make(progressBar, "Enter your building ", Snackbar.LENGTH_SHORT).show();
            profile_building.setError("Enter your building ");


        }
        if (profile_unit.getText().toString().isEmpty()) {
            //Snackbar.make(progressBar, "Enter your unit ", Snackbar.LENGTH_SHORT).show();
            profile_unit.setError("Enter your unit");

        }
        if (profile_street.getText().toString().isEmpty()) {
            //Snackbar.make(progressBar, "Enter your street ", Snackbar.LENGTH_SHORT).show();
            profile_street.setError("Enter your street");

        }

    }

    private void getUserDetails() {


        editProfile();

        if (prefs.getSignedIn()) {
            progressBar.setVisibility(View.VISIBLE);
            disposable.add(viewModel.getUser(Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class).getUser_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .map(userModel -> userModel)
                    .subscribe(this::handleUserDetails, throwable -> {
                        progressBar.setVisibility(View.GONE);

                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", view -> getUserDetails())
                                .show();
                        // profile_full_edit_click.setVisibility(View.INVISIBLE);


                    }));
        }

    }

    private void handleUserDetails(UserModel userModel) {
        progressBar.setVisibility(View.GONE);
        if (userModel.getStatus().equalsIgnoreCase("success")) {
            //  profile_full_edit_click.setVisibility(View.VISIBLE);
            setUserDetails(userModel.getUser());
            user = userModel.getUser();
            savetoPreference(userModel);


        } else {
            // profile_full_edit_click.setVisibility(View.VISIBLE);
            Snackbar.make(progressBar, userModel.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setUserDetails(UserModel.User userDetails) {
        //set

        prefs.setUserid(userDetails.getUser_id());
        profile_name.setText(userDetails.getName());
        profile_email.setText(userDetails.getEmail());
        profile_mob.setText(userDetails.getPhone());
        profile_building.setText(userDetails.getBuilding());
        profile_unit.setText(userDetails.getUnit());
        profile_street.setText(userDetails.getStreet());
        Glide.with(this)
                .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                .applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                .applyDefaultRequestOptions(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_if_user))
                .load(userDetails.getPhoto())
                .into(profile_image);

        if (userDetails.getArea_name().equalsIgnoreCase(""))//check for area never selected
        {
            spinner_area.setSelection(0);
        } else {
            Log.e(TAG, "setUserDetails: normal" + viewModel.getMapServicArea().get(userDetails.getArea_name()));
            spinner_area.setSelection(getIndex(spinner_area, userDetails.getArea_name()));
        }

        if (userDetails.getArea_name().equalsIgnoreCase("other")) {

            profile_area_other.setText(userDetails.getOther_area());
            is_area_other = true;
            profile_area_other.setVisibility(View.VISIBLE);
        } else {
            is_area_other = false;
            profile_area_other.setVisibility(View.GONE);
        }

        if (userDetails.getPhone().isEmpty() || userDetails.getPhone() == null) {
            phone_verify_click.setVisibility(View.GONE);
        }
        if (userDetails.getIs_verified()) {
            phone_verify_click.setText("Verified");
            phone_verify_click.setTextColor(getResources().getColor(R.color.colorAccent));
            phone_verify_click.setClickable(false);

        } else {
            phone_verify_click.setText("Click to verify !");
            phone_verify_click.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            phone_verify_click.setClickable(true);

        }

        if (from_status != null && from_status.equalsIgnoreCase("reviewconfirm")) {
            is_from = true;
            Log.e(TAG, "initSome: " + from_status);
            editProfile();
        }


    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }

        }
        return index;
    }


    private void controlEditableFields(Boolean state) {
        if (state) {
            moveCursorToEnd(profile_name);
            phone_verify_click.setVisibility(View.INVISIBLE);

        } else {
            //TODO:next update
            phone_verify_click.setVisibility(View.VISIBLE);

        }


        spinner_area.setEnabled(state);
        spinner_relative_click.setEnabled(state);
        profile_name.setEnabled(state);
        profile_area.setEnabled(state);
        profile_street.setEnabled(state);
        profile_mob.setEnabled(state);
        profile_building.setEnabled(state);
        profile_unit.setEnabled(state);
        spinner_relative_click.setClickable(state);
        profile_area_other.setEnabled(state);


    }

    public void moveCursorToEnd(TextView textView) {
        Editable etext = textView.getEditableText();
        Selection.setSelection(etext, textView.getText().toString().length());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap_pro_pic = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Glide.with(this)
                        .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                        .applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                        .applyDefaultRequestOptions(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_if_user))
                        .load(resultUri)
                        .into(profile_image);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setupToolbar() {
        toolbar.setTitle("Profile");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp_white);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);


    }


    private void setupSpinner(ServiceArea serviceArea) {
        serviceAreaData = serviceArea;
        String serviceData[] = viewModel.setUpSpinnerData(serviceArea);
        if (!serviceData.equals(null)) {
            adapter_area = new ArrayAdapter<>(this, R.layout.spinner_item_layout_white, serviceData);
            // Specify the layout to use when the list of choices appears
            adapter_area.setDropDownViewResource(R.layout.spinner_item_layout_profile);

            spinner_area.setAdapter(adapter_area);


        }


    }

    private void getSrviceArea() {

        disposable.add(viewModel.getServiceArea()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(serviceArea -> serviceArea).subscribe(this::setupSpinner, throwable -> {
                    if (throwable instanceof NetworkErrorException) {
                        Snackbar.make(progressBar, throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }));


    }

    private void spinnerListener() {
        spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemSelected:area " + adapterView.getItemAtPosition(i));
                sel_area = adapterView.getItemAtPosition(i).toString();
                if (!sel_area.equalsIgnoreCase("Select_area")) {
                    sel_area_id = getAreaIdFromName(sel_area);
                }
                Log.e(TAG, "onItemSelected: area_id" + sel_area_id);


                if (Utils.isStringEqual(adapterView.getItemAtPosition(i).toString(), "other")) {
                    is_area_other = true;
                    profile_area_other.setVisibility(View.VISIBLE);

                } else {
                    profile_area_other.setVisibility(View.GONE);
                    is_area_other = false;

                }
                TextView tv = (TextView) view;
                if (i == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(getResources().getColor(R.color.red));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private String getAreaIdFromName(String name) {
        for (ServiceArea.AreaData area : serviceAreaData.getAreaData()) {
            if (area.getArea_name().equals(name)) {
                return area.getArea_id();
            }
        }
        return null;

    }

    private Boolean validate() {
        if (profile_name.getText().toString().isEmpty()) {
            Snackbar.make(progressBar, "Enter your name", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.isPhoneValid(profile_mob.getText().toString())) {
            Snackbar.make(progressBar, "Enter valid phone number", Snackbar.LENGTH_SHORT).show();
            return false;

        } else if (profile_building.getText().toString().isEmpty()) {
            Snackbar.make(progressBar, "Enter your building ", Snackbar.LENGTH_SHORT).show();
            return false;

        } else if (profile_unit.getText().toString().isEmpty()) {
            Snackbar.make(progressBar, "Enter your unit ", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (profile_street.getText().toString().isEmpty()) {
            Snackbar.make(progressBar, "Enter your street ", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (sel_area.equalsIgnoreCase("Select Area")) {
            Snackbar.make(progressBar, "Select Area ", Snackbar.LENGTH_SHORT).show();
            return false;

        } else if (is_area_other) {
            if (profile_area_other.getText().toString().isEmpty()) {
                Snackbar.make(progressBar, "Enter your area ", Snackbar.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private JsonObject setupProfileUPdateData() {
        JsonObject object = new JsonObject();
        object.addProperty("user_id", Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class).getUser_id());
        object.addProperty("name", profile_name.getText().toString());
        object.addProperty("phone", profile_mob.getText().toString());
        object.addProperty("photo", bitmap_pro_pic != null ? encodeTobase64(bitmap_pro_pic) : "");
        object.addProperty("building", profile_building.getText().toString());
        object.addProperty("unit", profile_unit.getText().toString());
        object.addProperty("street", profile_street.getText().toString());
        object.addProperty("area_id", sel_area_id);
        object.addProperty("other_area_name", profile_area_other.getText().toString());
        return object;
    }

    private void updateProfile(JsonObject jsonObject) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(viewModel.updateProfile(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleUpdateProfile
                        , throwable -> {
                            if (throwable instanceof IOException) {
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();
                            }
                        }));
    }

    private void handleUpdateProfile(UserModel userModel) {
        progressBar.setVisibility(View.GONE);
        if (userModel.getStatus().equalsIgnoreCase("success")) {
            edited = true;
            is_editable = false;
            edit_profile_pic.setVisibility(View.GONE);
            rel_profile_save.setVisibility(View.GONE);
            controlEditableFields(false);
            getUserDetails();

        } else {
            is_editable = true;
            edit_profile_pic.setVisibility(View.VISIBLE);
            controlEditableFields(true);
            Snackbar.make(progressBar, "Something Went wrong.PLease try Later", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void savetoPreference(UserModel userModel) {

        prefs.setUserid(userModel.getUser().getUser_id());
        prefs.setSignedIn(true);
        prefs.setNumberVerified(userModel.getUser().getIs_verified().toString());
        prefs.setUserDetails(Utils.convertmodelToString(userModel.getUser()));
        UserModel.User user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
        Log.e(TAG, "savetoPreference: " + user.getName());
        ((MyApplication) getApplication()).bus()
                .send("reviewconfirm");

        if (from_status != null && from_status.equalsIgnoreCase("reviewconfirm") && edited) {
            onBackPressed();
            finish();
        }


    }

    @OnClick(R.id.iv_profile_edit_click)
    public void editProfile() {

        profile_full_edit_click.setVisibility(View.INVISIBLE);

        is_editable = true;
        rel_profile_save.setVisibility(View.VISIBLE);
        edit_profile_pic.setVisibility(View.VISIBLE);

        controlEditableFields(true);

        if (is_from)
            //testEditFielEmpty();
            Log.e(TAG, "editProfile: ");


    }

    @OnClick(R.id.tv_pmob_verfication)
    public void verifyText(View view) {

    }

    @OnClick(R.id.tv_reset_pwd_click)
    public void resetPassword(View view) {
        ResetPasswordFrag frag = new ResetPasswordFrag();
        frag.show(getFragmentManager(), "");
    }

    @OnClick(R.id.tv_pmob_verfication)
    public void phoneVerify(View view) {
        if (user.getPhone() != null && !user.getPhone().equalsIgnoreCase("")) {

            Intent intent = new Intent(ProfileActivity.this, PhoneVerifyActivity.class);
            intent.putExtra("userdetails", Utils.convertmodelToString(user));
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        } else {
            Snackbar.make(progressBar, "Enter phone number to verify !", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure ?");
        builder.setMessage("Abort changes");
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        is_editable = false;
                        onBackPressed();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String encodeTobase64(Bitmap image) {
        Bitmap image_ = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image_.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        if (is_editable && !is_from) {
            //alertDialog();
        } else {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            super.onBackPressed();
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
