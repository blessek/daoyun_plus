package com.example.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.test.fragment.FindFragment;
import com.example.test.fragment.MainFragment;
import com.example.test.fragment.MeFragment;
import com.example.test.fragment.MyCreateFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    protected LinearLayout mMenuMain;
    protected LinearLayout mMenuFind;
    protected LinearLayout mMenuMe;
    protected ImageView mainImageView;
    protected ImageView findImageView;
    protected ImageView meImageView;
    protected MainFragment mMainFragment=new MainFragment();//首页
    protected FindFragment mFindFragment=new FindFragment();//发现
    protected MeFragment mMeFragment=new MeFragment();//我的
    public static String userName;
    public static String icon;
    public static String loginType;
    public static String name = null;
    public static String phoneNumber;
    public int BUFFER_SIZE = 8192;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.finishAll();
        ActivityCollector.addActivity(this);
        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        if(userName.equals("15900000001")){
            userName = "admin";
        }else if(userName.equals("15900000002")){
            userName = "teacher";
        }else if(userName.equals("15900000003")){
            userName = "student1";
        }else  if(userName.equals("15900000004")){
            userName = "student2";
        }
//        Log.i("MainActivityInfo", phoneNumber);
//        Log.i("LoginInfo", PropertiesUtill.getProperties(MainActivity.this, "gesturePassword"));
        initView();
        //获取管理类
        this.getSupportFragmentManager()
                .beginTransaction()
            .add(R.id.container_content,mMainFragment)
            .add(R.id.container_content,mFindFragment)
                .hide(mFindFragment)
                .add(R.id.container_content,mMeFragment)
                .hide(mMeFragment)
        //事物添加  默认：显示首页  其他页面：隐藏
        //提交
        .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    //    public BitmapDrawable getImageDrawable(String path)
//            throws IOException
//    {
//        //打开文件
//        File file = new File(path);
//        if(!file.exists())
//        {
//            return null;
//        }
//
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] bt = new byte[BUFFER_SIZE];
//
//        //得到文件的输入流
//        InputStream in = new FileInputStream(file);
//
//        //将文件读出到输出流中
//        int readLength = in.read(bt);
//        while (readLength != -1) {
//            outStream.write(bt, 0, readLength);
//            readLength = in.read(bt);
//        }
//
//        //转换成byte 后 再格式化成位图
//        byte[] data = outStream.toByteArray();
//        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// 生成位图
//        BitmapDrawable bd = new BitmapDrawable(bitmap);
//
//        return bd;
//    }

    /**
     * 初始化视图
     */
    public void initView(){
        mMenuMain= (LinearLayout) this.findViewById(R.id.menu_main);
        mMenuFind= (LinearLayout) this.findViewById(R.id.menu_find);
        mMenuMe= (LinearLayout) this.findViewById(R.id.menu_me);
        mainImageView = this.findViewById(R.id.Iv_main);
        findImageView = this.findViewById(R.id.Iv_find);
        meImageView = this.findViewById(R.id.Iv_me);

        mMenuMain.setOnClickListener(this);
        mMenuFind.setOnClickListener(this);
        mMenuMe.setOnClickListener(this);

        mainImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_main_click));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.menu_main://首页
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .show(mMainFragment)
                        .hide(mFindFragment)
                        .hide(mMeFragment)
                        .commit();
                mainImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_main_click));
                findImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_find_normal));
                meImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_me_normal));
                break;
            case  R.id.menu_find://发现
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mMainFragment)
                        .show(mFindFragment)
                        .hide(mMeFragment)
                        .commit();
                mainImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_main_normal));
                findImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_find_click));
                meImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_me_normal));
                break;
            case  R.id.menu_me://我的
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mMainFragment)
                        .hide(mFindFragment)
                        .show(mMeFragment)
                        .commit();
                mainImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_main_normal));
                findImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_find_normal));
                meImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.nav_me_click));
                break;
        }
    }
}
