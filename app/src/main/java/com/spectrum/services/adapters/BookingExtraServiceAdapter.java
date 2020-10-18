package com.spectrum.services.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.booking.cleaning.BookCleaningActivity;
import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.utils.Shared;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 01/02/18.
 */

public class BookingExtraServiceAdapter extends RecyclerView.Adapter<BookingExtraServiceAdapter.DataExtraService> {
    private Context context;
    private Boolean ticked = false;
    private List<CleanExtraService.ExtraServiceData> extraServiceData;
    private InterfaceExtraServiceData interfaceServiceData;

    public BookingExtraServiceAdapter(Context context, List<CleanExtraService.ExtraServiceData> extraServiceData) {

        this.context = context;
        this.extraServiceData = Shared.listSelectedExtraSErvice;
        this.interfaceServiceData=(BookCleaningActivity)context;
    }

    @Override
    public DataExtraService onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_extra_service_layout, parent, false);

        return new DataExtraService(view);
    }

    @Override
    public void onBindViewHolder(final DataExtraService holder, int position) {
        CleanExtraService.ExtraServiceData data=extraServiceData.get(position);
        holder.setData(data);

        holder.tick.setBackground(data.getIs_selected()?context.getResources().getDrawable(R.drawable.round_bg_green):
                context.getResources().getDrawable(R.drawable.round_bg_grey));


        holder.relative_main.setOnClickListener(view -> {
            data.setIs_selected(!data.getIs_selected());
            holder.tick.setBackground(data.getIs_selected()?context.getResources().getDrawable(R.drawable.round_bg_green):
                    context.getResources().getDrawable(R.drawable.round_bg_grey));
            interfaceServiceData.extraService();


        });

    }

    @Override
    public int getItemCount() {
        return extraServiceData.size();
    }

    public class DataExtraService extends RecyclerView.ViewHolder {
        @BindView(R.id.relative_extra_service)
        RelativeLayout relative_main;
        @BindView(R.id.tv_extra_service_amount)
        TextView amount;
        @BindView(R.id.tv_extra_service_name)
        TextView name;
        @BindView(R.id.iv_extra_service_image)
        ImageView item_image;
        @BindView(R.id.iv_extra_service_tick)
        ImageView tick;

        public DataExtraService(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(CleanExtraService.ExtraServiceData data) {
            amount.setText(data.getExtra_service_amount());
            name.setText(data.getExtra_service_name());
            Glide.with(context)
                    .load(data.getExtra_service_image())
                    .into(item_image);
        }
    }

    public interface InterfaceExtraServiceData
    {
        void extraService();
    }
}
