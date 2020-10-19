package com.spectrum.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.spectrum.services.adapters.HomeBgImageAdapter;
import com.spectrum.services.auth.LoginActivity;
import com.spectrum.services.auth.SignUpActivity;
import com.spectrum.services.booking.cleaning.BookCleaningActivity;
import com.spectrum.services.booking.maintenance.BookMaintenanceActivity;
import com.spectrum.services.drawer.AboutUsActivity;
import com.spectrum.services.drawer.ChatActivity;
import com.spectrum.services.drawer.ContactusActivity;
import com.spectrum.services.drawer.booking_history.BookingCancelActivity;
import com.spectrum.services.drawer.booking_history.BookingHistoryActivity;
import com.spectrum.services.drawer.faq.FaqActivity;
import com.spectrum.services.drawer.notifications.NotificationActivity;
import com.spectrum.services.drawer.service_area.ServiceAreaActivity;
import com.spectrum.services.drawer.services.DetailCleaningActivity;
import com.spectrum.services.drawer.services.DetailMaintenanceActivity;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.profile.ProfileActivity;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "message";
    @BindView(R.id.home_bg_viewpager)
    ViewPager bg_view_pager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 3;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //drawer stuff
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.iv_drawer_user)
    ImageView drawer_user_image;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.tv_drawer_name)
    TextView drawer_user_name;
    @BindView(R.id.drawer_logout_click)
    RelativeLayout relative_logout;
    @BindView(R.id.bottom_buttons)
    LinearLayout bottom_auth_layout;
    @BindView(R.id.iv_drawer_edit)
    ImageView iv_profile_edit;

    @BindView(R.id.drawer_menu_bookinghistory)
    LinearLayout drawer_menu_booking_history;


    @BindView(R.id.drawer_menu_bookingcancel)
    LinearLayout drawer_menu_bookingcancel;

    @BindView(R.id.drawer_menu_notification)
    LinearLayout drawer_menu_notification;

    @BindView(R.id.dots_indicator)
    CircleIndicator dotsIndicator;

    private FirebaseAnalytics mFirebaseAnalytics;


    private Prefs prefs;
    private ViewModel viewModel;
    private final int current_version = BuildConfig.VERSION_CODE;
    private final String package_name = BuildConfig.APPLICATION_ID;
    private Boolean ver_data = false;
    CompositeDisposable disposable = new CompositeDisposable();
    AppEventsLogger logger;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        logger = AppEventsLogger.newLogger(getApplicationContext());

        ButterKnife.bind(this);
        setUpToolbar();
        versionCheck();
        //setUpDrawer();
        setupViewPager();
        initSome();
        actionListeners();
        /**
         * loggers
         */
        logContactEvent();


    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logContactEvent() {
        /**
         * contact
         */
        logger.logEvent(AppEventsConstants.EVENT_NAME_CONTACT);
    }

    private void initSome() {

        Log.e(TAG, "package_name: " + package_name);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        prefs = Prefs.with(this);
        setUserData();

    }

    private void versionCheck() {
        Log.e(TAG, "versionCheck: current:" + current_version);
        disposable.add(Utils.getApiService().getVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(vUpdateModel -> vUpdateModel)
                .subscribe(vUpdateModel -> {
                            ver_data = true;
                            if (vUpdateModel.getStatus().equalsIgnoreCase("success")) {
                                Log.e(TAG, "versionCheck:from_server " + vUpdateModel.getVersionAndroid().getNew_version());
                                if (current_version < vUpdateModel.getVersionAndroid().getNew_version()) {
                                    if (vUpdateModel.getVersionAndroid().getType().equalsIgnoreCase("critical")) {
                                        showAlertDialog(true);
                                    } else {
                                        showAlertDialog(false);
                                    }

                                } else {


                                }


                            } else {


                            }

                        },
                        throwable -> {


                        }));
    }

    private void showAlertDialog(Boolean type) {
        AlertDialog.Builder dialog_critical = new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("New version available. Download now !")
                .setCancelable(false)
                .setNegativeButton("cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();

                })
                .setPositiveButton("Update", ((dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + package_name));
                    startActivity(intent);
                    finish();

                }));
        AlertDialog.Builder dialog_normal = new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("New version available. Download now !")
                .setCancelable(false)
                .setNegativeButton("skip", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    ;

                })
                .setPositiveButton("Update", ((dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + package_name));
                    startActivity(intent);
                    finish();

                }));
        AlertDialog alertDialog;
        if (type) {
            alertDialog = dialog_critical.create();
            alertDialog.show();
        } else {
            alertDialog = dialog_normal.create();
            alertDialog.show();
        }
    }

    private void setUserData() {
        if (prefs.getSignedIn()) {
            UserModel.User user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
            drawer_user_name.setText(user.getName());
            relative_logout.setVisibility(View.VISIBLE);
            bottom_auth_layout.setVisibility(View.GONE);
            iv_profile_edit.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                    .applyDefaultRequestOptions(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_if_user))
                    .load(user.getPhoto())
                    .into(drawer_user_image);
            drawer_menu_booking_history.setVisibility(View.VISIBLE);
            drawer_menu_bookingcancel.setVisibility(View.VISIBLE);
            drawer_menu_notification.setVisibility(View.VISIBLE);
        } else {
            drawer_menu_bookingcancel.setVisibility(View.GONE);
            drawer_menu_booking_history.setVisibility(View.GONE);
            drawer_menu_notification.setVisibility(View.GONE);
            drawer_user_name.setText("Not signed in");
            relative_logout.setVisibility(View.GONE);
            bottom_auth_layout.setVisibility(View.VISIBLE);
            iv_profile_edit.setVisibility(View.GONE);
        }
    }

    private void actionListeners() {
        ((MyApplication) getApplication()).bus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof String) {
                        if (((String) o).equalsIgnoreCase("reviewconfirm")) {
                            setUserData();
                        }
                    }
                });
    }

    private void setUpToolbar() {
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_dehaze_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);

            }
        });
    }

    private void setUpDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_dehaze_24dp);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setupViewPager() {
        HomeBgImageAdapter bgImageAdapter = new HomeBgImageAdapter(this);
        bg_view_pager.setAdapter(bgImageAdapter);
        dotsIndicator.setViewPager(bg_view_pager);
        autoScrollBg();

    }

    private void autoScrollBg() {
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                bg_view_pager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //drawer
    private void closeDrawer() {
        Completable.timer(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> drawer.closeDrawer(GravityCompat.START));
    }

    @OnClick(R.id.iv_drawer_edit)
    public void editProfileClick(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }

    @OnClick(R.id.drawer_logout_click)
    public void logoutClick(View view) {

        alertDialog();

    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Alert");
        builder.setMessage("Are you sure ?");
        builder.setCancelable(false)
                .setPositiveButton("Logout", (dialog, which) -> {
                    Prefs.clearSession(HomeActivity.this);
                    //fb
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();

                    }
                    dialog.cancel();
                    Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @OnClick(R.id.drawer_menu_cleaning)
    public void menuCleaningDetails(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, DetailCleaningActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.drawer_menu_maintenance)
    public void menuMaintenanceDetails(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, DetailMaintenanceActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @OnClick(R.id.drawer_menu_bookinghistory)
    public void menuBookingHistory(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, BookingHistoryActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }

    @OnClick(R.id.drawer_menu_bookingcancel)
    public void setDrawer_menu_bookingcancel(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, BookingCancelActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }


    @OnClick(R.id.drawer_about_us)
    public void menuAboutus(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @OnClick(R.id.drawer_menu_faq)
    public void menuFaq(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, FaqActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @OnClick(R.id.drawer_menu_service_area)
    public void menuServiceArea(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, ServiceAreaActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @OnClick(R.id.drawer_contact_us)
    public void menuContactus(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, ContactusActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @OnClick(R.id.drawer_menu_notification)
    public void menuNotification(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @OnClick(R.id.drawer_chat)
    public void menuChat(View view) {
        closeDrawer();
        Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }


    //home
    @OnClick(R.id.relative_home_loginButton)
    public void home_login_click(View view) {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }

    @OnClick(R.id.relative_home_signupnButton)
    public void home_register_click(View view) {
        Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @OnClick(R.id.relative_cleaning_booking)
    public void cleanBook() {
        logScheduleEvent();
        Intent intent = new Intent(HomeActivity.this, BookCleaningActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.relative_maintenance_booking)
    public void maintenanceBook() {
        logScheduleEvent();
        Intent intent = new Intent(HomeActivity.this, BookMaintenanceActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.telephone)
    public void callNow(View view) {
        logContactEvent();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:800 7274"));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    public void mailClick(View view) {
        logContactEvent();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:info@spectrumservices.ae"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void whatsappClick(View view) {
        logContactEvent();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=+971 556992200"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);

        }
    }
}
