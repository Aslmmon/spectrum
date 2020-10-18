package com.spectrum.services.drawer.booking_history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.spectrum.services.R;
import com.spectrum.services.models.BookingHistoryModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 19/02/18.
 */

public class CancelBookingFrag extends Fragment {
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_current_bhistory)
    RecyclerView recycler_current_bh;

    @BindView(R.id.relative_error_no_item)
    RelativeLayout relative_error_no_item;
    @BindView(R.id.tv_error_message)
    TextView error_message;

    private String data;
    private AdapterBookingHistory adapter;
    private BookingHistoryModel historyModel;
    private List<BookingHistoryModel.CurrentData> datalist = new ArrayList<>();

    public static CancelBookingFrag InstanceClean(String s) {
        CancelBookingFrag frag = new CancelBookingFrag();
        Bundle bundle = new Bundle();
        bundle.putString("data", s);
        frag.setArguments(bundle);
        return frag;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments().getString("data");

    }

    private void initRecycler() {
        recycler_current_bh.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (adapter == null) {
            adapter = new AdapterBookingHistory(datalist, getActivity(), true);
            recycler_current_bh.setAdapter(adapter);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_cleaning_bhistory, container, false);
        ButterKnife.bind(this, view);
        initRecycler();
        initSome();
        if (!data.isEmpty()) {
            recycler_current_bh.setVisibility(View.VISIBLE);

            historyModel = new Gson().fromJson(data, BookingHistoryModel.class);
            if (historyModel.getCurrentData().size() > 0) {

                datalist.clear();
                datalist.addAll(historyModel.getCurrentData());

                adapter = new AdapterBookingHistory(datalist, getActivity(), true);
                recycler_current_bh.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                recycler_current_bh.setVisibility(View.GONE);
                setupErrorLayout();

            }

        }

        return view;
    }

    private void initSome() {
        relative_error_no_item.setVisibility(View.GONE);
    }

    private void setupErrorLayout() {
        relative_error_no_item.setVisibility(View.VISIBLE);
        error_message.setText("No bookings found !");
    }
}
