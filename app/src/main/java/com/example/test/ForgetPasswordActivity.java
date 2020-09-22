package com.example.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import okhttp3.*;

public class ForgetPasswordActivity extends AppCompatActivity {

    protected EditText userNameET;
    protected EditText verificationET;
    protected Button sendCodeBtn;
    protected EditText passwordET;
    protected EditText passwordConfirmET;
    protected Button registerBtn;
    private int verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ActivityCollector.addActivity(this);
        userNameET = findViewById(R.id.et_login_username);
        verificationET = findViewById(R.id.et_reg_vericode);
        sendCodeBtn = findViewById(R.id.bt_veri_submit);
        passwordET = findViewById(R.id.et_login_pwd);
        passwordConfirmET = findViewById(R.id.et_reg_conf_pwd);
        registerBtn = findViewById(R.id.bt_login_submit);
        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                verificationCode = r.nextInt(899999) + 100000;
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this)
                        .setTitle("验证码")
                        .setMessage("验证码为：" + verificationCode)
                        .setPositiveButton("确定", null);
                builder.show();
                sendCodeBtn.setText("已发送");
                sendCodeBtn.setEnabled(false);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verificationET.getText().toString().equals(verificationCode+"")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this)
                            .setMessage("验证码错误！")
                            .setPositiveButton("确定", null);
                    builder.show();
                    sendCodeBtn.setText("发送验证码");
                    sendCodeBtn.setEnabled(true);
                }else if(!passwordET.getText().toString().equals(passwordConfirmET.getText().toString())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this)
                            .setMessage("两次密码输入不一致！")
                            .setPositiveButton("确定", null);
                    builder.show();
                }else if(passwordET.getText().toString().length() < 6) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this)
                            .setMessage("密码最低为6位！")
                            .setPositiveButton("确定", null);
                    builder.show();
                }else if(!isChinaPhoneLegal(userNameET.getText().toString())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this)
                            .setMessage("请输入正确的手机号码！")
                            .setPositiveButton("确定", null);
                    builder.show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("queryType", "forgetPassword")
                                    .add("userName", userNameET.getText().toString())
                                    .add("password", passwordET.getText().toString())
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://47.98.236.0:8080/register")
                                    .post(requestBody)
                                    .build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                    Toast.makeText(RegisterActivity.this, "Connection failed!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    String responseBodyStr = response.body().string();
                                    Log.i("RegisterInfo", responseBodyStr);
//                                    SharedPreferences.Editor editor = getSharedPreferences(userNameET.getText().toString(), MODE_PRIVATE).edit();
//                                    editor.putString("password", passwordET.getText().toString());
//                                    editor.apply();
                                    if(responseBodyStr.equals("success")){
                                        showAlertDialog("修改新密码成功！");
                                    }else if(responseBodyStr.equals("failure")){
                                        showAlertDialog("此账户不存在！");
                                    }

                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    protected void showAlertDialog(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this)
                        .setMessage(msg);
                if(msg.equals("注册成功！")){
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                        }
                    });
                }else{
                    builder.setPositiveButton("确定", null);
                }
                builder.show();
            }
        });
    }

    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        String regExp = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])" +
                "|(18[0-9])|(19[8,9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
