package com.example.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.fragment.MemberFragment;
import com.example.test.util.SPUtil;
import com.example.test.widget.GraphicLockView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GestureUnlockActivity extends AppCompatActivity {

    private GraphicLockView graphicLockView;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_unlock);

        Intent intent = getIntent();
        final String signinId = intent.getStringExtra("signinId");
        Log.i("memFrgInfo", "receive "+signinId);

        backBtn = findViewById(R.id.toolbar_left_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        graphicLockView = findViewById(R.id.lock_view1);
        graphicLockView.setOnGraphicLockListener(new GraphicLockView.OnGraphicLockListener() {

            @Override
            public void LockSuccess(int what, String password) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentTime = sdf.format(new Date());
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Log.i("geslockInfo", ClassTabActivity.classId);
                        RequestBody requestBody = new FormBody.Builder()
                                .add("classId", ClassTabActivity.classId)
                                .add("signinId", signinId)
                                .add("phoneNumber", MainActivity.phoneNumber)
                                .add("time", currentTime)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://47.98.236.0:8080/signin")
                                .post(requestBody)
                                .build();
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                final String responseBodyStr = response.body().string();
                                if(responseBodyStr.equals("have_finish")){
                                    showAlertDialog("本次签到已结束！");
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(GestureUnlockActivity.this)
                                                    .setTitle("手势签到")
                                                    .setMessage("手势签到成功！")
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            MemberFragment.experTV.setText("当前获得" + String.valueOf(SubmitActivity.getExperience(MemberFragment.experTV.getText().toString())
                                                                    + Integer.valueOf(responseBodyStr)) + "经验值");
                                                            finish();
                                                        }
                                                    });
                                            builder.show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).start();
                AlertDialog.Builder builder = new AlertDialog.Builder(GestureUnlockActivity.this)
                        .setTitle("手势签到")
                        .setMessage("手势签到成功！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
            }

            @Override
            public void LockFailure() {
                AlertDialog.Builder builder = new AlertDialog.Builder(GestureUnlockActivity.this)
                        .setTitle("手势签到")
                        .setMessage("手势签到失败！请重新尝试")
                        .setPositiveButton("确定", null);
                builder.show();
            }

            @Override
            public void LockShort() {
                AlertDialog.Builder builder = new AlertDialog.Builder(GestureUnlockActivity.this)
                        .setTitle("手势签到")
                        .setMessage("最少连接四个点")
                        .setPositiveButton("确定", null);
                builder.show();
            }
        });
    }

    public void showAlertDialog(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(GestureUnlockActivity.this)
                        .setMessage(msg)
                        .setPositiveButton("确定", null);
                builder.show();
            }
        });
    }

}
