package com.spectrum.services.sms;

/**
 * Created by Abins Shaji on 22/09/17.
 */

public interface SmsListener {
    public void messageReceived(String messageText);
}
