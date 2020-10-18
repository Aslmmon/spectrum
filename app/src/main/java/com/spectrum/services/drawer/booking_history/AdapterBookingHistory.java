package com.spectrum.services.drawer.booking_history;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.booking.PaymentActivity;
import com.spectrum.services.models.BookingHistoryModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 26/02/18.
 */

public class AdapterBookingHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BookingHistoryModel.CurrentData> data;
    private Context context;
    private final int CLEAN_HISTORY = 0;
    private final int MAINT_HISTORY = 1;
    private Boolean isCurrent;


    public AdapterBookingHistory(List<BookingHistoryModel.CurrentData> data, Context context, Boolean isCurrent) {
        this.data = data;
        this.context = context;
        this.isCurrent = isCurrent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case CLEAN_HISTORY: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_clean_bhistory, parent, false);
                return new CleanDataVHolder(view);

            }
            case MAINT_HISTORY: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_maintenance_bhistory, parent, false);
                return new MaintDataVHolder(view);

            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (data.get(position).getType()) {
            case "1": {
                ((MaintDataVHolder) holder).setMaintData(data.get(position), isCurrent);

                break;

            }
            case "0": {
                ((CleanDataVHolder) holder).setCleanData(data.get(position), context, isCurrent);

                break;

            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (data.get(position).getType()) {
            case "1": {
                return MAINT_HISTORY;


            }
            case "0": {
                return CLEAN_HISTORY;

            }

        }
        return -1;
    }

    public static class CleanDataVHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_cleanbhistory_date)
        TextView clean_bh_date;
        @BindView(R.id.tv_cleanbhistory_maid)
        TextView clean_bh_maid;
        @BindView(R.id.tv_cleanbhistory_material)
        TextView clean_bh_material;
        @BindView(R.id.tv_cleanbhistory_amount)
        TextView clean_bh_amount;
        @BindView(R.id.button_cleanbhistory_pay)
        Button clean_bh_pay_button;
        @BindView(R.id.iv_clean_bhistory_status)
        ImageView tick_status;
        @BindView(R.id.tv_cleanbhistory_ctype)
        TextView ctype;
        @BindView(R.id.recycler_clean_date)
        RecyclerView recycler_clean_date;
        @BindView(R.id.tv_ref_id)
        TextView ref_id;
        @BindView(R.id.cv_cleanbhistory)
        CardView cv_cleanbhistory;

        AdapterCleanDateTime adapter;
        List<BookingHistoryModel.CurrentData.Shifts> shifts = new ArrayList<>();
        Context context;


        public CleanDataVHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void initRecycler() {
            recycler_clean_date.setLayoutManager(new LinearLayoutManager(context));
            adapter = new AdapterCleanDateTime(context, shifts);
            recycler_clean_date.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


        public void setCleanData(BookingHistoryModel.CurrentData data, Context context, Boolean isCurrent) {
            shifts.clear();
            this.context = context;
            shifts.addAll(data.getSchedule());
            initRecycler();
            //clean_bh_date.setText(data.getDate());
            clean_bh_maid.setText(data.getMaids());
            clean_bh_material.setText(data.getClean_mat());
            clean_bh_amount.setText("AED " + data.getRate());
            ctype.setText(data.getService_type());
            ref_id.setText(data.getReference_id());

            if (data.getPay_status().equalsIgnoreCase("0")) {
                clean_bh_pay_button.setVisibility(View.VISIBLE);
                //tick_status.setVisibility(View.INVISIBLE);


            } else {
                clean_bh_pay_button.setVisibility(View.GONE);
                //tick_status.setVisibility(View.VISIBLE);
            }


            clean_bh_pay_button.setOnClickListener(view -> {
                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("amount", data.getRate());
                intent.putExtra("ref", data.getReference_id());

                //ToDo :after api change.
                intent.putExtra("vat", data.getVat_perc());
                context.startActivity(intent);

            });

            if (isCurrent) {
                clean_bh_pay_button.setVisibility(View.INVISIBLE);
            } else {
                if (data.getIs_cancel().equalsIgnoreCase("yes")) {

                    clean_bh_pay_button.setVisibility(View.INVISIBLE);

                } else {

                    clean_bh_pay_button.setVisibility(View.VISIBLE);

                }
            }

            cv_cleanbhistory.setOnClickListener(v -> {

                if (isCurrent && data.getIs_cancel().equalsIgnoreCase("yes")) {


                } else if (isCurrent && data.getIs_cancel().equalsIgnoreCase("no")) {
                    new AlertDialog.Builder(context)
                            .setTitle("Cancel Booking")
                            .setMessage("Are you sure? You want to cancel the booking.")

                            .setNegativeButton("Cancel", (dialog1, which) -> {

                            })
                            .setPositiveButton("Yes", (dialog1, which) -> {

                                Intent intent = new Intent(context, CancelActivity.class);
                                intent.putExtra("booking_id", data.getReference_id());

                                context.startActivity(intent);

                            }).show();
                }


            });


        }
    }

    public static class MaintDataVHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_maintbhistory_date)
        TextView maint_bh_date;
        @BindView(R.id.tv_maintbhistory_hour)
        TextView maint_bh_hour;
        @BindView(R.id.tv_maintbhistory_priority)
        TextView maint_bh_priority;
        @BindView(R.id.tv_maintbhistory_amount)
        TextView maint_bh_rate;
        @BindView(R.id.button_maintbhistory_pay)
        Button maint_bh_pay_button;
        @BindView(R.id.tv_mainthistory_type)
        TextView maint_type;
        @BindView(R.id.tv_ref_id)
        TextView ref_id;
        @BindView(R.id.tv_status)
        TextView status;
        @BindView(R.id.cv_maintenancebhistory)
        CardView cv_maintenancebhistory;

        public MaintDataVHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setMaintData(BookingHistoryModel.CurrentData currentData, Boolean isCurrent) {
            maint_bh_date.setText(currentData.getDate());
            maint_bh_priority.setText(currentData.getPriority());
            maint_bh_rate.setText("AED " + currentData.getRate());
            maint_bh_pay_button.setVisibility(View.VISIBLE);
            maint_type.setText(currentData.getService_type());
            ref_id.setText(currentData.getReference_id());

            if (currentData.getHours().equalsIgnoreCase("")) {
                maint_bh_hour.setText("Flexible");
            } else {
                maint_bh_hour.setText(currentData.getHours());

            }

            cv_maintenancebhistory.setOnClickListener(v -> {

                if (isCurrent && currentData.getIs_cancel().equalsIgnoreCase("yes")) {


                } else if (isCurrent && currentData.getIs_cancel().equalsIgnoreCase("no")) {
                    new AlertDialog.Builder(cv_maintenancebhistory.getContext())
                            .setTitle("Cancel Booking")
                            .setMessage("Are you sure? You want to cancel the booking.")

                            .setNegativeButton("Cancel", (dialog1, which) -> {

                            })
                            .setPositiveButton("Yes", (dialog1, which) -> {

                                Intent intent = new Intent(cv_maintenancebhistory.getContext(), CancelActivity.class);
                                intent.putExtra("booking_id", currentData.getReference_id());

                                cv_maintenancebhistory.getContext().startActivity(intent);

                            }).show();
                }

            });

            if (isCurrent) {
                maint_bh_pay_button.setVisibility(View.INVISIBLE);
            } else {
                if (currentData.getIs_cancel().equalsIgnoreCase("yes")) {

                    maint_bh_pay_button.setVisibility(View.INVISIBLE);

                } else {

                    maint_bh_pay_button.setVisibility(View.VISIBLE);

                }
            }

            status.setText(currentData.getStatus());


        }
    }
}
