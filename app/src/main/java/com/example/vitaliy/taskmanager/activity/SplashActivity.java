package com.example.vitaliy.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.example.vitaliy.taskmanager.R;
import com.github.ybq.android.spinkit.style.ThreeBounce;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        ThreeBounce threeBounce = new ThreeBounce();
        if (progressBar != null) {
            progressBar.setIndeterminateDrawable(threeBounce);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,TaskActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
                overridePendingTransition(R.anim.enter_right_to_left_splash,R.anim.exit_right_to_left_splash);
            }
        },3000);

    }
}
