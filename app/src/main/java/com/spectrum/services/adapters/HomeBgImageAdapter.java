package com.spectrum.services.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.spectrum.services.R;

/**
 * Created by Abins Shaji on 30/01/18.
 */

public class HomeBgImageAdapter extends PagerAdapter {
    private Context context;
    private static final Intro[] intros = {new Intro("Flexibility & Convenience", "However, whenever and wherever you want\n" +
            "We're offering you a variety of services\n", R.drawable.intro_first),
            new Intro("Best customer service", "We assure you that we are going to help you\n" +
                    "and solve whatever issues you face\n", R.drawable.intro_second),
            new Intro("Trusted & verified Professionals", "All our Heroes are experts in their field, vetted and \n" +
                    "trained to provide you with the highest quality service\n", R.drawable.intro_third)};
    private static final Integer[] IMAGES = {R.drawable.intro_first, R.drawable.intro_second, R.drawable.intro_third};

    public HomeBgImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        View imageLayout = LayoutInflater.from(context).inflate(R.layout.home_bg_image_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);

        final TextView title = (TextView) imageLayout
                .findViewById(R.id.tv_title);

        final TextView subtitle = (TextView) imageLayout
                .findViewById(R.id.tv_subtitle);

        imageView.setImageResource(intros[position].getImage());
        title.setText(intros[position].getTitle());
        subtitle.setText(intros[position].getSubtitle());

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public int getCount() {
        return IMAGES.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }

}
