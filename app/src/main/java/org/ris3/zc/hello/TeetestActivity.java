package org.ris3.zc.hello;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static android.os.SystemClock.sleep;

public class TeetestActivity extends AppCompatActivity {

    Button okBtn,cancelBtn,openBtn,sendBtn;
    private String TAG = "TeetestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teetest);

        openBtn = (Button)findViewById(R.id.openbtn);
        sendBtn = (Button)findViewById(R.id.sendbtn);
        okBtn = (Button)findViewById(R.id.okbtn);
        cancelBtn = (Button)findViewById(R.id.cancelbtn);

        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //init TEE
                VButton.initAttestation();
                //capture Button
                VButton.regAttestationView(0,0,0,0);

                //for test
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VButton vb = new VButton();
                // call VButton preview API to verify this follow event .
                vb.attestPreview(new VButton.VButtonCallback1() {
                    @Override
                    public void onSend() {
                        // App code
                        //sendFollow("follow" , verifyResult );
                        //Log.d(TAG, "sendFollow now ... !") ;
                        Log.d(TAG, "calculate phash ... !") ;
                        VButton vb = new VButton();
                        vb.SendInfo();
                    }
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "user cancel the operation !") ;
                    }
                });
                //for evaluation
                vb.check_preview();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VButton.regAttestationView(0,0,0,0);
                VButton vb = new VButton();
                // call VButton API to verify this follow event .
                vb.attestView(new VButton.VButtonCallback2() {
                    @Override
                    public void onSuccess() {
                        // App code
                        VButton vb = new VButton();
                        //vb.CheckInfo();
                        vb.getRSASign();
                        vb.RSACheck();
                        //Log.d(TAG, "Check Success !") ;
                    }
                    @Override
                    public void onError() {
                        // handle error
                        Log.d(TAG, "follow button : VButton verify failed !");
                    }
                });
                vb.check_view();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VButton vb = new VButton();
                // call VButton preview API to verify this follow event .
                vb.attestPreview(new VButton.VButtonCallback1() {
                    @Override
                    public void onSend() {
                        // App code
                        //sendFollow("follow" , verifyResult ) ;
                        VButton vb = new VButton();
                        Log.d(TAG, "serial: " + vb.serial + "\nnonce:" + vb.nonce + "\nphash:" + vb.phash) ;
                        vb.serial = "abcdef";
                        vb.nonce = vb.phash = 2;
                        Log.d(TAG, "serial: " + vb.serial + "\nnonce:" + vb.nonce + "\nphash:" + vb.phash) ;
                        Log.d(TAG, "sendFollow now ... !") ;
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "user cancel the operation !") ;
                    }
                });
                //for evaluation
                vb.check_preview();
            }
        });

    }


}
