package com.spectrum.services.drawer.faq;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.R;
import com.spectrum.services.models.drawer.FaqModel;
import com.spectrum.services.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqActivity extends AppCompatActivity {
    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_faq)
    RecyclerView recycler_faq;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    ProgressDialog progressDialog;

    private FaqAdapter faqAdapter;
    private List<FaqModel.Faq_data>faq_data=new ArrayList<>();

    public static int pos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        setupToolbar();
        //initProgressDilaog();
        initSome();
        initRecycler();
        getFaqData();

    }
    private void initSome()
    {

    }
    private void initRecycler()
    {
        recycler_faq.setLayoutManager(new LinearLayoutManager(this));
        if(faqAdapter==null)
        {
            faqAdapter=new FaqAdapter(this,faq_data);
            recycler_faq.setAdapter(faqAdapter);
        }

    }
    private void initProgressDilaog()
    {
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
    }

    private void getFaqData()
    {
        progressBar.setVisibility(View.VISIBLE);
        Utils.getApiService().getFaq()
                .enqueue(new Callback<FaqModel>() {
                    @Override
                    public void onResponse(Call<FaqModel> call, Response<FaqModel> response) {
                        progressBar.setVisibility(View.GONE);
                        if(response.isSuccessful())
                        {
                            if (response.body().getStatus().equalsIgnoreCase("success"))
                            {
                                faq_data.addAll(response.body().getFaq_data());
                                faqAdapter=new FaqAdapter(FaqActivity.this,faq_data);
                                recycler_faq.setAdapter(faqAdapter);
                                faqAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: failure" );
                                 Snackbar.make(progressBar, response.message(), Snackbar.LENGTH_SHORT).show();



                            }
                        }
                        else
                        {

                            Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT)
                                    .setAction("Retry", v -> {
                                        getFaqData();
                                    })
                                    .show();

                        }
                    }

                    @Override
                    public void onFailure(Call<FaqModel> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);

                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_LONG)
                                .setAction("Retry", v -> {
                                    getFaqData();
                                })
                                .show();


                    }
                });
    }



    private void setupToolbar() {
        toolbar.setTitle("Faq");
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
