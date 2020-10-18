package com.spectrum.services.booking.cleaning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.spectrum.services.BaseActivity;
import com.spectrum.services.R;
import com.spectrum.services.adapters.BookingDataAdapter;
import com.spectrum.services.auth.LoginActivity;
import com.spectrum.services.models.CleanExtraService;
import com.spectrum.services.models.CleaningBookModel;
import com.spectrum.services.models.MainCleanBookModel;
import com.spectrum.services.models.PriceRequestModel;
import com.spectrum.services.models.PriceResponseModel;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.profile.ProfileActivity;
import com.spectrum.services.utils.EventDecorator;
import com.spectrum.services.utils.MyApplication;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Shared;
import com.spectrum.services.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static com.spectrum.services.utils.Shared.cleaningBookModelList;

public class BookCleaningDateActivity extends BaseActivity implements OnDateSelectedListener {
    public static final String TAG = "message";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_bar_price)
    ProgressBar progressbar_price;
    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;
    @BindView(R.id.tv_book_date_number_hour)
    TextView book_hour;
    @BindView(R.id.tv_book_date_number_maids)
    TextView book_maid;
    @BindView(R.id.tv_book_date_number_extra_service)
    TextView extra_service;
    @BindView(R.id.tv_book_date_number_type_service)
    TextView type_service;
    @BindView(R.id.button_once)
    Button button_once;
    @BindView(R.id.button_schedule)
    Button button_schedule;
    @BindView(R.id.recycler_book_data)
    RecyclerView recycler_book_data;
    @BindView(R.id.scroll_booking_date)
    NestedScrollView scrollView;

    @BindView(R.id.tv_booking_charge_per)
    TextView booking_charge_per;
    @BindView(R.id.tv_booking_total_amount)
    TextView booking_total_amount;
    @BindView(R.id.et_promocode)
    EditText promocode;
    @BindView(R.id.tv_booking_promo_amount)
    TextView promocode_amount;
    @BindView(R.id.tv_booking_vat_amount)
    TextView booking_vat_amount;
    @BindView(R.id.tv_vat_label)
    TextView booking_vat_label;
    @BindView(R.id.tv_booking_service_amount)
    TextView booking_service_amount;

    AlertDialog alertDialog;


    private CompositeDisposable disposable = new CompositeDisposable();
    private EventDecorator eventDecorator = new EventDecorator();
    BookingDataAdapter bookingDataAdapter;
    Boolean booking_type = true;
    private BookCleanViewModel viewModel;
    private MainCleanBookModel basicbookModel;
    private String strBasicBookData;
    private PriceResponseModel priceData;


    private Prefs prefs;
    private UserModel.User userModel;

    private Calendar calendar;


    private final String AMOUNTPERSERVICE = "AED 50.00/HR";
    private String base_price, applied_coupen_val = "";
    private Boolean is_promo_applied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cleaning_date);
        ButterKnife.bind(this);
        setupToolbar();
        initSome();
        //getAllFridays();
        initBookDataRecycler();
        listenersForObservables();
        logSubscribeEvent("order","USD",210);
    }

    private void setupToolbar() {
        toolbar.setTitle("Cleaning Booking");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private void initSome() {

        //view model reference
        viewModel = ViewModelProviders.of(this).get(BookCleanViewModel.class);

        prefs = Prefs.with(this);


        //setBasic book data
        setBasicBookDetails();


        //defult booking type-once
        button_once.setTextColor(getResources().getColor(android.R.color.white));
        button_once.setBackground(getResources().getDrawable(R.drawable.rect_bg_green_oneside));

        //calender stuff
        calendarView.setOnDateChangedListener(this);
        calendar = Calendar.getInstance();

        Calendar tomorrow_calender = Calendar.getInstance();
        tomorrow_calender.add(Calendar.DAY_OF_YEAR, 1);


        Calendar instance2 = Calendar.getInstance();
        instance2.set(instance2.get(Calendar.YEAR) + 1, Calendar.DECEMBER, 31);

        calendarView.state().edit()
                .setMinimumDate(calendar)
                .setFirstDayOfWeek(Calendar.SATURDAY)
                .setMaximumDate(instance2.getTime())
                .commit();
        Log.e(TAG, "" + calendar.get(Calendar.DAY_OF_MONTH));


        if (Utils.getDay(calendar.getTimeInMillis()).equalsIgnoreCase("Fri")) {
            calendarView.setDateSelected(tomorrow_calender.getTime(), true);

        } else {
            calendarView.setDateSelected(calendar.getTime(), true);
        }


//        calendarView.addDecorator(eventDecorator);


        //scolling
        scrollView.setSmoothScrollingEnabled(true);

        //clear bookeddata list
        cleaningBookModelList.clear();

        progressbar_price.setVisibility(View.GONE);

        //select date and show popup
        //showTimePopupDefault();

        Toast.makeText(this, "Select the date to see the available time", Toast.LENGTH_LONG).show();

    }

    private void showTimePopupDefault() {

        SimpleDateFormat format = new SimpleDateFormat("hh.mm a");
        Calendar cal = Calendar.getInstance();

        Date now;
        try {
            String am_pm;
            if (cal.get(Calendar.AM_PM) == 0) {
                am_pm = "am";
            } else {
                am_pm = "pm";

            }

            now = format.parse(cal.get(Calendar.HOUR) + "." + cal.get(Calendar.MINUTE) + " " + am_pm);
            Log.e(TAG, "initSome: " + now);

            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || now.after(format.parse("06.00 pm"))) {
                Log.e(TAG, "initSome: into if");
                calendar.add(Calendar.DATE, 1);
            } else {
                calendar.add(Calendar.DATE, 0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        onDateSelected(calendarView, CalendarDay.from(calendar), false);

    }

    private void priceCalcApi() {
        priceData = null;
        progressbar_price.setVisibility(View.VISIBLE);
        booking_total_amount.setVisibility(View.INVISIBLE);
        booking_vat_amount.setVisibility(View.INVISIBLE);
        booking_service_amount.setVisibility(View.INVISIBLE);
        promocode_amount.setVisibility(View.INVISIBLE);
        disposable.add(viewModel.getPriceDetailApi(setupPriceRequestModel())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(priceResponseModel -> priceResponseModel)
                .subscribe(this::handlePriceResponce, throwable -> {
                    progressbar_price.setVisibility(View.GONE);
                    booking_total_amount.setVisibility(View.INVISIBLE);
                    booking_vat_amount.setVisibility(View.INVISIBLE);
                    booking_service_amount.setVisibility(View.INVISIBLE);
                    promocode_amount.setVisibility(View.INVISIBLE);
                    Snackbar.make(progressbar_price, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", view -> priceCalcApi())
                            .show();
                }));
    }

    private PriceRequestModel setupPriceRequestModel() {
        ArrayList<String> extraServiceId = new ArrayList<>();
        for (CleanExtraService.ExtraServiceData data : basicbookModel.getCleanExtraServices()) {
            extraServiceId.add(data.getExtra_service_id());
        }
        int size;
        if (cleaningBookModelList.size() < 1) {
            size = 1;
        } else {
            size = cleaningBookModelList.size();
        }

        PriceRequestModel model = new PriceRequestModel(prefs.getUserid() != null ? prefs.getUserid() : "", basicbookModel.getNo_hours(), basicbookModel.getNo_maids(),
                basicbookModel.getWant_materials().equalsIgnoreCase("true") ? "Y" : "N", basicbookModel.getType_service_id(), "" + size,
                promocode.getText().toString().isEmpty() ? "" : promocode.getText().toString(), extraServiceId,
                cleaningBookModelList.size() > 0 ? cleaningBookModelList.get(0).getDate() : getDayfromDate(Calendar.getInstance().getTime()));
        return model;
    }

    private void handlePriceResponce(PriceResponseModel model) {
        if (model.getStatus().equalsIgnoreCase("success")) {
            priceData = model;
            progressbar_price.setVisibility(View.GONE);
            booking_total_amount.setVisibility(View.VISIBLE);
            booking_service_amount.setVisibility(View.VISIBLE);
            booking_vat_amount.setVisibility(View.VISIBLE);
            promocode_amount.setVisibility(View.VISIBLE);

            Log.e(TAG, "handlePriceResponce: " + model.getPrice().getGross_amount());
            booking_vat_label.setText("VAT " + model.getPrice().getVat_percentage() + "%");
            booking_service_amount.setText("AED " + model.getPrice().getService_rate());
            booking_charge_per.setText("AED " + model.getPrice().getHour_rate() + " /HR");
            booking_vat_amount.setText("AED " + model.getPrice().getVat_charge());
            booking_total_amount.setText("AED " + priceData.getPrice().getGross_amount());
            promocode_amount.setText("- AED " + model.getPrice().getDiscount());

            //promocode conditions

            if (!promocode.getText().toString().isEmpty()) {


                if (!is_promo_applied) {
                    if (model.getCoupen_status().equalsIgnoreCase("applied") && model.getPrice().getCoupen_val().equals(promocode.getText().toString())) {
                        is_promo_applied = true;
                        applied_coupen_val = promocode.getText().toString();

                        Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();
                    } else if (model.getCoupen_status().equalsIgnoreCase("not")) {
                        Log.e(TAG, "handlePriceResponce:in not ");
                        is_promo_applied = false;
                        applied_coupen_val = "";
                        promocode.setText("");
                        Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }

                } else if (model.getCoupen_status().equalsIgnoreCase("applied") && !applied_coupen_val.equalsIgnoreCase(model.getPrice().getCoupen_val())) {
                    is_promo_applied = true;
                    applied_coupen_val = promocode.getText().toString();
                    Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();
                } else if (model.getCoupen_status().equalsIgnoreCase("not")) {
                    Log.e(TAG, "handlePriceResponce:in not ");
                    is_promo_applied = false;
                    applied_coupen_val = "";
                    promocode.setText("");
                    Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();
                }

            } else if (model.getCoupen_status().equalsIgnoreCase("applied")) {
                is_promo_applied = true;
                applied_coupen_val = model.getPrice().getCoupen_val();
                promocode.setText(applied_coupen_val);
                Snackbar.make(progressbar_price, model.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    @OnClick(R.id.promo_apply_click)
    public void coupenCodeApply(View view) {
        priceCalcApi();
        hideKeyboard();
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void setBasicBookDetails() {
        strBasicBookData = getIntent().getStringExtra("data");
        priceData = Utils.convertStringToModel(getIntent().getStringExtra("price"), PriceResponseModel.class);

        basicbookModel = Utils.convertStringToModel(strBasicBookData, MainCleanBookModel.class);
        book_hour.setText(basicbookModel.getNo_hours() + " hours");
        book_maid.setText(basicbookModel.getNo_maids() + " maids");
        type_service.setText(basicbookModel.getType_service());
        String cleaning_material = basicbookModel.getWant_materials().equalsIgnoreCase("true") ? "Yes" : "No";
        extra_service.setText(cleaning_material);

        //Price
        base_price = priceData.getPrice().getService_rate();
        booking_vat_label.setText("VAT " + priceData.getPrice().getVat_percentage() + "%");
        booking_charge_per.setText("AED " + priceData.getPrice().getHour_rate() + "/HR");
        booking_total_amount.setText("AED " + priceData.getPrice().getGross_amount());
        booking_vat_amount.setText("AED " + priceData.getPrice().getVat_charge());
        booking_service_amount.setText("AED " + priceData.getPrice().getService_rate());
        promocode_amount.setText("- AED " + priceData.getPrice().getDiscount());

        if (priceData.getCoupen_status().equalsIgnoreCase("applied")) {
            if (priceData.getPrice().getCoupen_val() != null && !priceData.getPrice().getCoupen_val().equalsIgnoreCase("")) {
                promocode.setText(priceData.getPrice().getCoupen_val());
                is_promo_applied = true;
                applied_coupen_val = priceData.getPrice().getCoupen_val();
            }
        }
    }

    @OnClick(R.id.rect_bookingdate_next_click)
    public void nextClick() {
        if (Shared.cleaningBookModelList.size() < 1) {
            Snackbar.make(progressbar_price, "Choose Booking Date and Time ", Snackbar.LENGTH_SHORT).show();

        } else if (priceData == null) {
            Snackbar.make(progressbar_price, "Price Calculating..please wait", Snackbar.LENGTH_SHORT).show();
            priceCalcApi();

        } else if (validUserData()) {
            Intent intent = new Intent(BookCleaningDateActivity.this, BookCleanConfirmActivity.class);
            intent.putExtra("basicData", strBasicBookData);
            intent.putExtra("priceData", Utils.convertmodelToString(priceData));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }

    }

    private Boolean validUserData() {
        //user details
        userModel = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
        if (prefs.getSignedIn()) {
            if ((userModel.getArea_id() == null) || userModel.getArea_id().equalsIgnoreCase("")
                    || userModel.getPhone() == null || userModel.getPhone().equalsIgnoreCase("")) {

                alertDialog();
                return false;
            }

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("from", "reviewconfirm");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return false;

        }
        return true;
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookCleaningDateActivity.this);
        builder.setTitle("Alert");
        builder.setMessage("Update profile details to complete booking");
        builder.setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("from", "reviewconfirm");
                    dialog.dismiss();
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void initBookDataRecycler() {
        recycler_book_data.setLayoutManager(new LinearLayoutManager(this));
        if (bookingDataAdapter == null) {
            bookingDataAdapter = new BookingDataAdapter(this, basicbookModel.getNo_hours());
            recycler_book_data.setAdapter(bookingDataAdapter);
            bookingDataAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
        super.onBackPressed();
    }

    public void onceBookDateClick(View view) {

        button_once.setTextColor(getResources().getColor(android.R.color.white));
        button_once.setBackground(getResources().getDrawable(R.drawable.rect_bg_green_oneside));

        button_schedule.setTextColor(getResources().getColor(R.color.blur_cprimary));
        button_schedule.setBackground(getResources().getDrawable(R.drawable.rect_bg_white_oneside));
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        booking_type = true;
    }

    public void scheduleBookDateClick(View view) {
        button_schedule.setTextColor(getResources().getColor(android.R.color.white));
        button_schedule.setBackground(getResources().getDrawable(R.drawable.rect_bg_green_oneside));

        button_once.setTextColor(getResources().getColor(R.color.blur_cprimary));
        button_once.setBackground(getResources().getDrawable(R.drawable.rect_bg_white_oneside));
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        booking_type = false;

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        BookCleanTimeFrag cleanTimeFrag = BookCleanTimeFrag.newInstance(getDayfromDate(date.getDate()), basicbookModel.getNo_hours());
        cleanTimeFrag.show(getFragmentManager(), "time_fragment");

        calendarView.setDateSelected(date, false);
    }

    private String getDayfromDate(Date date) {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");
        String date_str = sformat.format(date);
        Log.i("data clicked uba ",date_str);
        /**
         * check if chosen date is the same date :
         *
         */


        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = Calendar.getInstance().getTime();
        String formattedTodayDate = curFormater.format(todayDate);
        if(formattedTodayDate.equals(date_str)){
            Calendar c = Calendar.getInstance();
            c.setTime(todayDate);
            c.add(Calendar.DATE, 1);
            Date nextDate = c.getTime();
            date_str = curFormater.format(nextDate);
            // Log.i("data","next Day needed"+nextDay);
        }
        /**
         * End of Check ..
         */
        return date_str;
    }



    private void listenersForObservables() {
        disposable.add(((MyApplication) getApplication())
                .bus()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof BookCleanTimeFrag.EventTypeOne) {
                        CleaningBookModel model = new CleaningBookModel(((BookCleanTimeFrag.EventTypeOne) o).date, ((BookCleanTimeFrag.EventTypeOne) o).time);

                        addDateSelection(model);
                        selectDateCalender(model.getDate());
                        priceCalcApi();

                    } else if (o instanceof Integer) {

                    } else if (o instanceof CleaningBookModel) {
                        CleaningBookModel model = new CleaningBookModel(((CleaningBookModel) o).getDate(), ((CleaningBookModel) o).getTime());

                        if (checkDuplicateDate(model)) {

                            deselectDateCalender(((CleaningBookModel) o).getDate());
                            priceCalcApi();

                        }

                    } else if (o instanceof String) {
                        if (((String) o).equalsIgnoreCase("kill")) {
                            this.finish();
                        }
                        if (((String) o).equalsIgnoreCase("reviewconfirm")) {
                            nextClick();

                        }
                    }
                }));

    }

    private void addDateSelection(CleaningBookModel model) {

        if (booking_type) {
            cleaningBookModelList.clear();
        }
        if (checkforDuplicateBooking(model)) {
            cleaningBookModelList.add(model);
            Log.e(TAG, "accept: " + cleaningBookModelList.size());
            recycler_book_data.setAdapter(new BookingDataAdapter(BookCleaningDateActivity.this, basicbookModel.getNo_hours()));
            bookingDataAdapter.notifyDataSetChanged();


            Completable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            scrollView.smoothScrollTo(0, scrollView.getBottom());
                        }
                    });

        } else {
            alertDialog(model);
        }
    }

    private boolean checkforDuplicateBooking(CleaningBookModel model) {
        for (CleaningBookModel dummy : cleaningBookModelList) {
            if (dummy.getDate().equalsIgnoreCase(model.getDate()) && dummy.getTime().equalsIgnoreCase(model.getTime())) {
                return false;
            }
        }

        return true;
    }

    private boolean checkDuplicateDate(CleaningBookModel model) {
        for (CleaningBookModel dummy : cleaningBookModelList) {
            if (dummy.getDate().equalsIgnoreCase(model.getDate())) {
                return false;
            }
        }

        return true;
    }

    private void deselectDateCalender(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendarView.setDateSelected(format.parse(date), false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void selectDateCalender(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendarView.setDateSelected(format.parse(date), true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void alertDialog(final CleaningBookModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookCleaningDateActivity.this);
        builder.setTitle("Alert");
        builder.setMessage("You already booked for date " + Utils.getDateFormat(model.getDate()) + " " + model.getTime());
        builder.setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        super.onStop();
    }


}
