package com.example.joaco.phonecalltracker;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Services extends AppCompatActivity {
    private TextView tv_service;
    private TextView tv_service_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar=(Toolbar)findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.tv_service=(TextView)findViewById(R.id.tv_service);
        this.tv_service_content=(TextView)findViewById(R.id.tv_service_content);
        this.tv_service.setText(getIntent().getStringExtra("service"));
        this.tv_service_content.setText("Resultado proporcionado por el "+getIntent().getStringExtra("service"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void exit(View v) {
        this.finish();
    }
}
