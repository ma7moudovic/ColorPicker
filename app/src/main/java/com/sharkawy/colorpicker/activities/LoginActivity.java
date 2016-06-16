package com.sharkawy.colorpicker.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sharkawy.colorpicker.utilities.AppConstant;
import com.sharkawy.colorpicker.Instagram.InstagramApp;
import com.sharkawy.colorpicker.R;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private InstagramApp mApp;
//    private ImageView profile ;
    private Button btnConnect, btnViewInfo, btnGetAllImages, btnFollowers,
            btnFollwing;
    private LinearLayout llAfterLoginView;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(LoginActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setWidgetReference();
        bindEventHandlers();


        mApp = new InstagramApp(this, AppConstant.CLIENT_ID,
                AppConstant.CLIENT_SECRET, AppConstant.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(LoginActivity.this, "Connected as " + mApp.getUserName(), Toast.LENGTH_SHORT)
                        .show();
                btnConnect.setText("Disconnect");
//                llAfterLoginView.setVisibility(View.VISIBLE);
                // userInfoHashmap = mApp.
                mApp.fetchUserName(handler);
                startActivity(new Intent(LoginActivity.this, GalleryActivity.class)
                        .putExtra("userInfo", userInfoHashmap));
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });


        if (mApp.hasAccessToken()) {
            mApp.resetAccessToken();
            // tvSummary.setText("Connected as " + mApp.getUserName());
//            btnConnect.setText("Disconnect");
////            llAfterLoginView.setVisibility(View.VISIBLE);
//            mApp.fetchUserName(handler);
////            userInfoHashmap=mApp.getUserInfo();
//            startActivity(new Intent(LoginActivity.this, GalleryActivity.class)
//                    .putExtra("userInfo", userInfoHashmap));
        }

    }
    private void bindEventHandlers() {
        btnConnect.setOnClickListener(this);
//        profile.setOnClickListener(this);
    }

    private void setWidgetReference() {
        btnConnect = (Button) findViewById(R.id.btn_login);
//        profile = (ImageView) findViewById(R.id.profile);
    }

    @Override
    public void onClick(View v) {
        if(v==btnConnect){
            connectOrDisconnectUser();
//        }else if(v==profile){
//            if (mApp.hasAccessToken()) {
//                Toast.makeText(LoginActivity.this,userInfoHashmap.get(InstagramApp.TAG_ID),Toast.LENGTH_SHORT).show();
//                Glide.with(this)
//                        .load(userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE))
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(profile);
//
//                startActivity(new Intent(LoginActivity.this, GalleryActivity.class)
//                        .putExtra("userInfo", userInfoHashmap));
//            }else {
//                profile.setImageResource(R.mipmap.ic_launcher);
//            }
        }
    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    LoginActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new Dialog.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                    // btnConnect.setVisibility(View.VISIBLE);
//                                    llAfterLoginView.setVisibility(View.GONE);
                                    btnConnect.setText("Connect");
                                    // tvSummary.setText("Not connected");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }
}
