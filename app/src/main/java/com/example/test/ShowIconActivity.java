package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.test.fragment.MeFragment;


public class ShowIconActivity extends AppCompatActivity {

    private ImageView iconIV;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_icon);
        iconIV = findViewById(R.id.icon_Iv);
        backBtn = findViewById(R.id.toolbar_left_btn);
        if(MeFragment.iconFile == null){
            iconIV.setImageResource(R.drawable.course_img_1);
            Log.i("ShowIconInfo", "null");
        }else{
            iconIV.setImageBitmap(BitmapFactory.decodeFile(MeFragment.iconFile.getAbsolutePath()));
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
