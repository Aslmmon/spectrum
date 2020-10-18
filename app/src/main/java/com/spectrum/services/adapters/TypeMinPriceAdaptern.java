package com.spectrum.services.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeMinPriceAdaptern extends  RecyclerView.Adapter<TypeMinPriceAdaptern.DataHolder> {
        List<String> stringList=new ArrayList<>();

        public TypeMinPriceAdaptern(List<String> stringList) {
            this.stringList = stringList;
        }

        @NonNull
        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_maint_type_price,parent,false);
            return  new DataHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DataHolder holder, int position) {
            holder.setData(stringList.get(position));

        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }
        public class DataHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.tv_item_name)
            TextView textView;
            public DataHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }

            public void setData(String data){
                textView.setText(data);

            }
        }
    }