package com.example.zver.qrcogeapplication;

import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        TextView tvResult = (TextView) findViewById(R.id.tvResult);
        CheckBox cbResult = (CheckBox) findViewById(R.id.cbResult);
        Intent intentHome = getIntent();
        tvResult.setText("URL: " + intentHome.getStringExtra("url"));
        boolean flag = Boolean.parseBoolean(intentHome.getStringExtra("flag"));
        cbResult.setChecked(intentHome.getBooleanExtra("flag", false));
    }
}
