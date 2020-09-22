package com.example.test.fragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.AddActivityActivity;
import com.example.test.ClassTabActivity;
import com.example.test.MainActivity;
import com.example.test.TaskActivity;
import com.example.test.adapter.ActivityAdapter;
import com.example.test.fragment.AllActivityFragment;
import com.example.test.fragment.DoingActivityFragment;
import com.example.test.fragment.FinishActivityFragment;

import com.example.test.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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

public class ActivityFragment extends Fragment {

    private LinearLayout allActivityLayout;
    private LinearLayout doingActivityLayout;
    private LinearLayout finishActivityLayout;

    private TextView allActivityTV;
    private TextView doingActivityTV;
    private TextView finishActivityTV;

    private TextView allActivityNumTV;
    private TextView doingActivityNumTV;
    private TextView finishActivityNumTV;

    private View allActivityView;
    private View doingActivityView;
    private View finishActivityView;

    private AllActivityFragment allActivityFragment = new AllActivityFragment();
    private DoingActivityFragment doingActivityFragment = new DoingActivityFragment();
    private FinishActivityFragment finishActivityFragment = new FinishActivityFragment();
    private EmptyAllActivityFragment emptyAllActivityFragment = new EmptyAllActivityFragment();
    private EmptyDoingActivityFragment emptyDoingActivityFragment = new EmptyDoingActivityFragment();
    private EmptyFinishActivityFragment emptyFinishActivityFragment = new EmptyFinishActivityFragment();

    private TextView addTV;
    private Button backBtn;
    private TextView titleTV;

    public static boolean allEmpty = true;
    public static boolean doingEmpty = true;
    public static boolean finishEmpty = true;

    public static int allActivityNum = 0;
    public static int doingActivityNum = 0;
    public static int finishActivityNum = 0;

    public int tab = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity,container,false);
        addTV = view.findViewById(R.id.toolbar_right_tv);
        backBtn = view.findViewById(R.id.toolbar_left_btn);
        titleTV = view.findViewById(R.id.toolbar_title_tv);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        allActivityNum = 0;
        doingActivityNum = 0;
        finishActivityNum = 0;

        titleTV.setText(ClassTabActivity.courseName);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        addTV.setEnabled(true);
        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClassTabActivity.enterType.equals("join")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setMessage("您没有在此班课下添加活动的权限！")
                            .setPositiveButton("确定", null);
                    builder.show();
                }else{
                    startActivityForResult(new Intent(getContext(), AddActivityActivity.class), 1);
                }
            }
        });

        allActivityLayout = activity.findViewById(R.id.activity_all_layout);
        doingActivityLayout = activity.findViewById(R.id.activity_doing_layout);
        finishActivityLayout = activity.findViewById(R.id.activity_finish_layout);

        allActivityTV = activity.findViewById(R.id.activity_all_Tv);
        doingActivityTV = activity.findViewById(R.id.activity_doing_Tv);
        finishActivityTV = activity.findViewById(R.id.activity_finish_Tv);

        allActivityNumTV = activity.findViewById(R.id.activity_all_num_Tv);
        doingActivityNumTV = activity.findViewById(R.id.activity_doing_num_Tv);
        finishActivityNumTV = activity.findViewById(R.id.activity_finish_num_Tv);

        allActivityView = activity.findViewById(R.id.activity_all_view);
        doingActivityView = activity.findViewById(R.id.activity_doing_view);
        finishActivityView = activity.findViewById(R.id.activity_finish_view);

