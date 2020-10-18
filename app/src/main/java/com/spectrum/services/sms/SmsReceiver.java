package com.spectrum.services.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


/**
 * Created by Abins Shaji on 21/08/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Bundle data = intent.getExtras();
            Object[] pdus = (Object[]) data.get("pdus");
//            for (int i = 0; i < pdus.length; i++) {
            for (Object aPdusObj : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                String sender = smsMessage.getDisplayOriginatingAddress();
                String messageBody = smsMessage.getDisplayMessageBody();

                Log.e("spectrum", "Received SMS: " + messageBody + ", Sender: " + sender);

                if (!messageBody.contains("Spectrum")) { // if the SMS is not from our gateway, ignore the message
                    return;
                }
                else
                {
                    String verificationCode = messageBody.replaceAll("[^0-9]", "");
                    mListener.messageReceived(verificationCode.trim());

                    Log.e("spectrum", "OTP received: " + verificationCode);

                }

//                if (messageBody.endsWith("ABI")) b = true;
//
//                abcd = messageBody.replaceAll("[^0-9]", "");
//
//
//                if (b) {
//                    Log.e("otpon", "" + abcd);
//                    mListener.messageReceived(abcd);
//
//
//                } else {
//                    Log.e("fullmessaage", "" + messageBody);
//                }

                // verification code from sms
//                String verificationCode = getVerificationCode(messageBody);
//                mListener.messageReceived(verificationCode.trim());
//
//                Log.e("spectrum", "OTP received: " + verificationCode);

            }
        } catch (Exception e) {
            Log.e("spectrum", "Exception: " + e.getMessage());
        }

    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(":");

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
