package com.spectrum.services.drawer.services;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.spectrum.services.R;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 19/02/18.
 */

public class DetailServiceFrag extends DialogFragment {

    @BindView(R.id.iv_dservice_frag_close)
    ImageView close;
    @BindView(R.id.iv_dservice_frag_image)
    ImageView service_image;
    @BindView(R.id.tv_dservice_title)
    TextView service_title;
    @BindView(R.id.tv_dservice_frag_details)
    TextView service_details;

    ServiceDetails.ServiceData modeldata;
    Boolean type;//true for cleaning



    static DetailServiceFrag instance(String data,Boolean type)
    {
        DetailServiceFrag serviceFrag=new DetailServiceFrag();
        Bundle bundle=new Bundle();
        bundle.putString("data",data);
        bundle.putBoolean("type",type);
        serviceFrag.setArguments(bundle);
        return serviceFrag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View  view=inflater.inflate(R.layout.frag_detail_service,container,false);
        ButterKnife.bind(this,view);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        populatelayout();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,0);

       modeldata = new Gson().fromJson(getArguments().getString("data"),ServiceDetails.ServiceData.class);
       type=getArguments().getBoolean("type");


    }

    private void populatelayout()
    {
        if (type)
        {
            service_title.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }
        else
        {
            service_title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

        }
        Glide.with(this)
                .load(modeldata.getImage())
                .apply(RequestOptions.centerCropTransform())
                .into(service_image);

        service_title.setText(modeldata.getService());
        service_details.setText(modeldata.getDetails());
    }





}
