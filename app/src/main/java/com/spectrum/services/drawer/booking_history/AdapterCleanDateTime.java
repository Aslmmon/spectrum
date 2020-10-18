package com.spectrum.services.drawer.booking_history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.models.BookingHistoryModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 03/04/18.
 */
public class AdapterCleanDateTime extends RecyclerView.Adapter<AdapterCleanDateTime.HolderData> {

    Context context;
    List<BookingHistoryModel.CurrentData.Shifts> shifts;

    public AdapterCleanDateTime(Context context, List<BookingHistoryModel.CurrentData.Shifts> shifts) {
        this.context = context;
        this.shifts = shifts;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_cleanbh_datetime,parent,false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        holder.setData(shifts.get(position));

    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    public class HolderData extends RecyclerView.ViewHolder
    {
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.tv_status)
        TextView status;
        public HolderData(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(BookingHistoryModel.CurrentData.Shifts data)
        {
            date.setText(data.getDate());
            time.setText(data.getTime());
            status.setText(data.getStatus());
        }
    }

}
