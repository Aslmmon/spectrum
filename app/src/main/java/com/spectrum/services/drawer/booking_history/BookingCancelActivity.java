package com.spectrum.services.drawer.booking_history;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.spectrum.services.R;
import com.spectrum.services.models.BookingHistoryModel;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingCancelActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private List<BookingHistoryModel.CurrentData> dataList = new ArrayList<>();
    Prefs prefs;
    CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        ButterKnife.bind(this);
        setupToolbar();

        initSome();
        actionListeners();
    }

    private void initSome() {

        prefs = Prefs.with(this);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        getBookingHistoryApi();

//        Snackbar.make(tabLayout, "Please, long tap to cancel booking.", Snackbar.LENGTH_SHORT).show();
    }

    private void setupToolbar() {
        toolbar.setTitle("Cancel Booking");
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onBackPressed();
    }

    private void getBookingHistoryApi() {
        JsonObject object = new JsonObject();
        object.addProperty("user_id", prefs.getUserid());
        progressBar.setVisibility(View.VISIBLE);
        Utils.getApiService().getBookingHistory(object)
                .enqueue(new Callback<BookingHistoryModel>() {
                    @Override
                    public void onResponse(Call<BookingHistoryModel> call, Response<BookingHistoryModel> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            dataList.clear();
                            dataList.addAll(response.body().getCurrentData());
                            setupViewPager(viewPager, response.body());

                        }

                    }

                    @Override
                    public void onFailure(Call<BookingHistoryModel> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("bookinghistoryactivity", "onFailure: " + t.getMessage());
                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_LONG)
                                .setAction("Retry", view -> {
                                    getBookingHistoryApi();
                                })
                                .show();

                    }
                });
    }

    private void setupViewPager(ViewPager viewPager, BookingHistoryModel bookingHistoryModel) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(CancelBookingFrag.InstanceClean(new Gson().toJson(bookingHistoryModel)), "Current");
//        adapter.addFragment(PastBHistoryFrag.InstanceMaint(new Gson().toJson(bookingHistoryModel)), "Past");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void actionListeners() {
        disposable.add(((MyApplication) getApplication()).bus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof String) {
                        if (((String) o).equalsIgnoreCase("kill")) {
                            this.finish();
                        } else if (((String) o).equalsIgnoreCase("cancel_success")) {

                            Log.e("Called", "yes");
                            getBookingHistoryApi();

                            Snackbar.make(tabLayout, "Booking cancelled successfully.", Snackbar.LENGTH_SHORT).show();

                        }
                    }

                })

        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}

