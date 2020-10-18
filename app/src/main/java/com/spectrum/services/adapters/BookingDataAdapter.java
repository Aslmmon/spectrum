package com.spectrum.services.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.models.CleaningBookModel;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abins Shaji on 05/02/18.
 */

public class BookingDataAdapter extends RecyclerView.Adapter<BookingDataAdapter.DataHolder> {
    List<CleaningBookModel> modelList=new ArrayList<>();
    Context context;
    String hours;

    public BookingDataAdapter( Context context,String hours) {
        this.modelList = modelList;
        this.context = context;
        this.hours=hours;
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_book_data,parent,false);

        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, final int position) {
        holder.setData(Shared.cleaningBookModelList.get(position),hours);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog(Shared.cleaningBookModelList.get(position),position);

            }
        });



    }
    private void deleteDateSelected(int position)
    {
        ((MyApplication)context.getApplicationContext()).bus().send(Shared.cleaningBookModelList.get(position));
        // modelList.remove(position);
        Shared.cleaningBookModelList.remove(position);
        //sendDeletedPosition(position);
        notifyDataSetChanged();

    }
    private void alertDialog(final CleaningBookModel model, final int pos)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Are you sure ?");
        builder.setMessage("Do you want to delete booking for date "+Utils.getDateFormat(model.getDate())+" "+model.getTime());
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDateSelected(pos);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }


    @Override
    public int getItemCount() {
        return Shared.cleaningBookModelList.size();
    }



    public class DataHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_date)
        TextView date;
        @BindView(R.id.tv_time)
        TextView time;
        @BindView(R.id.iv_delete)
        ImageView delete;
        public DataHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }
        public void setData(CleaningBookModel data,String hours)
        {
            date.setText(Utils.getDateFormat(data.getDate()));
            time.setText(data.getTime()+" - "+getEndTime(data.getTime(),hours).toLowerCase());

        }
    }


    public String getEndTime(String start,String hours) {

        int int_hour=Integer.parseInt(hours);
        SimpleDateFormat format=new SimpleDateFormat("h:mm a");
        Date date= null;

        try {
            date = format.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY,int_hour);

       SimpleDateFormat primtformat=new SimpleDateFormat("h:mm a");
       return primtformat.format(calendar.getTime());


    }
}
