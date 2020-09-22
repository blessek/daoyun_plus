package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.util.SPUtil;
import com.example.test.widget.GraphicLockView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GestureSettingActivity extends AppCompatActivity {

    private GraphicLockView graphicLockView;
    private TextView addExperienceTV;
    private String selectExperience = null;
    private Button backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_setting);
        GraphicLockView.mPassword = null;
        graphicLockView = findViewById(R.id.lock_view);
        addExperienceTV = findViewById(R.id.toolbar_right_tv);
        final String[] experience = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        addExperienceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] tempExperience = new String[1];
                AlertDialog.Builder builder = new AlertDialog.Builder(GestureSettingActivity.this)
                        .setTitle("选择经验值")
                        .setSingleChoiceItems(experience, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tempExperience[0] = experience[which];
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectExperience = tempExperience[0];
                    }
                });
                builder.show();
            }
        });
        backBtn = findViewById(R.id.toolbar_left_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        Log.i("settingException", "screenshot fail4");
//        SharedPreferences preferences = getSharedPreferences("sigin", MODE_PRIVATE);
//        GraphicLockView.mPassword = preferences.getString("gestureSignIn", null);
//        OneBtnSignInSettingActivity.startOrNot = preferences.getBoolean("oneBtnSignIn", false);
//        if(GraphicLockView.mPassword != null){
//            AlertDialog.Builder dialog = new AlertDialog.Builder(GestureSettingActivity.this)
//                    .setTitle("签到手势设置")
//                    .setMessage("签到手势已设置，在手势签到结束前不能重新设置！")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            GestureSettingActivity.this.finish();
//                        }
//                    });
//                    dialog.show();
//        }else if(OneBtnSignInSettingActivity.startOrNot == true){
//            AlertDialog.Builder dialog = new AlertDialog.Builder(GestureSettingActivity.this)
//                    .setTitle("签到手势设置")
//                    .setMessage("设置失败！已有一个签到活动正在进行中！")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            GestureSettingActivity.this.finish();
//                        }
//                    });
//            dialog.show();
//        }

        graphicLockView.setOnGraphicLockListener(new GraphicLockView.OnGraphicLockListener() {

            @Override
            public void LockSuccess(int what, final String password) {
                if (GraphicLockView.mPassword == null) {
                    if(selectExperience == null){
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(GestureSettingActivity.this)
                                .setMessage("请先设置签到经验值！")
                                .setPositiveButton("确定", null);
                        builder1.show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(GestureSettingActivity.this)
                                .setTitle("签到手势设置")
                                .setMessage("手势解锁顺序为" + password + ",点击确定开始签到")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GraphicLockView.mPassword = password;
                                        Log.i("gesActiInfo", "connectioning...");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                OkHttpClient okHttpClient = new OkHttpClient();
                                                RequestBody requestBody = new FormBody.Builder()
                                                        .add("classId", ClassTabActivity.classId)
                                                        .add("className", ClassTabActivity.courseName)
                                                        .add("signinType", "gesture")
                                                        .add("experience", selectExperience)
                                                        .add("gesturePassword", GraphicLockView.mPassword)
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
                                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(GestureSettingActivity.this)
                                                                        .setMessage("创建手势签到成功！")
                                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                startActivityForResult(new Intent(GestureSettingActivity.this, FinishSignInActivity.class)
                                                                                        .putExtra("signin_mode","gesture_signin_mode")
                                                                                        .putExtra("signinId", responseBodyStr),1);
                                                                            }
                                                                        });
                                                                builder1.show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }).start();

//                            SharedPreferences.Editor editor = getSharedPreferences("sigin", MODE_PRIVATE).edit();
//                            editor.putString("gestureSignIn", password);
//                            editor.apply();

//                            String path = "../../../../config.properties";
//                            Properties properties = new Properties();
//                            InputStream inputStream = null;
//                            try {
//                                inputStream = new BufferedInputStream(new FileInputStream(path));
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                properties.load(inputStream);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            properties.setProperty("gesturePassword", password);
//                            SPUtil.put(GestureSettingActivity.this, "PASSWORD", password);
//                            PropertiesUtill.setProperties(GestureSettingActivity.this, "gesturePassword", password);
                                    }
                                });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                }else{
                    Toast.makeText(GestureSettingActivity.this, "手势密码已存在！", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void LockFailure() {
//                text.setText("与上次绘制的不一致" + GraphicLockView.mPassword);
                Toast.makeText(GestureSettingActivity.this, "设置失败！手势密码已存在！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void LockShort() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GestureSettingActivity.this)
                        .setTitle("签到手势设置")
                        .setMessage("签到手势至少四个点！")
                        .setPositiveButton("确定", null);
                dialog.show();
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
}
