package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskResultActivity extends AppCompatActivity {

    private List<Member> memberList = new ArrayList<>();
    private  MemberAdapter memberAdapter;
    private TextView titleTV;
    private Button backBtn;
    private String taskId;
    private TextView submitNumTV;
    private ListView listView;
    private List<Integer> experienceList = new ArrayList<>();
    private List<Integer> indexList = new ArrayList<>();
    private List<Member> tempMemberList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_result);
        Log.i("TaskReInfo", "taskId");
        submitNumTV = findViewById(R.id.submit_num_Tv);
        initMember();
        titleTV = findViewById(R.id.toolbar_title_tv);
        Intent intent = getIntent();
        taskId = intent.getStringExtra("taskId");
        Log.i("TaskReInfo", taskId);
        titleTV.setText(intent.getStringExtra("taskName"));

        backBtn = findViewById(R.id.toolbar_left_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void initMember(){
//        Member member_1 = new Member("1", R.drawable.course_img_1, "student1",
//                "190327051","64经验值");
//        memberList.add(member_1);
//        Member member_2 = new Member("2", R.drawable.course_img_2, "student2",
//                "190327090","62经验值");
//        memberList.add(member_2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("taskId", taskId)
                        .build();
                Request request = new Request.Builder()
                        .url("http://47.98.236.0:8080/submitlist")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseBodyStr = response.body().string();
                        Log.i("TaskReInfo", responseBodyStr);
                        if(responseBodyStr.equals("nobody_submit")){

                        }else{
                            try {
                                parseSubmitedList(responseBodyStr);
                                afterAction();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();

    }

    public void parseSubmitedList(String JsonArrayData) throws JSONException {
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
            experienceList.add(Integer.valueOf(experienceScore));
            indexList.add(rank-1);
            if(icon.equals("")){
                Member member;
                if(name.equals("")){
                    member = new Member(String.valueOf(rank), R.drawable.course_img_1, phoneNumber, IDNumber, experienceScore+"经验值");
                }else{
                    member = new Member(String.valueOf(rank), R.drawable.course_img_1, name, IDNumber, experienceScore+"经验值");
                }
                memberList.add(member);
                tempMemberList.add(member);

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
                                    tempMemberList.add(member);
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
                    tempMemberList.add(member);
//                    memberAdapter.notifyDataSetChanged();
                }

            }
        }
        final int finalRank2 = rank;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                submitNumTV.setText(finalRank2 + "人");
            }
        });
    }

    public void afterAction(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = experienceList.size() - 1 ; i >= 0; i--){
                    for(int m = 0 ; m < i ; m++){
                        if(experienceList.get(m) < experienceList.get(m+1)){
                            int temp = experienceList.get(m);
                            experienceList.set(m, experienceList.get(m+1));
                            experienceList.set(m+1, temp);
                            temp = indexList.get(m);
                            indexList.set(m, indexList.get(m+1));
                            indexList.set(m+1, temp);
                        }
                    }
                }
                for(int i = 0 ; i < experienceList.size() ; i++){
                    tempMemberList.set(i, memberList.get(indexList.get(i)));
                }
                memberList = tempMemberList;
                int index = 1;
                for(int i = 0 ; i < experienceList.size() ; i++){
                    if(i == 0){
                        indexList.set(i, 1);
                    }else if(experienceList.get(i) == experienceList.get(i-1)){
                        indexList.set(i, index);
                    }else if(experienceList.get(i) != experienceList.get(i-1)){
                        index++;
                        indexList.set(i, index);
                    }
                    memberList.get(i).setRanking(String.valueOf(indexList.get(i)));
                }
                memberAdapter = new MemberAdapter(TaskResultActivity.this, R.layout.member_item, memberList);
                listView = findViewById(R.id.submit_list_view);
                listView.setAdapter(memberAdapter);
//        Utility.setListViewHeightBasedOnChildren(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Member member = memberList.get(position);
                        Toast.makeText(TaskResultActivity.this, member.getMemberName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
