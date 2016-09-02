package com.nwsoft.maninwork;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 7000;
    ImageView mImageView;
    Button mBtnSkip;
    private String mGmail;
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSettings = getSharedPreferences("Settings",MODE_PRIVATE);
        mGmail = mSettings.getString("GMAIL","");

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mImageView =(ImageView)findViewById(R.id.imageView2);
        //loadImageFromAsset();
        //ClipDrawable drawable = (ClipDrawable) mImageView.getDrawable();
        //Note: The default level is 0, which is fully clipped so the image is not visible. When the level is 10,000, the image is not clipped and completely visible.
        //drawable.setLevel(drawable.getLevel() + 1000);

        TextView txtVersion = (TextView)findViewById(R.id.txt_version);
        String versionName = pInfo.versionName;
        int versionCode=pInfo.versionCode;
        txtVersion.setText(String.format("Version Code: %d ( %s )", versionCode, versionName));
        mBtnSkip= (Button)findViewById(R.id.button4);
        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //http://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/
        //둘중 하나? 또는 둘다?
        //startDelayRun();      //x초후 그냥 종료.
        startCountdown();       //x초를 카운트다운하고 나서 종료
    }

    private void startDelayRun() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Launch after splash activity
//                Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(i);
                // close this activity(splash)
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void startCountdown() {
        new CountDownTimer(SPLASH_TIME_OUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //이 부분에 반복적으로 실행해줄 코드를 넣어주면 된다
                int count = (int)(millisUntilFinished/1000);
                mBtnSkip.setText(String.format("SKIP %s", count));
            }
            @Override
            public void onFinish() {
                //이 부분은 Time out초가 지나고 끝났을때 이부분이 실행되게 된다
                Intent iCom = new Intent(SplashActivity.this,CompanyActivity.class);
                startActivity(iCom);
                finish();
            }
        }.start();
    }

    public void loadImageFromAsset() {
        try {
            InputStream ims = getAssets().open("maninworksvg1024x500.png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            mImageView.setImageDrawable(d);
        }
        catch(IOException ex) {
            return;
        }

    }
}
