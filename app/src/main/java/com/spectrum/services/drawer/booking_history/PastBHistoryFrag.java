package com.spectrum.services.drawer.booking_history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PastBHistoryFrag extends Fragment {
    @BindView(R.id.recycler_past_bhistory)
    RecyclerView recycler_past;

    @BindView(R.id.relative_error_no_item)
    RelativeLayout relative_error_no_item;
    @BindView(R.id.tv_error_message)
    TextView error_message;


    private String data;
    private AdapterBookingHistory adapter;
    private BookingHistoryModel historyModel;
    private List<BookingHistoryModel.CurrentData> datalist = new ArrayList<>();


    public static PastBHistoryFrag InstanceMaint(String s) {
        PastBHistoryFrag frag = new PastBHistoryFrag();
        Bundle bundle = new Bundle();
        bundle.putString("data", s);
        frag.setArguments(bundle);
        return frag;

    }

    private void initRecycler() {
        recycler_past.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (adapter == null) {
            adapter = new AdapterBookingHistory(datalist, getActivity(), false);
            recycler_past.setAdapter(adapter);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments().getString("data");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_maintenance_bhistory, container, false);
        ButterKnife.bind(this, view);
        initSome();
        initRecycler();
        if (!data.isEmpty()) {
            recycler_past.setVisibility(View.VISIBLE);
            historyModel = new Gson().fromJson(data, BookingHistoryModel.class);
            if (historyModel.getPastData().size() > 0) {
                datalist.clear();
                datalist.addAll(historyModel.getPastData());

                adapter = new AdapterBookingHistory(datalist, getActivity(), false);
                recycler_past.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                recycler_past.setVisibility(View.GONE);
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
