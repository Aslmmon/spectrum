package com.spectrum.services.drawer.services;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 14/02/18.
 */

public class DetailServiceAdapter extends RecyclerView.Adapter<DetailServiceAdapter.DetailHolder> {
    public static final String TAG = "message";
    private  List<ServiceDetails.ServiceData>serviceData=new ArrayList<>();
    private  Context context;
    public Boolean type; //true for cleaning
    private  String service_data;

    public DetailServiceAdapter(List<ServiceDetails.ServiceData> serviceData, Context context, Boolean type) {
        this.serviceData = serviceData;
        this.context = context;
        this.type = type;
    }

    @Override
    public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_detail_service,parent,false);
        return new DetailHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailHolder holder, final int position) {
        holder.setServiceData(serviceData.get(position));
        holder.relative_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_data= new Gson().toJson(serviceData.get(position));
                DetailServiceFrag serviceFrag= DetailServiceFrag.instance(service_data,type);
                serviceFrag.show(((Activity) context).getFragmentManager(),"service_detail_fragment");

            }
        });


    }

    @Override
    public int getItemCount() {

        return serviceData.size();
    }

    public class DetailHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.relative_detail_service)
        RelativeLayout relative_main;
        @BindView(R.id.iv_service_detail)
        ImageView image_view;
        @BindView(R.id.tv_service_detail)
        TextView title;

        public DetailHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        protected void setServiceData(ServiceDetails.ServiceData data)
        {
            if(type)
            {
                relative_main.setBackground(context.getResources().getDrawable(R.drawable.detail_service_bg));
                title.setTextColor(context.getResources().getColor(R.color.colorAccent));
            }
            else
            {
                relative_main.setBackground(context.getResources().getDrawable(R.drawable.detail_service_bg_maintenance));
                title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
            Glide.with(context)
                    .load(data.getImage())
                    .apply(RequestOptions.centerCropTransform())
                    .into(image_view);

            title.setText(data.getService());

        }
    }
}