//        if(ClassTabActivity.courseName.equals("工程实践")){
//            doingActivityNumTV.setText("12");
//            allActivityNumTV.setText("12");
//        }else if(ClassTabActivity.courseName.equals("工程训练")){
//            doingActivityNumTV.setText("9");
//            allActivityNumTV.setText("9");
//        }

        doingActivityTV.setTextColor(Color.parseColor("#ff00bfff"));
        doingActivityNumTV.setTextColor(Color.parseColor("#ff00bfff"));
        doingActivityView.setBackgroundColor(Color.parseColor("#ff00bfff"));

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_content_activity_layout, allActivityFragment)
                .add(R.id.container_content_activity_layout, doingActivityFragment)
                .add(R.id.container_content_activity_layout, finishActivityFragment)
                .add(R.id.container_content_activity_layout, emptyAllActivityFragment)
                .add(R.id.container_content_activity_layout, emptyDoingActivityFragment)
                .add(R.id.container_content_activity_layout, emptyFinishActivityFragment)
                .hide(allActivityFragment)
                .hide(finishActivityFragment)
                .hide(doingActivityFragment)
                .hide(emptyAllActivityFragment)
                .hide(emptyFinishActivityFragment)
                .commit();

        allActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 1;
                allActivityTV.setTextColor(Color.parseColor("#ff00bfff"));
                allActivityNumTV.setTextColor(Color.parseColor("#ff00bfff"));
                allActivityView.setBackgroundColor(Color.parseColor("#ff00bfff"));
                doingActivityTV.setTextColor(Color.parseColor("#80000000"));
                doingActivityNumTV.setTextColor(Color.parseColor("#80000000"));
                doingActivityView.setBackgroundColor(Color.parseColor("#D8DDE1"));
                finishActivityTV.setTextColor(Color.parseColor("#80000000"));
                finishActivityNumTV.setTextColor(Color.parseColor("#80000000"));
                finishActivityView.setBackgroundColor(Color.parseColor("#D8DDE1"));
                if(allEmpty == true){
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .hide(allActivityFragment)
                            .hide(doingActivityFragment)
                            .hide(finishActivityFragment)
                            .hide(emptyDoingActivityFragment)
                            .hide(emptyFinishActivityFragment)
                            .show(emptyAllActivityFragment)
                            .commit();
                }else{
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .show(allActivityFragment)
                            .hide(doingActivityFragment)
                            .hide(finishActivityFragment)
                            .hide(emptyDoingActivityFragment)
                            .hide(emptyFinishActivityFragment)
                            .hide(emptyAllActivityFragment)
                            .commit();
                }

            }
        });

        doingActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 2;
                allActivityTV.setTextColor(Color.parseColor("#80000000"));
                allActivityNumTV.setTextColor(Color.parseColor("#80000000"));
                allActivityView.setBackgroundColor(Color.parseColor("#D8DDE1"));
                doingActivityTV.setTextColor(Color.parseColor("#ff00bfff"));
                doingActivityNumTV.setTextColor(Color.parseColor("#ff00bfff"));
                doingActivityView.setBackgroundColor(Color.parseColor("#ff00bfff"));
                finishActivityTV.setTextColor(Color.parseColor("#80000000"));
                finishActivityNumTV.setTextColor(Color.parseColor("#80000000"));
                finishActivityView.setBackgroundColor(Color.parseColor("#D8DDE1"));
                if(doingEmpty == true){
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .hide(allActivityFragment)
                            .hide(doingActivityFragment)
                            .hide(finishActivityFragment)
                            .show(emptyDoingActivityFragment)
                            .hide(emptyFinishActivityFragment)
                            .hide(emptyAllActivityFragment)
                            .commit();
                }else{
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .hide(allActivityFragment)
                            .show(doingActivityFragment)
                            .hide(finishActivityFragment)
                            .hide(emptyDoingActivityFragment)
                            .hide(emptyFinishActivityFragment)
                            .hide(emptyAllActivityFragment)
                            .commit();
                }
            }
        });

        finishActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 3;
                allActivityTV.setTextColor(Color.parseColor("#80000000"));
                allActivityNumTV.setTextColor(Color.parseColor("#80000000"));
                allActivityView.setBackgroundColor(Color.parseColor("#D8DDE1"));
                doingActivityTV.setTextColor(Color.parseColor("#80000000"));
                doingActivityNumTV.setTextColor(Color.parseColor("#80000000"));
                doingActivityView.setBackgroundColor(Color.parseColor("#D8DDE1"));
                finishActivityTV.setTextColor(Color.parseColor("#ff00bfff"));
                finishActivityNumTV.setTextColor(Color.parseColor("#ff00bfff"));
                finishActivityView.setBackgroundColor(Color.parseColor("#ff00bfff"));
                if(finishEmpty == true){
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .hide(allActivityFragment)
                            .hide(doingActivityFragment)
                            .hide(finishActivityFragment)
                            .hide(emptyDoingActivityFragment)
                            .show(emptyFinishActivityFragment)
                            .hide(emptyAllActivityFragment)
                            .commit();
                }else{
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .hide(allActivityFragment)
                            .hide(doingActivityFragment)
                            .show(finishActivityFragment)
                            .hide(emptyDoingActivityFragment)
                            .hide(emptyFinishActivityFragment)
                            .hide(emptyAllActivityFragment)
                            .commit();
                }
            }
        });

        getClassActivity();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == getActivity().RESULT_OK){
            Log.i("ActiFragInfo", "onActivityResulted");
            allActivityNumTV.setText(String.valueOf(allActivityNum));
            doingActivityNumTV.setText(String.valueOf(doingActivityNum));
            finishActivityNumTV.setText(String.valueOf(finishActivityNum));
            if(tab == 1 && allEmpty == false){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .show(allActivityFragment)
                        .hide(doingActivityFragment)
                        .hide(finishActivityFragment)
                        .hide(emptyDoingActivityFragment)
                        .hide(emptyFinishActivityFragment)
                        .hide(emptyAllActivityFragment)
                        .commit();
            }else if(tab == 1 && allEmpty == true){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .hide(allActivityFragment)
                        .hide(doingActivityFragment)
                        .hide(finishActivityFragment)
                        .hide(emptyDoingActivityFragment)
                        .hide(emptyFinishActivityFragment)
                        .show(emptyAllActivityFragment)
                        .commit();
            }else if(tab == 2 && doingEmpty == false){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .hide(allActivityFragment)
                        .show(doingActivityFragment)
                        .hide(finishActivityFragment)
                        .hide(emptyDoingActivityFragment)
                        .hide(emptyFinishActivityFragment)
                        .hide(emptyAllActivityFragment)
                        .commit();
            }else if(tab == 2 && doingEmpty == true){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .hide(allActivityFragment)
                        .hide(doingActivityFragment)
                        .hide(finishActivityFragment)
                        .show(emptyDoingActivityFragment)
                        .hide(emptyFinishActivityFragment)
                        .hide(emptyAllActivityFragment)
                        .commit();
            }else if(tab == 3 && finishEmpty == false){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .hide(allActivityFragment)
                        .hide(doingActivityFragment)
                        .show(finishActivityFragment)
                        .hide(emptyDoingActivityFragment)
                        .hide(emptyFinishActivityFragment)
                        .hide(emptyAllActivityFragment)
                        .commit();
            }else if(tab == 3 && finishEmpty == true){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .hide(allActivityFragment)
                        .hide(doingActivityFragment)
                        .hide(finishActivityFragment)
                        .hide(emptyDoingActivityFragment)
                        .show(emptyFinishActivityFragment)
                        .hide(emptyAllActivityFragment)
                        .commit();
            }
        }
    }

    public void getClassActivity(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("classId", ClassTabActivity.classId)
                        .add("phoneNumber", MainActivity.phoneNumber)
                        .build();
                Request request = new Request.Builder()
                        .url("http://47.98.236.0:8080/classactivity")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseBodyStr = response.body().string();
                        if(responseBodyStr.equals("no_class_activity")){
                            allEmpty = true;
                            doingEmpty = true;
                            finishEmpty = true;
                        }else{
                            allEmpty = true;
                            doingEmpty = true;
                            finishEmpty = true;
                            File classActivityFile = new File(Environment.getExternalStorageDirectory() + "/daoyun/"
                                    + ClassTabActivity.classId + "_activity.json");
                            if(classActivityFile.exists()){
                                classActivityFile.delete();
                            }
                            classActivityFile.createNewFile();
                            byte[] bt = new byte[4096];
                            bt = responseBodyStr.getBytes();
                            FileOutputStream out = new FileOutputStream(classActivityFile);
                            out.write(bt, 0, bt.length);
                            out.close();
                            try {
                                parseJsonArray(responseBodyStr);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public void parseJsonArray(final String jsonData) throws JSONException {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(jsonData);
                    for(int i = 0 ; i < jsonArray.length() ; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String activityId = jsonObject.getString("activityId");
                        String activityTitle = jsonObject.getString("activityTitle");
                        String activityContent = jsonObject.getString("activityContent");
                        String experineceScore = jsonObject.getString("experineceScore");
                        String deadline = jsonObject.getString("deadline");
                        String joinedNum = jsonObject.getString("joinedNum");
                        String submitOrNot = jsonObject.getString("submitOrNot");
                        if(submitOrNot.equals("yes")){
                            submitOrNot = "已参与";
                        }else{
                            submitOrNot = "未参与";
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentTime = sdf.format(new Date());
                        Log.i("ActiFragInfo", currentTime);
                        Log.i("ActiFragInfo", deadline);
                        String judgeResult = judgeDeadline(currentTime, deadline);
                        Log.i("ActiFragInfo", judgeResult);
                        if(judgeResult.equals("overtime")){
                            allEmpty = false;
                            finishEmpty = false;
                            allActivityNum++;
                            finishActivityNum++;
                            TaskActivity taskActivity;
                            if(ClassTabActivity.enterType.equals("create")){
                                Log.i("ActiFragInfo", "ClassTabActivity.enterType");
                                taskActivity = new TaskActivity(activityTitle, joinedNum + "人参与",
                                        "   ", experineceScore+"经验值","已超时");
                            }else{
                                taskActivity = new TaskActivity(activityTitle, joinedNum + "人参与",
                                        submitOrNot, experineceScore+"经验值","已超时");
                            }
                            taskActivity.taskId = activityId;
                            taskActivity.taskContent = activityContent;
                            FinishActivityFragment.activityList.add(taskActivity);
                            FinishActivityFragment.activityAdapter.notifyDataSetChanged();
//                FinishActivityFragment.activityAdapter = new ActivityAdapter(getContext(), R.layout.activity_item, FinishActivityFragment.activityList);
//                FinishActivityFragment.listView = getActivity().findViewById(R.id.activity_finish_list_view);
//                FinishActivityFragment.listView.setAdapter(FinishActivityFragment.activityAdapter);

                            AllActivityFragment.activityList.add(taskActivity);
                            AllActivityFragment.activityAdapter.notifyDataSetChanged();
//                AllActivityFragment.activityAdapter = new ActivityAdapter(getContext(), R.layout.activity_item, AllActivityFragment.activityList);
//                AllActivityFragment.listView = getActivity().findViewById(R.id.activity_all_list_view);
//                AllActivityFragment.listView.setAdapter(AllActivityFragment.activityAdapter);
                        }else{
                            allEmpty = false;
                            doingEmpty = false;
                            allActivityNum++;
                            doingActivityNum++;
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .hide(allActivityFragment)
                                    .show(doingActivityFragment)
                                    .hide(finishActivityFragment)
                                    .hide(emptyDoingActivityFragment)
                                    .hide(emptyFinishActivityFragment)
                                    .hide(emptyAllActivityFragment)
                                    .commit();
                            TaskActivity taskActivity;
                            if(ClassTabActivity.enterType.equals("create")){
                                Log.i("ActiFragInfo", "ClassTabActivity.enterType");
                                taskActivity = new TaskActivity(activityTitle, joinedNum + "人参与",
                                        "   ", experineceScore+"经验值", judgeResult);
                            }else{
                                taskActivity = new TaskActivity(activityTitle, joinedNum + "人参与",
                                        submitOrNot, experineceScore+"经验值", judgeResult);
                            }
                            taskActivity.taskId = activityId;
                            taskActivity.taskContent = activityContent;
                            DoingActivityFragment.activityList.add(taskActivity);
                            DoingActivityFragment.activityAdapter.notifyDataSetChanged();
//                DoingActivityFragment.activityAdapter = new ActivityAdapter(getContext(), R.layout.activity_item, DoingActivityFragment.activityList);
//                DoingActivityFragment.listView = getActivity().findViewById(R.id.activity_doing_list_view);
//                DoingActivityFragment.listView.setAdapter(DoingActivityFragment.activityAdapter);

                            AllActivityFragment.activityList.add(taskActivity);
                            AllActivityFragment.activityAdapter.notifyDataSetChanged();
//                AllActivityFragment.activityAdapter = new ActivityAdapter(getContext(), R.layout.activity_item, AllActivityFragment.activityList);
//                AllActivityFragment.listView = getActivity().findViewById(R.id.activity_all_list_view);
//                AllActivityFragment.listView.setAdapter(AllActivityFragment.activityAdapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                allActivityNumTV.setText(String.valueOf(allActivityNum));
                doingActivityNumTV.setText(String.valueOf(doingActivityNum));
                finishActivityNumTV.setText(String.valueOf(finishActivityNum));
            }
        });
    }

    public String judgeDeadline(String currentTime, String deadline){
        TimeClass currentTimeClass = new TimeClass(currentTime);
        Log.i("ActiFragInfo", currentTimeClass.year+"年"+currentTimeClass.month+"月"+
                currentTimeClass.day+"日"+currentTimeClass.hour+"时"+currentTimeClass.minute+"分"+
                currentTimeClass.second+"秒");
        TimeClass deadlineClass = new TimeClass(deadline);
        Log.i("ActiFragInfo", deadlineClass.year+"年"+deadlineClass.month+"月"+
                deadlineClass.day+"日"+deadlineClass.hour+"时"+deadlineClass.minute+"分"+
                deadlineClass.second+"秒");
        int flag = 0;
        if(currentTimeClass.year > deadlineClass.year){
            flag = 1;
        }else if(currentTimeClass.year == deadlineClass.year && currentTimeClass.month > deadlineClass.month){
            flag = 1;
        }else if(currentTimeClass.year == deadlineClass.year && currentTimeClass.month == deadlineClass.month
                && currentTimeClass.day > deadlineClass.day){
            flag = 1;
        }else if(currentTimeClass.year == deadlineClass.year && currentTimeClass.month == deadlineClass.month
                && currentTimeClass.day == deadlineClass.day && currentTimeClass.hour > deadlineClass.hour){
            flag = 1;
        }else if(currentTimeClass.year == deadlineClass.year && currentTimeClass.month == deadlineClass.month
                && currentTimeClass.day == deadlineClass.day && currentTimeClass.hour == deadlineClass.hour
                && currentTimeClass.minute > deadlineClass.minute){
            flag = 1;
        }else if(currentTimeClass.year == deadlineClass.year && currentTimeClass.month == deadlineClass.month
                && currentTimeClass.day == deadlineClass.day && currentTimeClass.hour == deadlineClass.hour
                && currentTimeClass.minute == deadlineClass.minute && currentTimeClass.second > deadlineClass.second){
            flag = 1;
        }
        if(flag == 1){
            return "overtime";
        }else{
            int currentDayIndex = dayIndexInYear(currentTimeClass.year, currentTimeClass.month, currentTimeClass.day);
            int deadlineDayIndex = dayIndexInYear(deadlineClass.year, deadlineClass.month, deadlineClass.day);
            int apartDays = 0;
            int apartHours = 0;
            int apartMinutes = 0;
            if(currentTimeClass.year == deadlineClass.year){
                apartDays = deadlineDayIndex - currentDayIndex;
            }else{
                for(int i = currentTimeClass.year ; i <= deadlineClass.year ; i++){
                    boolean leapYearOrNot = judgeLeapYear(i);
                    if(i == currentTimeClass.year){
                        apartDays = leapYearOrNot ? (366 - currentDayIndex) : (365 - currentDayIndex);
                    }else if(i == deadlineClass.year){
                        apartDays += deadlineDayIndex;
                    }else{
                        apartDays += leapYearOrNot ? 366 : 365;
                    }
                }
            }
            if((currentTimeClass.hour > deadlineClass.hour) || (currentTimeClass.hour == deadlineClass.hour
                && currentTimeClass.minute > deadlineClass.minute)){
                apartDays--;
                apartHours = 24 - currentTimeClass.hour + deadlineClass.hour;
                if(currentTimeClass.minute > deadlineClass.minute){
                    apartHours--;
                    apartMinutes = 60 - currentTimeClass.minute + deadlineClass.minute;
                }else {
                    apartMinutes = deadlineClass.minute - currentTimeClass.minute;
                }
            }else{
                apartHours = deadlineClass.hour - currentTimeClass.hour;
                if(currentTimeClass.minute > deadlineClass.minute){
                    apartHours--;
                    apartMinutes = 60 - currentTimeClass.minute + deadlineClass.minute;
                }else{
                    apartMinutes = deadlineClass.minute - currentTimeClass.minute;
                }
            }
            return apartDays + "天" + apartHours + "小时" + apartMinutes + "分钟";
        }

    }

    public int dayIndexInYear(int year, int month, int day){
        boolean leapYear = judgeLeapYear(year);
        int[] monthDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int temp_day = 0;
        int final_day = 0;

        for(int i = 0 ; i < month-1 ; i ++){
            if(i == 1 && leapYear == true){
                temp_day += 29;
            }else{
                temp_day += monthDay[i];
            }
        }
        final_day = temp_day + day;
        return final_day;
    }

    public boolean judgeLeapYear(int year){
        boolean leapYear;
        if(year % 400 == 0){
            leapYear = true;
        }else if(year % 100 == 0){
            leapYear = false;
        }else if(year % 4 == 0){
            leapYear = true;
        }else{
            leapYear = false;
        }
        return  leapYear;
    }

    public class TimeClass{
        public int year;
        public int month;
        public int day;
        public int hour;
        public int minute;
        public int second;
        public TimeClass(String time){
            year = Integer.valueOf(time.substring(0, 4));
            int splitIndex = time.indexOf("-", 5);
            month = Integer.valueOf(time.substring(5, splitIndex));
            int spaceIndex = time.indexOf(" ");
            day = Integer.valueOf(time.substring(splitIndex + 1, spaceIndex));
            int colonIndex = time.indexOf(":");
            hour = Integer.valueOf(time.substring(spaceIndex + 1, colonIndex));
            int colonIndex2 = time.indexOf(":", colonIndex+1);
            minute = Integer.valueOf(time.substring(colonIndex + 1, colonIndex2));
            second = Integer.valueOf(time.substring(colonIndex2 + 1));
        }
    }

}
