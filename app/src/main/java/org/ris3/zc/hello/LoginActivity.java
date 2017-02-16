package org.ris3.zc.hello;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by zhisun on 1/11/17.
 */

public class LoginActivity extends AppCompatActivity {
    final String TAG = "LoginActivity";
    CallbackManager callbackManager;
    Button loginBtn,cancelBtn,testBtn;
    LoginButton loginButton;
    EditText nameTxt, passwdTxt;

    TextView tv1;
    int trialCounter = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginBtn = (Button)findViewById(R.id.loginBtn);
        nameTxt = (EditText)findViewById(R.id.nameTxt);
        passwdTxt = (EditText)findViewById(R.id.passwdTxt);

        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        testBtn = (Button)findViewById(R.id.open);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameTxt.getText().toString().equals("admin") &&
                        passwdTxt.getText().toString().equals("admin")) {
                    Log.d(TAG, "login success, start mainActivity");
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();
                    startMainActivity();
                }else{
                    Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
                    trialCounter--;
                    Log.d(TAG, "login trial failed!");
                    if (trialCounter == 0) {
                        loginBtn.setEnabled(false);
                    }
                }
            }
        });

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, TeetestActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        /*
        //test preview
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VButton vb = new VButton();
                // call VButton preview API to verify this follow event .
                vb.attestPreview(new VButton.VButtonCallback1() {
                    @Override
                    public void onSend() {
                        // App code
                        //sendFollow("follow" , verifyResult ) ;
                        Log.d(TAG, "sendFollow now ... !") ;
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "user cancel the operation !") ;
                    }
                });
                //for evaluation

                //vb.doSend();
                //vb.doCancel();
                vb.check_preview();
            }
        });
        */
        //test view
        /*
        VButton.regAttestationView(0,0,0,0);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VButton vb = new VButton();

                // call VButton API to verify this follow event .
                vb.attestView(new VButton.VButtonCallback2() {
                    @Override
                    public void onSuccess() {
                        // App code
                        Log.d(TAG, "Check Success !") ;
                    }

                    @Override
                    public void onError() {
                        // handle error
                        Log.d(TAG, "follow button : VButton verify failed !");
                    }
                });

                vb.doSuccess();
                vb.doError();
            }
        });
        */

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        loginButton.setReadPermissions("email");
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "loginbutton:facebook login success");
                startMainActivity();
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "loginbutton:facebook login cancel");

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "loginbutton:facebook login failed");
            }
        });
    }

    private void startMainActivity() {
        Log.d(TAG, "start MainActivity");
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
