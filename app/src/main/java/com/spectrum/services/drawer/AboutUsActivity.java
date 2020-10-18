package com.spectrum.services.drawer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spectrum.services.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.data_basic)
    TextView content_main;
    @BindView(R.id.read_more)
    TextView read_more;
    @BindView(R.id.arrow)
    ImageView arrow;

    @BindView(R.id.tv_vision_content)
    TextView vision_content;
    @BindView(R.id.tv_mission_content)
    TextView mission_content;
    @BindView(R.id.tv_value_content)
    TextView values_content;

    private boolean f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initSome();
        setupToolbar();


    }

    private void initSome()
    {
        f = false;

        vision_content.setVisibility(View.GONE);
        vision_content.setActivated(false);
        mission_content.setVisibility(View.GONE);
        mission_content.setActivated(false);
        values_content.setVisibility(View.GONE);
        values_content.setActivated(false);


    }

    private void setupToolbar() {
        toolbar.setTitle("About us");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onBackPressed();
    }


    @OnClick(R.id.linear_read_more_click)public void readMoreClick(View view)
    {
        if (f) {
            content_main.setText(getResources().getString(R.string.about_us_content_one));
            f = false;
            read_more.setText("Read more");
            arrow.setRotation(0);
            return;
        } else {
            content_main.setText(getResources().getString(R.string.about_us_content_full));
            f = true;
            read_more.setText("Read less");
            arrow.setRotation(-90);
        }

    }
    @OnClick(R.id.arrow_vision)public void expandVision(View view)
    {
        if(!vision_content.isActivated())
        {
            vision_content.setVisibility(View.VISIBLE);
            vision_content.setActivated(true);
            view.setRotation(180);
        }
        else
        {
            vision_content.setVisibility(View.GONE);
            vision_content.setActivated(false);
            view.setRotation(360);
        }


    }

    @OnClick(R.id.arrow_misson)public void expandMission(View view)
    {
        if(!mission_content.isActivated())
        {
            mission_content.setVisibility(View.VISIBLE);
            mission_content.setActivated(true);
            view.setRotation(180);
        }
        else
        {
            mission_content.setVisibility(View.GONE);
            mission_content.setActivated(false);
            view.setRotation(360);
        }

    }
    @OnClick(R.id.arrow_values)public  void expandValues(View view)
    {
        if(!values_content.isActivated())
        {
            values_content.setVisibility(View.VISIBLE);
            values_content.setActivated(true);
            view.setRotation(180);
        }
        else
        {
            values_content.setVisibility(View.GONE);
            values_content.setActivated(false);
            view.setRotation(360);
        }

    }
}

