package com.spectrum.services;

import android.app.Application;
import android.os.Bundle;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import java.math.BigDecimal;
import java.util.Currency;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    AppEventsLogger logger;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = AppEventsLogger.newLogger(getApplicationContext());

    }



    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logViewContentEvent(String contentType, String contentData, String contentId, String currency, double price) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, price, params);
    }


    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logContactEvent() {
        /**
         * contact
         */
        logger.logEvent(AppEventsConstants.EVENT_NAME_CONTACT);


    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logSubscribeEvent(String orderId, String currency, double price) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_ORDER_ID, orderId);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SUBSCRIBE, price, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logInitiateCheckoutEvent(String contentData, String contentId, String contentType, int numItems, boolean paymentInfoAvailable, String currency, double totalPrice) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putInt(AppEventsConstants.EVENT_PARAM_PAYMENT_INFO_AVAILABLE, paymentInfoAvailable ? 1 : 0);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, totalPrice, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logScheduleEvent() {
        logger.logEvent(AppEventsConstants.EVENT_NAME_SCHEDULE);
    }
    /**
     * logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     * purchaseAmount is BigDecimal, e.g. BigDecimal.valueOf(4.32).
     * currency is Currency, e.g. Currency.getInstance("USD"),
     *     where the string is from http://en.wikipedia.org/wiki/ISO_4217.
     * parameters is Bundle.
     */
    public void logpurchase() {
        logger.logPurchase(BigDecimal.valueOf(22.5), Currency.getInstance("ASD"));
    }



}
