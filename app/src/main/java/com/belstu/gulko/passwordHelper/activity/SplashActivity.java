package com.belstu.gulko.passwordHelper.activity;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.belstu.gulko.passwordHelper.R;

public class SplashActivity extends AppCompatActivity {
    public static String str_login_test;

    public static SharedPreferences sh;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        sh = getSharedPreferences("myprefe", 0);
        editor = sh.edit();
        str_login_test = sh.getString("loginTest", null);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                if (str_login_test != null
                        && !str_login_test.toString().trim().equals("")) {
                    Intent send = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    startActivity(send);
                }

                else {

                    Intent send = new Intent(getApplicationContext(),
                            LoginRegistrationActivity.class);
                    startActivity(send);

                }
            }

        }, 3000);

    }

    public boolean containsPass(String str){

       return  sh.contains(str)?true:false;


    }

}
