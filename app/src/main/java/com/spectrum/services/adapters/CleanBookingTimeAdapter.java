package com.spectrum.services.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.booking.cleaning.BookCleanTimeFrag;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 05/02/18.
 */

public class CleanBookingTimeAdapter extends RecyclerView.Adapter<CleanBookingTimeAdapter.TimeHolder> {
    public static final String TAG = "message";
    private Context context;
    private PassTime callbackPassTime;
    private List<String> bookTimeList;

    public CleanBookingTimeAdapter(Context context, PassTime callbackPassTime, List<String> bookTimeList) {

       BookCleanTimeFrag.pos=-1;
        this.context = context;
        this.callbackPassTime = callbackPassTime;
        this.bookTimeList = bookTimeList;

    }

    @Override
    public TimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_time_clean_booking, parent, false);
        return new TimeHolder(view);
    }

    @Override
    public void onBindViewHolder(final TimeHolder holder, final int position) {
        holder.time.setTextColor(context.getResources().getColor(R.color.greyseven));
        holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.rect_bg_white));

        if (position == BookCleanTimeFrag.pos) {
            holder.time.setTextColor(Color.WHITE);
            holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.rect_bg_green_edge_round));
            callbackPassTime.getTime(bookTimeList.get(position));

            //testing rxbus
            //((MyApplication)context.getApplicationContext()).bus().send(bookTimeList.get(position));
        }


        holder.relativeLayout.setOnClickListener(view -> {
            BookCleanTimeFrag.pos = position;
            notifyDataSetChanged();

        });
        //holder.time.setText("10:00 AM");
        holder.setData(bookTimeList.get(position));

    }


    @Override
    public int getItemCount() {
        return bookTimeList.size();
    }

    public class TimeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_time_adapter)
        TextView time;
        @BindView(R.id.relative_main_adapter)
        RelativeLayout relativeLayout;

        public TimeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String data) {
            time.setText(data);
        }
    }

    public interface PassTime {
        void getTime(String t);
    }


}
