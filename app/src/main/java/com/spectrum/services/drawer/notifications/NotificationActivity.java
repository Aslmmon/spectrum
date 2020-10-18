package com.spectrum.services.drawer.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.spectrum.services.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_notification)
    RecyclerView recy_notification;
    @BindView(R.id.relative_error_no_item)
    RelativeLayout relative_error_no_item;
    @BindView(R.id.tv_error_message)
    TextView error_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setupToolbar();
        initSome();
    }

    private void initSome()
    {
        //error
        recy_notification.setVisibility(View.GONE);
        setupErrorLayout();

    }
    private void setupErrorLayout()
    {
        relative_error_no_item.setVisibility(View.VISIBLE);
        error_message.setText("No items found !");
    }
    private void setupToolbar() {
        toolbar.setTitle("Offers & Notifications");
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
}
