package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.example.test.widget.GraphicLockView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OneBtnSignInSettingActivity extends AppCompatActivity {

    private EditText distanceLimitET;
    private Button startOneBtn;
    private TextView experienceSettingTV;
    private LinearLayout latitudeLayout;
    private LinearLayout longitudeLayout;
    private TextView latitudeTV;
    private TextView longitudeTV;
    private String selectExperience;
    private double latitude;
    private double longitude;
    public static boolean startOrNot = false;
    public static int distanceLimit = 0;
    public LocationClient locationClient;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SharedPreferences preferences = getSharedPreferences("sigin", MODE_PRIVATE);
        setContentView(R.layout.activity_one_btn_sign_in_setting);

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

        experienceSettingTV = findViewById(R.id.signin_experience_Tv);
        final String[] experience = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        experienceSettingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OneBtnSignInSettingActivity.this)
                        .setTitle("选择经验值")
                        .setSingleChoiceItems(experience, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectExperience = experience[which];
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        experienceSettingTV.setText(selectExperience);
                    }
                });
                builder.show();
            }
        });
        latitudeLayout = findViewById(R.id.latitude_layout);
        latitudeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLongitudeLatitude();
            }
        });
        longitudeLayout = findViewById(R.id.longitude_layout);
        longitudeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLongitudeLatitude();
            }
        });
        latitudeTV = findViewById(R.id.latitude_Tv);
        longitudeTV = findViewById(R.id.longitude_Tv);
        distanceLimitET = findViewById(R.id.distance_limit_Et);
        startOneBtn = findViewById(R.id.start_one_btn);
        startOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(distanceLimitET.getText().toString().equals("")){
                    showAlertDialog("请输入签到极限距离！");
                }else if(experienceSettingTV.getText().toString().equals("")){
                    showAlertDialog("请选择签到经验值！");
                }else{
                    if(longitudeTV.getText().toString().equals("") && latitudeTV.getText().toString().equals("")){
                        getLongitudeLatitude();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("classId", ClassTabActivity.classId)
                                    .add("className", ClassTabActivity.courseName)
                                    .add("signinType", "oneButton")
                                    .add("experience", experienceSettingTV.getText().toString())
                                    .add("limitDistance", distanceLimitET.getText().toString())
                                    .add("latitude", latitudeTV.getText().toString())
                                    .add("longitude", longitudeTV.getText().toString())
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://47.98.236.0:8080/setsignin")
                                    .post(requestBody)
                                    .build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    final String responseBodyStr = response.body().string();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(OneBtnSignInSettingActivity.this)
                                                    .setMessage("一键签到设置成功！")
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            startActivityForResult(new Intent(OneBtnSignInSettingActivity.this, FinishSignInActivity.class)
                                                                    .putExtra("signin_mode","one_btn_mode")
                                                                    .putExtra("signinId", responseBodyStr), 1);
                                                        }
                                                    });
                                            builder.show();
                                        }
                                    });
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            finish();
        }
    }

    public void getLongitudeLatitude(){
        progressDialog = new ProgressDialog(OneBtnSignInSettingActivity.this);
        progressDialog.setMessage("获取定位信息中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        locationClient.start();
    }

    public void showAlertDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(OneBtnSignInSettingActivity.this)
                .setMessage(msg)
                .setPositiveButton("确定",null);
        builder.show();
    }

    protected void showProgressDialog(final ProgressDialog progressDialog){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                startActivity(new Intent(OneBtnSignInSettingActivity.this, FinishSignInActivity.class)
                        .putExtra("signin_mode", "one_btn_mode"));
            }
        });
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            longitude = bdLocation.getLongitude();
            latitude = bdLocation.getLatitude();
            longitudeTV.setText(String.valueOf(longitude));
            latitudeTV.setText(String.valueOf(latitude));
            progressDialog.dismiss();
        }
    }

}
