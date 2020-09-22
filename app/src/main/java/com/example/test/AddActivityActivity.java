package com.example.test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.test.adapter.ActivityAdapter;
import com.example.test.fragment.ActivityFragment;
import com.example.test.fragment.AllActivityFragment;
import com.example.test.fragment.DoingActivityFragment;
import com.example.test.fragment.FinishActivityFragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddActivityActivity extends AppCompatActivity {

    private Button backBtn;
    private EditText activityTitleET;
    private EditText activityDescriptionET;
    private LinearLayout deadlineDateLayout;
    private TextView deadlineDateTV;
    private LinearLayout deadlineTimeLayout;
    private TextView deadlineTimeTV;
    private LinearLayout experienceScoreLayout;
    private TextView experienceScoreTV;
    private Button createActivityBtn;
    private int select_year = 0;
    private int select_month = 0;
    private int select_day = 0;
    private int select_hour = 0;
    private int select_miniute = 0;
    private String selectExperienceScore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        backBtn = findViewById(R.id.toolbar_left_btn);
        activityTitleET = findViewById(R.id.activity_title_Et);
        activityDescriptionET = findViewById(R.id.activity_description_Et);
        deadlineDateLayout = findViewById(R.id.deadline_date_layout);
        deadlineDateTV = findViewById(R.id.deadline_date_Tv);
        deadlineTimeLayout = findViewById(R.id.deadline_time_layout);
        deadlineTimeTV = findViewById(R.id.deadline_time_Tv);
        experienceScoreLayout = findViewById(R.id.experience_score_layout);
        experienceScoreTV = findViewById(R.id.experience_score_Tv);
        createActivityBtn = findViewById(R.id.create_activity_btn);

        Calendar calendar = Calendar.getInstance();
        final int m_year = calendar.get(Calendar.YEAR);
        final int m_month = calendar.get(Calendar.MONTH);
        final int m_day = calendar.get(Calendar.DAY_OF_MONTH);
        final int m_hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int m_minute = calendar.get(Calendar.MINUTE);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        experienceScoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] experienceScore = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
                                                                "12", "13", "14", "15", "16", "17", "18", "19", "20"};
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AddActivityActivity.this)
                        .setTitle("选择活动经验值")
                        .setSingleChoiceItems(experienceScore, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectExperienceScore = experienceScore[which];
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        experienceScoreTV.setText(selectExperienceScore);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        deadlineDateLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivityActivity.this, onDateSetListener, m_year, m_month, m_day).show();
            }
        });

        deadlineTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AddActivity", "deadlineTime");
                new TimePickerDialog(AddActivityActivity.this, onTimeSetListener, m_hour, m_minute, true).show();
            }
        });

        createActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activityTitleET.getText().toString().equals("")){
                    showAlertDialog("活动标题不可为空！");
                }else if(activityDescriptionET.getText().toString().equals("")){
                    showAlertDialog("活动具体描述不可为空！");
                }else if(deadlineDateTV.getText().toString().equals("")){
                    showAlertDialog("请选择活动截止日期！");
                }else if(deadlineTimeTV.getText().toString().equals("")){
                    showAlertDialog("请选择活动截止时间！");
                }else if(experienceScoreTV.getText().toString().equals("")){
                    showAlertDialog("请选择本活动的经验值！");
                }else{
                    addActivityToServer();
                }
            }
        });
    }

    private void addActivityToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("phoneNumber", MainActivity.phoneNumber)
                        .add("classId", ClassTabActivity.classId)
                        .add("className", ClassTabActivity.courseName)
                        .add("activityTitle", activityTitleET.getText().toString())
                        .add("activityDescription", activityDescriptionET.getText().toString())
                        .add("experienceScore", selectExperienceScore)
                        .add("deadlineYear", String.valueOf(select_year))
                        .add("deadlineMonth", String.valueOf(select_month))
                        .add("deadlineDay", String.valueOf(select_day))
                        .add("deadlineHour", String.valueOf(select_hour))
                        .add("deadlineMinute", String.valueOf(select_miniute))
                        .build();
                Request request = new Request.Builder()
                        .url("http://47.98.236.0:8080/addactivity")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String responseBodyStr = response.body().string();
                        if(!responseBodyStr.equals("")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddActivityActivity.this)
                                            .setMessage("添加活动成功！")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    String currentTime = sdf.format(new Date());
                                                    String deadlineTime;
                                                    if(select_month >= 1 && select_month <= 9){
                                                        deadlineTime = select_year + "-0" + select_month;
                                                    }else{
                                                        deadlineTime = select_year + "-" + select_month;
                                                    }
                                                    if(select_day >= 1 && select_day <= 9){
                                                        deadlineTime += "-0" + select_day;
                                                    }else{
                                                        deadlineTime += "-" + select_day;
                                                    }
                                                    if(select_hour >= 0 && select_hour <= 9){
                                                        deadlineTime += " 0" + select_hour;
                                                    }else{
                                                        deadlineTime += " " + select_hour;
                                                    }
                                                    if(select_miniute >= 0 && select_miniute <= 9){
                                                        deadlineTime += ":0" + select_miniute;
                                                    }else{
                                                        deadlineTime += ":" + select_miniute;
                                                    }
                                                    deadlineTime += ":00";
                                                    String judgeResult = judgeDeadline(currentTime, deadlineTime);
                                                    if(judgeResult.equals("overtime")){
                                                        ActivityFragment.allEmpty = false;
                                                        ActivityFragment.finishEmpty = false;
                                                        ActivityFragment.allActivityNum++;
                                                        ActivityFragment.finishActivityNum++;
                                                        TaskActivity taskActivity = new TaskActivity(activityTitleET.getText().toString(), "0人参与",
                                                                "   ", selectExperienceScore+"经验值","已超时");
                                                        taskActivity.taskId = responseBodyStr;
                                                        FinishActivityFragment.activityList.add(taskActivity);
                                                        FinishActivityFragment.activityAdapter.notifyDataSetChanged();
//                                                        FinishActivityFragment.activityAdapter = new ActivityAdapter(AddActivityActivity.this, R.layout.activity_item, FinishActivityFragment.activityList);
//                                                        View view= getLayoutInflater().inflate(R.layout.fragment_activity_finish, null);
//                                                        FinishActivityFragment.listView = view.findViewById(R.id.activity_finish_list_view);
//                                                        FinishActivityFragment.listView.setAdapter(FinishActivityFragment.activityAdapter);

                                                        AllActivityFragment.activityList.add(taskActivity);
                                                        AllActivityFragment.activityAdapter.notifyDataSetChanged();
//                                                        AllActivityFragment.activityAdapter = new ActivityAdapter(AddActivityActivity.this, R.layout.activity_item, AllActivityFragment.activityList);
//                                                        View view1 = getLayoutInflater().inflate(R.layout.fragment_activity_all, null);
//                                                        AllActivityFragment.listView = view1.findViewById(R.id.activity_all_list_view);
//                                                        AllActivityFragment.listView.setAdapter(AllActivityFragment.activityAdapter);
                                                    }else{
                                                        ActivityFragment.allEmpty = false;
                                                        ActivityFragment.doingEmpty = false;
                                                        ActivityFragment.allActivityNum++;
                                                        ActivityFragment.doingActivityNum++;
                                                        TaskActivity taskActivity = new TaskActivity(activityTitleET.getText().toString(), "0人参与",
                                                                "   ", selectExperienceScore + "经验值", judgeResult);
                                                        taskActivity.taskId = responseBodyStr;
                                                        DoingActivityFragment.activityList.add(taskActivity);
                                                        DoingActivityFragment.activityAdapter.notifyDataSetChanged();
//                                                        DoingActivityFragment.activityAdapter = new ActivityAdapter(AddActivityActivity.this, R.layout.activity_item, DoingActivityFragment.activityList);
//                                                        View view= getLayoutInflater().inflate(R.layout.fragment_activity_doing, null);
//                                                        DoingActivityFragment.listView = view.findViewById(R.id.activity_doing_list_view);
//                                                        DoingActivityFragment.listView.setAdapter(DoingActivityFragment.activityAdapter);

                                                        AllActivityFragment.activityList.add(taskActivity);
                                                        AllActivityFragment.activityAdapter.notifyDataSetChanged();
//                                                        AllActivityFragment.activityAdapter = new ActivityAdapter(AddActivityActivity.this, R.layout.activity_item, AllActivityFragment.activityList);
//                                                        View view1= getLayoutInflater().inflate(R.layout.fragment_activity_all, null);
//                                                        AllActivityFragment.listView = view1.findViewById(R.id.activity_all_list_view);
//                                                        AllActivityFragment.listView.setAdapter(AllActivityFragment.activityAdapter);
                                                    }
                                                    Intent intent = new Intent();
                                                    setResult(RESULT_OK, intent);
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
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            deadlineDateTV.setText(year + "年" + (month+1) + "月" + dayOfMonth + "日");
            select_year = year;
            select_month = month + 1;
            select_day = dayOfMonth;
        }
    };

    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            deadlineTimeTV.setText(hourOfDay + "点" + minute + "分");
            select_hour = hourOfDay;
            select_miniute = minute;
        }
    };

    public void showAlertDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivityActivity.this)
                .setMessage(msg)
                .setPositiveButton("确定", null);
        builder.show();
    }

    public String judgeDeadline(String currentTime, String deadline){
        AddActivityActivity.TimeClass currentTimeClass = new AddActivityActivity.TimeClass(currentTime);
        Log.i("ActiFragInfo", currentTimeClass.year+"年"+currentTimeClass.month+"月"+
                currentTimeClass.day+"日"+currentTimeClass.hour+"时"+currentTimeClass.minute+"分"+
                currentTimeClass.second+"秒");
        AddActivityActivity.TimeClass deadlineClass = new AddActivityActivity.TimeClass(deadline);
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
