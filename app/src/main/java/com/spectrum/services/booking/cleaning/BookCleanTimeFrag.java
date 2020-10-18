package com.spectrum.services.booking.cleaning;


import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.R;
import com.spectrum.services.adapters.CleanBookingTimeAdapter;
import com.spectrum.services.models.BookTime;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Utils;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Abins Shaji on 05/02/18.
 */

public class BookCleanTimeFrag extends DialogFragment implements CleanBookingTimeAdapter.PassTime {
    public static final String TAG = "message";
    private String date, hours;
    @BindView(R.id.recycler_clean_book_frag)
    RecyclerView time_recycler;
    CleanBookingTimeAdapter adapter;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_sel_date)
    TextView sel_date;
    private String sel_time = "";


    public static int pos = -1;
    private List<String> bookTimeList = new ArrayList<>();
    private EventTypeOne eventTypeOne;
    private CompositeDisposable disposable = new CompositeDisposable();


    static BookCleanTimeFrag newInstance(String date, String hours) {

        BookCleanTimeFrag cleanTimeFrag = new BookCleanTimeFrag();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("hours", hours);
        cleanTimeFrag.setArguments(bundle);
        return cleanTimeFrag;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getArguments().getString("date");
        hours = getArguments().getString("hours");
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_book_clean_time, container, false);
        ButterKnife.bind(this, view);
        setCancelable(true);
        initRecycler();
        initSome();
        getTime();
        return view;


    }

    private void initSome() {

        sel_date.setText(date!=null?Utils.getDateFormat(date):"Oops");
        progressBar.setVisibility(View.GONE);
        eventTypeOne = new EventTypeOne("", "");
    }

    private void initRecycler() {
        time_recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        time_recycler.setHasFixedSize(true);
        if (adapter == null) {
            adapter = new CleanBookingTimeAdapter(getActivity(), this, bookTimeList);
            time_recycler.setAdapter(adapter);
        }


    }

    @OnClick(R.id.iv_time_frag_cancel_click)
    public void cancelClick(View view) {
        dismiss();
    }

    @OnClick(R.id.relative_clean_frag_submit_click)
    public void submitClick(View view) {
        if (sel_time.length() > 0) {
            Log.e(TAG, "submitClick: ");
            eventTypeOne.date = date;
            eventTypeOne.time = sel_time;
            ((MyApplication) getActivity().getApplication()).bus().send(eventTypeOne);
            //EventBus.getDefault().post(new BookCleaningDateActivity.MessageEvent(date,sel_time));
            dismiss();


        } else {
            Snackbar.make(view, "Please Select time", Snackbar.LENGTH_SHORT).show();
        }


    }

    @Override
    public void getTime(String t) {
        sel_time = t;
        Log.e(TAG, "getTime:in Fragment " + sel_time);
    }

    public class EventTypeOne {
        String time;
        String date;

        public EventTypeOne(String time, String date) {
            this.time = time;
            this.date = date;

        }
    }

    private void getTime() {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = Calendar.getInstance().getTime();
        String formattedTodayDate = curFormater.format(todayDate);



        time_recycler.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Log.i("data",date);
        Log.i("data",hours);
        if(formattedTodayDate.equals(date)){
            Calendar c = Calendar.getInstance();
            c.setTime(todayDate);
            c.add(Calendar.DATE, 1);
            Date nextDate = c.getTime();
            date = curFormater.format(nextDate);
           // Log.i("data","next Day needed"+nextDay);
        }

        Utils.getApiService().getBookTimeForDate(date, hours)
                .enqueue(new Callback<BookTime>() {
                    @Override
                    public void onResponse(Call<BookTime> call, Response<BookTime> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body().getStatus().equalsIgnoreCase("success")) {
                                time_recycler.setVisibility(View.VISIBLE);
                                bookTimeList.addAll(response.body().getTimeData());
                                Log.e(TAG, "onResponse: "+bookTimeList.size() );
                                adapter = new CleanBookingTimeAdapter(getActivity(),BookCleanTimeFrag.this,bookTimeList);
                                time_recycler.setAdapter(adapter);
                                adapter.notifyDataSetChanged();



                            } else if (response.body().getStatus().equalsIgnoreCase("error")) {
                                Log.i("data","clicked");
                                Snackbar.make(progressBar, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();

                            }
                        } else {
                            Snackbar.make(progressBar, "Something went wrong", Snackbar.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<BookTime> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", v -> {
                                    getTime();

                                })
                                .show();

                    }
                });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
