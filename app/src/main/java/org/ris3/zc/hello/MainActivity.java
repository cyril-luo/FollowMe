package org.ris3.zc.hello;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    static int followId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LikeView likeView = (LikeView) findViewById(R.id.like_view);
        likeView.setObjectIdAndType(
                "https://www.facebook.com/FacebookDevelopers",
                LikeView.ObjectType.PAGE);

        Button followBtn = (Button)findViewById(R.id.button);
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "follow";
                sendFollow(msg);
            }
        });
    }

    private void sendFollow(String msg) {

        int fromId = 0;

        followId += 1;

        Log.d(TAG, "Send Follow Msg:" + msg);
        if (msg == null) {
            return;
        }

        final HttpClient client = HttpClient.getInstance(getApplicationContext());
        JSONObject followRequest = new JSONObject();

        try {
            followRequest.put("follow", followId);
            followRequest.put("from", fromId);
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        JsonObjectRequest joq =
                new JsonObjectRequest(getFullUrl("/post/follow"), followRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONObject followResp = null;

                                try {
                                    followResp = response.getJSONObject("follow");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                onFollowSend(followResp);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                onFollowSend(null);
                            }
                        });

        client.send(joq);
    }

    private String getFullUrl(String url) {
        Log.d(TAG,"Server Port:" + AppConfig.SERVER_PORT.toString());
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
}
