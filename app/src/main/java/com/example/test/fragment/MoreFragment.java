package com.example.test.fragment;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.test.ClassTabActivity;
import com.example.test.MainActivity;
import com.example.test.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MoreFragment extends Fragment {

    private Button backBtn;
    private Button exitDismissBtn;
    private ImageView classIconIV;
    private TextView gradeClassTV;
    private TextView classNameTV;
    private TextView teacherNameTv;
    private TextView termTV;
    private TextView schoolDepartmentTV;
    private TextView classIntructionTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        backBtn = view.findViewById(R.id.toolbar_left_btn);
        classIconIV = view.findViewById(R.id.more_fragment_Iv);
        gradeClassTV = view.findViewById(R.id.more_class_Tv);
        classNameTV = view.findViewById(R.id.more_course_Tv);
        teacherNameTv = view.findViewById(R.id.more_teacher_Tv);
        termTV = view.findViewById(R.id.more_term_Tv);
        schoolDepartmentTV = view.findViewById(R.id.cloud_school_Tv);
        classIntructionTV = view.findViewById(R.id.class_intruction_Tv);
        exitDismissBtn = view.findViewById(R.id.exit_dismiss_btn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        File classInfoFile;
        if(ClassTabActivity.enterType.equals("create")){
            classInfoFile = new File(Environment.getExternalStorageDirectory() + "/daoyun/"
                    + MainActivity.phoneNumber + ".json");
            exitDismissBtn.setText("解 散 班 课");
        }else{
            classInfoFile = new File(Environment.getExternalStorageDirectory() + "/daoyun/"
                    + MainActivity.phoneNumber + "_join.json");
        }
        exitDismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClassTabActivity.enterType.equals("create")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setMessage("暂不支持解散班课")
                            .setPositiveButton("确定", null);
                    builder.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setMessage("暂不支持退出班课")
                            .setPositiveButton("确定", null);
                    builder.show();
                }
            }
        });
        try {
            FileInputStream in = new FileInputStream(classInfoFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String classJsonStr = reader.readLine();
            JSONArray jsonArray = new JSONArray(classJsonStr);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("classId").equals(ClassTabActivity.classId)){
                    String classIcon = jsonObject.getString("classIcon");
                    if(!classIcon.equals("")){
                        classIconIV.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
                                + "/daoyun/" + classIcon));
                    }else{
                        classIconIV.setImageResource(R.drawable.course_img_1);
                    }
                    gradeClassTV.setText(jsonObject.getString("gradeClass"));
                    classNameTV.setText(jsonObject.getString("className"));
                    if(ClassTabActivity.enterType.equals("create")){
                        teacherNameTv.setText(MainActivity.name);
                    }else{
                        teacherNameTv.setText(jsonObject.getString("teacherName"));
                    }
                    termTV.setText(jsonObject.getString("term") + " 学校课表班课");
                    classIntructionTV.setText(jsonObject.getString("classIntruction"));
                    schoolDepartmentTV.setText(jsonObject.getString("schoolDepartment"));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }
}
