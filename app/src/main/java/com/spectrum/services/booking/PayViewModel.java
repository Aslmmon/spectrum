package com.spectrum.services.booking;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.gson.JsonObject;
import com.spectrum.services.models.UserModel;
import com.spectrum.services.models.payfort.FortTokenResponce;
import com.spectrum.services.utils.Prefs;
import com.spectrum.services.utils.Utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abins Shaji on 02/04/18.
 */
public class PayViewModel extends AndroidViewModel {
    public static final String TAG = "message";
    private Prefs prefs;
    private String gross_amount, ref_id;
    UserModel.User user;

    //Statics
    private final static String MERCHANT_IDENTIFIER ="UoEcfagm";//"CKxScKab";// ;////
    private final static String ACCESS_CODE ="39wz0fGb0Ocgjg2qcMjA"; //"XIj8KVLKSPumGuyfnu6g";//;////;""
    private final static String SHA_TYPE = "SHA-256";
    private final static String SHA_REQUEST_PHRASE ="SPECIN";//"TESTSHAIN";// ;//""
    public final static String SHA_RESPONSE_PHRASE ="SPECOUT";//"TESTSHAOUT";//;//"SPECOUT"
    public final static String CURRENCY_TYPE = "AED";
    public final static String LANGUAGE_TYPE = "en";//Arabic - ar //English - en
    public final static String SDK_TOKEN = "SDK_TOKEN";

    //WS params
    private final static String KEY_MERCHANT_IDENTIFIER = "merchant_identifier";
    private final static String KEY_SERVICE_COMMAND = "service_command";
    private final static String KEY_DEVICE_ID = "device_id";
    private final static String KEY_LANGUAGE = "language";
    private final static String KEY_ACCESS_CODE = "access_code";
    private final static String KEY_SIGNATURE = "signature";

    //responce param
    private final static String RES_ACCESS_CODE = "access_code";
    private final static String RES_CODE = "response_code";
    private final static String RES_LAN = "language";
    private final static String RES_MER_IDENT = "merchant_identifier";
    private final static String RES_SDK = "sdk_token";
    private final static String RES_MESSAGE = "response_message";
    private final static String RES_SER_COMMAND = "service_command";
    private final static String RES_DEVICE_ID = "device_id";
    private final static String RES_STATUS = "status";
    private final static String RES_SIG = "signature";


    public PayViewModel(@NonNull Application application) {
        super(application);
        prefs = Prefs.with(getApplication());
    }

    public void initData(String amount, String id) {
        user = Utils.convertStringToModel(prefs.getUserDetails(), UserModel.User.class);
        gross_amount = amount;
        ref_id = id;


    }

    public Boolean checkShaResponce(FortTokenResponce responce) {
        String catStrg = SHA_RESPONSE_PHRASE +
                RES_ACCESS_CODE + "=" + responce.getAccess_code() +
                RES_DEVICE_ID + "=" + responce.getDevice_id() +
                RES_LAN + "=" + responce.getLanguage() +
                RES_MER_IDENT + "=" + responce.getMerchant_identifier() +
                RES_CODE + "=" + responce.getResponse_code() +
                RES_MESSAGE + "=" + responce.getResponse_message() +
                RES_SDK + "=" + responce.getSdk_token() +
                RES_SER_COMMAND + "=" + responce.getService_command() +
                RES_STATUS + "=" + responce.getStatus() +
                SHA_RESPONSE_PHRASE;

        Log.e(TAG, "checkShaResponce: calculated " + getSignatureSHA256(catStrg));
        Log.e(TAG, "checkShaResponce: incoming" + responce.getSignature());

        if (responce.getSignature().equals(getSignatureSHA256(catStrg))) {
            return true;
        }


        return false;


    }

    public JsonObject getTokenParams(String did) {
        JsonObject jsonObject = new JsonObject();
        String device_id = did;
        String concatenatedString = SHA_REQUEST_PHRASE +
                KEY_ACCESS_CODE + "=" + ACCESS_CODE +
                KEY_DEVICE_ID + "=" + device_id +
                KEY_LANGUAGE + "=" + LANGUAGE_TYPE +
                KEY_MERCHANT_IDENTIFIER + "=" + MERCHANT_IDENTIFIER +
                KEY_SERVICE_COMMAND + "=" + SDK_TOKEN +
                SHA_REQUEST_PHRASE;

        jsonObject.addProperty(KEY_SERVICE_COMMAND, SDK_TOKEN);
        jsonObject.addProperty(KEY_MERCHANT_IDENTIFIER, MERCHANT_IDENTIFIER);
        jsonObject.addProperty(KEY_ACCESS_CODE, ACCESS_CODE);
        String signature = getSignatureSHA256(concatenatedString);
        jsonObject.addProperty(KEY_SIGNATURE, signature);
        jsonObject.addProperty(KEY_DEVICE_ID, device_id);
        jsonObject.addProperty(KEY_LANGUAGE, LANGUAGE_TYPE);

        Log.e("concatenatedString", concatenatedString);
        Log.e("signature", signature);
        Log.e("JsonString", String.valueOf(jsonObject));
        return jsonObject;
    }


    private static String getSignatureSHA256(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(SHA_TYPE);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            return String.format("%0" + (messageDigest.length * 2) + 'x', new BigInteger(1, messageDigest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public Map<String, Object> collectRequestMap(String sdkToken) {
        Log.e(TAG, "collectRequestMap: " + sdkToken);
        int amount_iso = (int) ((Double.parseDouble(gross_amount)) * 100);
        Log.e(TAG, "collectRequestMap: " + amount_iso);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("command", "PURCHASE");
        requestMap.put("customer_email", user.getEmail());
        requestMap.put("currency", "AED");
        requestMap.put("amount", amount_iso);
        requestMap.put("language", "en");
        requestMap.put("merchant_reference", ref_id);
        requestMap.put("sdk_token", sdkToken);


        requestMap.put("customer_name", user.getName());
        //requestMap.put("customer_ip", "172.150.16.10");
        // requestMap.put("payment_option", "VISA");
        requestMap.put("eci", "ECOMMERCE");
        requestMap.put("merchant_extra", prefs.getUserid());
        //requestMap.put("order_description", "DESCRIPTION");



        Log.e(TAG, "collectRequestMap: " + requestMap);
        return requestMap;
    }

    public io.reactivex.Observable<FortTokenResponce> getTokenApi(JsonObject jsonObject) {
        return Utils.getApiService().getPayFortToken(jsonObject);

    }

    public io.reactivex.Observable<UserModel> updateServerPaymentApi(JsonObject object) {
        return Utils.getApiService().updateServerPayment(object);
    }

}
