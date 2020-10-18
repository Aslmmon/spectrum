package com.spectrum.services.drawer.service_area;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.models.drawer.ServiceAreaModel;
import com.spectrum.services.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 16/02/18.
 */

public class ServiceAreaAdapter extends RecyclerView.Adapter<ServiceAreaAdapter.ServiceAreaData> {

    private List<ServiceAreaModel.SAreaData> areaData = new ArrayList<>();
    private List<ServiceAreaModel.SAreaData> searchData = new ArrayList<>();
    private Context context;

    public ServiceAreaAdapter(List<ServiceAreaModel.SAreaData> areaData, Context context) {
        this.areaData = areaData;
        this.context = context;
        this.searchData.addAll(areaData);
    }

    @Override
    public ServiceAreaData onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_service_area, parent, false);
        return new ServiceAreaData(view);
    }

    @Override
    public void onBindViewHolder(ServiceAreaData holder, int position) {
        holder.setServiceAreaData(areaData.get(position));

    }

    @Override
    public int getItemCount() {
        return areaData.size();
    }


    public class ServiceAreaData extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_sarea_name)
        TextView name;
        @BindView(R.id.relative_service_area)
        RelativeLayout relative_service_area;

        public ServiceAreaData(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setServiceAreaData(final ServiceAreaModel.SAreaData sAreaData) {
            name.setText(sAreaData.getName());
            relative_service_area.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyApplication)context.getApplicationContext())
                            .bus()
                            .send(sAreaData);
                }
            });
        }
    }




    public void searchArea(String text) {
        areaData.clear();

        if (text.length() == 0) {
            areaData.addAll(searchData);
        } else {
            for (ServiceAreaModel.SAreaData sAreaData : searchData) {
                if (sAreaData.getName().toLowerCase().contains(text)) {

                    areaData.add(sAreaData);

                }
            }


        }
        notifyDataSetChanged();


    }
}
