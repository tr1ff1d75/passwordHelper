package com.belstu.gulko.passwordHelper.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.content.res.Configuration;

import com.belstu.gulko.passwordHelper.R;

public class AboutUsActivity extends AppCompatActivity {
    AboutUsActivityFrag frag1;
    FragmentTransaction fTrans;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        frag1 = new AboutUsActivityFrag();

        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont, frag1);
        fTrans.commit();
    }

}
