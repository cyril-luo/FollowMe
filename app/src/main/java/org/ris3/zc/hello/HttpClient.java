package org.ris3.zc.hello;

/**
 * Created by zhisun on 1/8/17.
 */

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class HttpClient {

    private static HttpClient instance_ = null;
    private static Context context_ = null;

    private RequestQueue requestQueue_ = null;

    private HttpClient(Context ctxt) {

        context_ = ctxt;
        requestQueue_ = getRequestQueue();

    }

    public static synchronized HttpClient getInstance(Context context) {
        if (instance_ == null) {
            instance_ = new HttpClient(context);
        }
        return instance_;
    }

    RequestQueue getRequestQueue() {
        if(requestQueue_ == null) {
            return Volley.newRequestQueue(context_.getApplicationContext());
        } else {
            return requestQueue_;
        }
    }

    public <T> void send(Request<T> req) {
        getRequestQueue().add(req);
    }
}
