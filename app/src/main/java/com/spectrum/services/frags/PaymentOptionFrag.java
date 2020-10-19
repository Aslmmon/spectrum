package com.spectrum.services.frags;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.spectrum.services.R;
import com.spectrum.services.booking.BookingStatusActivity;
import com.spectrum.services.booking.PaymentActivity;
import com.spectrum.services.booking.cleaning.BookCleanConfirmActivity;
import com.spectrum.services.booking.cleaning.BookCleanViewModel;
import com.spectrum.services.booking.cleaning.BookCleaningActivity;
import com.spectrum.services.models.CleanBookingRequestModel;
import com.spectrum.services.models.CleanBookingResponceModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Utils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Abins Shaji on 03/05/18.
 */
public class PaymentOptionFrag extends DialogFragment {
    public static final String TAG = "PaymentOptionFrag";
    private BookCleanViewModel viewModel;

    @BindView(R.id.relative_online_pay)
    RelativeLayout online_click;
    @BindView(R.id.relative_cash_pay)
    RelativeLayout cash_click;

    @BindView(R.id.tv_online_text)
    TextView text_online;
    @BindView(R.id.tv_cash_pay)
    TextView text_cash;
    @BindView(R.id.iv_fpwd_close)
    ImageView close;

    @BindView(R.id.relative_play_click)
    RelativeLayout relative_done;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    CleanBookingRequestModel requestModel;
    PriceResponseModel priceData;
    CompositeDisposable disposable = new CompositeDisposable();

    Boolean is_card_payment = false;

    public static PaymentOptionFrag initData(String data, String price) {
        PaymentOptionFrag frag = new PaymentOptionFrag();
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        bundle.putString("price", price);
        frag.setArguments(bundle);
        return frag;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_payment_options, container, false);
        ButterKnife.bind(this, view);
        onlineClick();
        return view;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(BookCleanViewModel.class);

        requestModel = Utils.convertStringToModel(getArguments().getString("data"), CleanBookingRequestModel.class);
        priceData = Utils.convertStringToModel(getArguments().getString("price"), PriceResponseModel.class);

        //  Log.e(TAG, "onCreate: " + new GsonBuilder().setPrettyPrinting().create().toJson(priceData));
        setStyle(android.app.DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        close.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });

        relative_done.setOnClickListener(view1 ->
        {
            ((BookCleanConfirmActivity) Objects.requireNonNull(getActivity())).logpurchase();
            ((BookCleanConfirmActivity) getActivity()).logInitiateCheckoutEvent("data", "id", "type", 1, true, "USD", 147);
            performBook(requestModel);
        });


    }

    public void performBook(CleanBookingRequestModel model) {
        if (model != null && priceData != null) {
            progressBar.setVisibility(View.VISIBLE);
            disposable.add(viewModel.bookClean(model)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleBookingResponce, throwable -> {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(progressBar, "Check your connection", Snackbar.LENGTH_SHORT).show();

                    }));
        }

    }

    private void handleBookingResponce(CleanBookingResponceModel model) {
        progressBar.setVisibility(View.GONE);
        if (model.getStatus() != null) {
            if (is_card_payment) {

                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra("amount", priceData.getPrice().getGross_amount());
                intent.putExtra("ref", model.getReference_id());
                intent.putExtra("vat", priceData.getPrice().getVat_percentage());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                getDialog().dismiss();
                ((MyApplication) getActivity().getApplication()).bus().send("kill");
            } else {
                Intent intent = new Intent(getActivity(), BookingStatusActivity.class);
//              intent.putExtra("paymentstatus", Utils.convertmodelToString(model));
//              intent.putExtra("paydetails", Utils.convertmodelToString(priceData));
                intent.putExtra("ref", model.getReference_id());
                intent.putExtra("amount", priceData.getPrice().getGross_amount());
                intent.putExtra("vat", priceData.getPrice().getVat_percentage());
                intent.putExtra("type", "clean");

                Log.e(TAG, "handleBookingResponce: " + priceData.getPrice().getVat_percentage());

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                getDialog().dismiss();

            }

        } else {
            Snackbar.make(progressBar, model.getMessage(), Snackbar.LENGTH_SHORT).show();
        }

    }


    @OnClick(R.id.relative_online_pay)
    public void onlineClick() {
        is_card_payment = true;
        text_online.setTextColor(getResources().getColor(android.R.color.white));
        online_click.setBackground(getResources().getDrawable(R.drawable.rect_bg_green_edge_round));

        text_cash.setTextColor(getResources().getColor(R.color.blur_cprimary));
        cash_click.setBackground(getResources().getDrawable(R.drawable.rect_bg_white));


        requestModel.setPayment_type("card");

    }

    @OnClick(R.id.relative_cash_pay)
    public void cashClick() {
        is_card_payment = false;
        text_cash.setTextColor(getResources().getColor(android.R.color.white));
        cash_click.setBackground(getResources().getDrawable(R.drawable.rect_bg_green_edge_round));

        text_online.setTextColor(getResources().getColor(R.color.blur_cprimary));
        online_click.setBackground(getResources().getDrawable(R.drawable.rect_bg_white));

        requestModel.setPayment_type("cash");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
