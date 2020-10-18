package com.spectrum.services.drawer.faq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;
import com.spectrum.services.models.drawer.FaqModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.spectrum.services.drawer.faq.FaqActivity.pos;

/**
 * Created by Abins Shaji on 16/02/18.
 */

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqData> {

    private Context context;
    private List<FaqModel.Faq_data> faq_data = new ArrayList<>();

    public FaqAdapter(Context context, List<FaqModel.Faq_data> faq_data) {
        this.context = context;
        this.faq_data = faq_data;
    }

    @Override
    public FaqData onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_faq, parent, false);
        return new FaqData(view);
    }

    @Override
    public void onBindViewHolder(FaqData holder, final int position) {
        holder.setData(faq_data.get(position));



        if (position == pos) {
            holder.answer.setVisibility(View.VISIBLE);
            holder.expand_click.setRotation(-180);

        }
        else
        {
            holder.answer.setVisibility(View.GONE);
            //holder.expand_click.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand_more_black_24dp));
            holder.expand_click.setRotation(360);
        }


        holder.expand_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = position;
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return faq_data.size();
    }

    public class FaqData extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_faq_expand_click)
        ImageView expand_click;
        @BindView(R.id.tv_faq_question)
        TextView quest;
        @BindView(R.id.tv_faq_answer)
        TextView answer;

        public FaqData(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(FaqModel.Faq_data data) {
            answer.setText(data.getAnswer());
            quest.setText(data.getQuestion());

        }
    }
}
