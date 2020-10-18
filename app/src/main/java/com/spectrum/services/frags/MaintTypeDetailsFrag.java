package com.spectrum.services.frags;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.spectrum.services.R;
import com.spectrum.services.adapters.MaintTypeDetailsAdapter;
import com.spectrum.services.models.drawer.ServiceDetails;
import com.spectrum.services.utils.Utils;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 18/06/18.
 */
public class MaintTypeDetailsFrag extends DialogFragment {

    public static final String TAG = "MaintTypeDetailsFrag";
    @BindView(R.id.iv_close)
    ImageView close;
    @BindView(R.id.recycler_type)
    RecyclerView recyclerView;
    @BindView(R.id.tv_title)
    TextView title;

    ServiceDetails.ServiceData data;
    String main_title;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_maint_type_details,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    public static MaintTypeDetailsFrag init(String title,String listData){
        Bundle bundle=new Bundle();
        bundle.putString("title",title);
        bundle.putString("listData",listData);
        MaintTypeDetailsFrag frag=new MaintTypeDetailsFrag();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecycler();
        title.setText(main_title);
        close.setOnClickListener(view1 ->
        {
            getDialog().dismiss();
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,DialogFragment.STYLE_NORMAL);
        Bundle bundle=this.getArguments();
        data= Utils.convertStringToModel(bundle.getString("listData"), ServiceDetails.ServiceData.class);
        main_title=bundle.getString("title");
       // Log.e(TAG, "onCreate: "+ new GsonBuilder().setPrettyPrinting().create().toJson(data));



    }

    private void setupRecycler(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new MaintTypeDetailsAdapter(data.instructionData,getActivity()));


    }
}
