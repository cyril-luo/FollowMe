package org.ris3.zc.hello;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by cyril on 17-2-15.
 */



public class VButton {

    private String TAG = "VButon";
    public static long nonce;
    public static String serial;
    public static long phash;
    public static int remote_ret;

    abstract interface VButtonCallback1 {
        void onSend();
        void onCancel();
    }

    public interface VButtonCallback2 {
        void onSuccess();
        void onError();
    }

    private VButtonCallback1 vbuttoncallback1;
    private VButtonCallback2 vbuttoncallback2;

    public void attestPreview(VButtonCallback1 vbuttoncallback) {
        //do preview
        this.vbuttoncallback1 = vbuttoncallback;
    }

    public void attestView(VButtonCallback2 vbuttoncallback) {
        //do view
        this.vbuttoncallback2 = vbuttoncallback;
    }

    private void doSuccess(){
        //attestation code

        vbuttoncallback2.onSuccess();
    }

    private void doError(){
        //do nothing
        vbuttoncallback2.onError();
    }


    private void doSend(){
        //send code

        vbuttoncallback1.onSend();
    }

    private void doCancel(){
        //do nothing
        vbuttoncallback1.onCancel();
    }

    public void check_preview(){
        boolean state = true;
        //
        if(state)
            doSend();
        else
            doCancel();
    }

    public void check_view(){
        boolean state = true;
        //
        if(state)
            doSuccess();
        else
            doError();
    }

    //TEE Interface func

    public static int initAttestation() {
        return TEEService.SecAttest_Init();
    }

    public static int finalAttestation() {
        return TEEService.SecAttest_Final();
    }

    public static int regAttestationView(int x1, int y1, int x2, int y2) {
        return TEEService.SecAttest_Register_View(x1,y1, x2, y2);
    }

    public static int unregAttestationView(int x1, int y1, int x2, int y2) {
        return TEEService.SecAttest_Unregister_View(x1, y1, x2, y2);
    }


    //get info to attestation

    public String getDeviceinfo(){
        String str;
        //get serial
        str = android.os.Build.SERIAL;
        return str;
    }

    public String getPicinfo(int x1, int y1, int x2, int y2, String phash){
        String str=null;
        return str;
    }

    // net interface

    public void SendInfo() {
        // init serial and phash
        serial = getDeviceinfo();
        phash = 847372483;
        //Log.d(TAG, "serial:" + serial);
        if (serial == null) {
            return;
        }
        //Log.d(TAG, "phash:" + phash);
        if (phash == 0) {
            return;
        }
        final HttpClient client = HttpClient.getInstance(getApplicationContext());
        JSONObject followRequest = new JSONObject();
        try {
            followRequest.put("serial", serial);
            followRequest.put("phash", phash);
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        JsonObjectRequest joq =
                new JsonObjectRequest(getFullUrl("/devices/send"), followRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONObject followResp = null;
                                try {
                                    followResp = response.getJSONObject("follow");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                resNonce(followResp);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFollowSend(null);
                            }
                        });
        client.send(joq);
        return;
    }


    public void CheckInfo() {
        //Log.d(TAG, "serial:" + serial);
        if (serial == null) {
            return;
        }
        //Log.d(TAG, "phash:" + phash);
        if (phash == 0) {
            return;
        }
        //Log.d(TAG, "nonce:" + nonce);
        if (nonce == 0) {
            return;
        }
        final HttpClient client = HttpClient.getInstance(getApplicationContext());
        JSONObject followRequest = new JSONObject();
        try {
            followRequest.put("serial", serial);
            followRequest.put("phash", phash);
            followRequest.put("nonce", nonce);
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        JsonObjectRequest joq =
                new JsonObjectRequest(getFullUrl("/devices/check"), followRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONObject followResp = null;
                                try {
                                    followResp = response.getJSONObject("follow");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                check_ret(followResp);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onFollowSend(null);
                            }
                        });
        client.send(joq);
        return;
    }

    private String getFullUrl(String url) {
        //Log.d(TAG,"Server Port:" + AppConfig.SERVER_PORT.toString());
        return "http://" + AppConfig.SERVER_IP + ":" + AppConfig.SERVER_PORT + url;
    }

    private void onFollowSend(JSONObject followResp) {
        int res = -1;
        try {
            if (followResp != null)
                res = followResp.getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (res >= 0)
            Log.d(TAG, "onFollowSend succeed! res = " + res);
        else
            Log.d(TAG, "onFollowSend failed! res = " + res);

        return;
    }

    private void resNonce(JSONObject followResp) {
        long res = -1;
        try {
            if (followResp != null)
                res = followResp.getLong("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nonce = res;
        if (res > 0)
            Log.d(TAG, "requestNonce succeed! nonce = " + res);
        else
            Log.d(TAG, "requestNonce failed! nonce = " + res);
        return;
    }

    private void check_ret(JSONObject followResp) {
        int res = -1;
        try {
            if (followResp != null)
                res = followResp.getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remote_ret = res;
        if (res > 0)
            Log.d(TAG, "Remotecheck succeed! ret = " + res);
        else
            Log.d(TAG, "Remotecheck failed! ret = " + res);
        return;
    }

}
