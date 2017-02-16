package org.ris3.zc.hello;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by cyril on 17-2-15.
 */



public class VButton {

    private String TAG = "VButon";
    public static long nonce;
    public static String serial;
    public static long phash;
    public static String plain;
    public static String rsasign;

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

    public void getPicinfo(int x1, int y1, int x2, int y2, String phash){
        String str=null;
        return ;
    }

    public void getRSASign(){
        if (serial == null) {
            return ;
        }
        if (phash == 0) {
            return ;
        }
        if (nonce == 0) {
            return ;
        }
        String str = serial + nonce + phash;
        RSAFunc rsaEncrypt= new RSAFunc();
        try {
            rsaEncrypt.loadRSAKey(RSAFunc.DEFAULT_PRIVATE_KEY);
            System.out.println("加载私钥成功");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("加载私钥失败");
        }
        try {
            //加密
            str = sha1(str);
            plain = str;
            Log.d(TAG, "plain:" + plain);
            byte[] cipher = rsaEncrypt.encryptbyPri(rsaEncrypt.getPrivateKey(), str.getBytes("UTF-8"));
            //rsasign = cipher;
            rsasign = Base64.encodeToString(cipher,Base64.DEFAULT);
            Log.d(TAG, "rsasign:" + rsasign);
            String hex="";
            for (int i = 0; i < cipher.length; i++) {
                hex += Integer.toHexString(cipher[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
            }
            Log.d(TAG, "hex:" + hex);
            //解密
            /*
            byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(), cipher);
            System.out.println("密文长度:"+ cipher.length);
            System.out.println(RSAFunc.byteArrayToString(cipher));
            System.out.println("明文长度:"+ plainText.length);
            System.out.println(RSAFunc.byteArrayToString(plainText));
            System.out.println(new String(plainText));
            */

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }


    // net interface

    public void SendInfo() {
        // init serial and phash
        serial = getDeviceinfo();
        phash = 847372483;
        if (serial == null) {
            return;
        }
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

    public void RSACheck() {
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
        //Log.d(TAG, "nonce:" + nonce);
        if (rsasign == null) {
            return;
        }
        final HttpClient client = HttpClient.getInstance(getApplicationContext());
        JSONObject followRequest = new JSONObject();
        try {
            followRequest.put("serial", serial);
            followRequest.put("phash", phash);
            followRequest.put("nonce", nonce);
            followRequest.put("rsasign", rsasign);
            followRequest.put("plain", plain);
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        JsonObjectRequest joq =
                new JsonObjectRequest(getFullUrl("/devices/getRSA"), followRequest,
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
