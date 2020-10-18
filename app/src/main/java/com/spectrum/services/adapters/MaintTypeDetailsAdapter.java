package com.spectrum.services.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.models.drawer.ServiceDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 18/06/18.
 */
public class MaintTypeDetailsAdapter extends RecyclerView.Adapter<MaintTypeDetailsAdapter.DataHolder> {
    private ArrayList<ServiceDetails.InstructionData> data;
    private Context context;


    public MaintTypeDetailsAdapter(ArrayList<ServiceDetails.InstructionData> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adaper_maint_type_details,parent,false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        holder.setData(data.get(position));
//        if (position==1){
//            holder.title.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            holder.title.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DataHolder extends  RecyclerView.ViewHolder{

        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.recycler_inner)
        RecyclerView recyclerView;
        public DataHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(ServiceDetails.InstructionData item){
            title.setText(item.getTitle());
            InnerAdapter adaptern=new InnerAdapter(item.getDetailsList());
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adaptern);


        }
    }

    class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.InnerHolderData> {
        ArrayList<String> dataList;

        public InnerAdapter(ArrayList<String> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public InnerHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_inner,parent,false);
            return new InnerHolderData(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InnerHolderData holder, int position) {
            holder.setData(dataList.get(position));

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class InnerHolderData extends RecyclerView.ViewHolder{
            @BindView(R.id.tv_item)
            TextView item;
            public InnerHolderData(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }

            public void setData(String data){
                item.setText(data);
            }
        }
    }
}
