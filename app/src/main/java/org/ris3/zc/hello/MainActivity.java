package org.ris3.zc.hello;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Log.d(TAG, "Send Follow Msg:" + msg);
        if (msg == null) {
            return;
        }

        final HttpClient client = HttpClient.getIn
    }

}
