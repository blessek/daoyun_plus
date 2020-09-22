package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.adapter.MemberAdapter;
import com.example.test.util.SPUtil;
import com.example.test.widget.GraphicLockView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FinishSignInActivity extends AppCompatActivity {

    private Button finishSignInBtn;
    private TextView signInNumTV;
    private Button backBtn;
    private ListView listView;
    private List<Member> memberList = new ArrayList<>();
    private MemberAdapter memberAdapter;
    private String signinId;
    private TextView refreshTV;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_sign_in);
        final String signin_mode = getIntent().getStringExtra("signin_mode");
        signinId = getIntent().getStringExtra("signinId");

        initMember(0);
        memberAdapter = new MemberAdapter(FinishSignInActivity.this, R.layout.member_item, memberList);
        listView = findViewById(R.id.signedIn_listview);
        listView.setAdapter(memberAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Member member = memberList.get(position);
                Toast.makeText(FinishSignInActivity.this, member.getMemberName(), Toast.LENGTH_SHORT).show();
            }
        });

        refreshTV = findViewById(R.id.toolbar_right_tv);
        refreshTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberList.clear();
                initMember(1);
            }
        });

        backBtn = findViewById(R.id.toolbar_left_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        finishSignInBtn = findViewById(R.id.finish_signin_btn);
        if(signin_mode.equals("gesture_signin_mode")){
            finishSignInBtn.setText("结束手势签到");
        }else{
            finishSignInBtn.setText("结束一键签到");
        }

        signInNumTV = findViewById(R.id.signIn_num_Tv);
        finishSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("signinId", signinId)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://47.98.236.0:8080/finishsignin")
                                .post(requestBody)
                                .build();
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String responseBodyStr = response.body().string();
                                final String signinTypeStr;
                                if(signin_mode.equals("gesture_signin_mode")){
                                    signinTypeStr = "手势签到！";
                                    GraphicLockView.mPassword = null;
                                }else{
                                    signinTypeStr = "一键签到！";
                                }
                                if(responseBodyStr.equals("finish_succeed")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(FinishSignInActivity.this)
                                                    .setMessage("成功结束" + signinTypeStr)
                                                    .setPositiveButton("确定", null);
                                            builder.show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    public void initMember(final int i){
        if(i == 1){
            progressDialog = new ProgressDialog(FinishSignInActivity.this);
            progressDialog.setMessage("刷新中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("signinId", signinId)
                        .build();
                Request request = new Request.Builder()
                        .url("http://47.98.236.0:8080/querysigninmember")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseBodyStr = response.body().string();
                        if(responseBodyStr.equals("nobody_join")){
                            if(i == 1){
                                progressDialog.dismiss();
                            }
                        }else{
                            try {
                                parseJoinedList(responseBodyStr, i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public void parseJoinedList(String JsonArrayData, final int m) throws JSONException {
        JSONArray jsonArray = new JSONArray(JsonArrayData);
        int rank = 0;
        for(int i = 0 ; i < jsonArray.length() ; i++){
            rank++;
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            final String phoneNumber = jsonObject.getString("phoneNumber");
            final String name = jsonObject.getString("name");
            final String IDNumber = jsonObject.getString("IDNumber");
            final String experienceScore = jsonObject.getString("experienceScore");
            final String icon = jsonObject.getString("icon");
            if(icon.equals("")){
                Member member;
                if(name.equals("")){
                    member = new Member(String.valueOf(rank), R.drawable.course_img_1, phoneNumber, IDNumber, experienceScore+"经验值");
                }else{
                    member = new Member(String.valueOf(rank), R.drawable.course_img_1, name, IDNumber, experienceScore+"经验值");
                }
                memberList.add(member);
//                memberAdapter.notifyDataSetChanged();
            }else{
                final File userIconFile = new File(Environment.getExternalStorageDirectory() + "/daoyun/" +icon);
                if(!userIconFile.exists()){
                    final int finalRank = rank;
                    final int finalRank1 = rank;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("icon", icon)
                                    .add("type", "usericon")
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://47.98.236.0:8080/downloadicon")
                                    .post(requestBody)
                                    .build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    FileOutputStream os = new FileOutputStream(userIconFile);
                                    byte[] BytesArray = response.body().bytes();
                                    os.write(BytesArray);
                                    os.flush();
                                    os.close();
//                                    afterAction(finalRank, userIconFile.getAbsolutePath(), name, IDNumber, experienceScore, phoneNumber);
                                    Member member;
                                    if(name.equals("")){
                                        member = new Member(String.valueOf(finalRank1), userIconFile.getAbsolutePath(), phoneNumber, IDNumber, experienceScore+"经验值");
                                    }else{
                                        member = new Member(String.valueOf(finalRank1), userIconFile.getAbsolutePath(), name, IDNumber, experienceScore+"经验值");
                                    }
                                    memberList.add(member);
//                                    memberAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }else{
                    Member member;
                    if(name.equals("")){
                        member = new Member(String.valueOf(rank), userIconFile.getAbsolutePath(), phoneNumber, IDNumber, experienceScore+"经验值");
                    }else{
                        member = new Member(String.valueOf(rank), userIconFile.getAbsolutePath(), name, IDNumber, experienceScore+"经验值");
                    }
                    memberList.add(member);
//                    memberAdapter.notifyDataSetChanged();
                }

            }
        }
        final int finalRank2 = rank;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                memberAdapter.notifyDataSetChanged();
                signInNumTV.setText(finalRank2 + "人");
                if(m == 1){
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
