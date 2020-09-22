package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.fragment.MeFragment;
import com.example.test.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.*;

public class UserInfoActivity extends AppCompatActivity {

    protected RadioButton maleRadioButton;
    protected RadioButton femaleRadioButton;
    protected RadioButton teacherRadioButton;
    protected RadioButton studentRadioButton;
    protected RadioButton elseRadioButton;
    protected Button backButton;
    protected TextView userNameTV;
    protected TextView userPhoneTV;
    protected LinearLayout birthYearLayout;
    protected TextView birthYearTV;
    protected LinearLayout schoolLayout;
    protected TextView schoolTV;
    protected Button saveBtn;
    protected EditText nameET;
    protected EditText idET;
    protected EditText userNameET;
    protected EditText emailET;
    protected String schoolStr = null;
    protected String nameStr;
    protected String sex;
    protected String role;
    protected String idNum;
    protected String selectedYear;
    protected LinearLayout editUserIconLayout;
    protected ImageView userIconIV;
    protected Dialog mCameraDialog;
    private static final int  IMAGE_SELECT=1;
    private static final int  IMAGE_CUT=2;
    private static final int  IMAGE_CAPTURE=3;
    private Uri iconUri;
    private String timeStamp;
    private Uri cropImageUri;
    private File camera_file = null;
    private File cropFile = null;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        path = Environment.getExternalStorageDirectory() + File.separator + "daoyun";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        Log.i("UserInfoInfo", "start");

        maleRadioButton = findViewById(R.id.rbtn_male);
        femaleRadioButton = findViewById(R.id.rbtn_female);
        teacherRadioButton = findViewById(R.id.rbtn_teacher);
        studentRadioButton = findViewById(R.id.rbtn_student);
        elseRadioButton = findViewById(R.id.rbtn_else);
        userNameTV = findViewById(R.id.user_name_Tv);
        userPhoneTV = findViewById(R.id.user_phone_Tv);
        birthYearLayout = findViewById(R.id.birth_year_layout);
        birthYearTV = findViewById(R.id.birth_year_Tv);
        schoolLayout = findViewById(R.id.school_layout);
        schoolTV = findViewById(R.id.school_Tv);
        saveBtn = findViewById(R.id.save_info_btn);
        nameET = findViewById(R.id.name_Et);
        idET = findViewById(R.id.id_Et);
        userNameET = findViewById(R.id.userName_Et);
        emailET = findViewById(R.id.email_Et);
        editUserIconLayout = findViewById(R.id.edit_user_icon_layout);
        userIconIV = findViewById(R.id.edit_user_icon_Iv);


//        userNameTV.setText(MainActivity.userName);
        if(MainActivity.userName.equals("admin")){
            userPhoneTV.setText("15900000001");
        }else if(MainActivity.userName.equals("teacher")){
            userPhoneTV.setText("15900000002");
        }else if(MainActivity.userName.equals("student1")){
            userPhoneTV.setText("15900000003");
        }else  if(MainActivity.userName.equals("student2")){
            userPhoneTV.setText("15900000004");
        }else{
            userPhoneTV.setText(MainActivity.userName);
        }
        initUi();

        editUserIconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog();
            }
        });

        userIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserInfoActivity.this, ShowIconActivity.class));
            }
        });

        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    boolean flag = false;
                    try {
                        String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                        Pattern regex = Pattern.compile(check);
                        Matcher matcher = regex.matcher(emailET.getText().toString());
                        flag = matcher.matches();
                    } catch (Exception e) {
                        flag = false;
                    }
                    if(!flag){
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this)
                                .setMessage("邮箱名不合法，请重新输入！")
                                .setPositiveButton("确定", null);
                        builder.show();
                        emailET.setText(null);
                    }
                }
            }
        });

        final String[] year = new String[]{"1987年", "1988年", "1989年", "1990年", "1991年", "1992年", "1993年",
                "1994年", "1995年", "1996年", "1997年", "1998年", "1999年", "2000年", "2001年", "2002年", "2003年",
                "2004年", "2005年"};
        birthYearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this)
                        .setTitle("选择出生年份")
                        .setSingleChoiceItems(year, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedYear = year[which];
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        birthYearTV.setText(selectedYear);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        schoolLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(UserInfoActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this)
                        .setTitle("请输入学校及院系")
                        .setView(editText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        schoolStr = editText.getText().toString();
                        schoolTV.setText(schoolStr);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        maleRadioButton.setOnCheckedChangeListener(mChangeListener);
        femaleRadioButton.setOnCheckedChangeListener(mChangeListener);
        teacherRadioButton.setOnCheckedChangeListener(mChangeListener_1);
        studentRadioButton.setOnCheckedChangeListener(mChangeListener_1);
        elseRadioButton.setOnCheckedChangeListener(mChangeListener_1);

        backButton = findViewById(R.id.toolbar_left_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        nameStr = nameET.getText().toString();
                        idNum = idET.getText().toString();
                        Log.i("returnLoginInfo",MainActivity.userName + " " + nameStr + ""
                        + idNum + " " + selectedYear + " " + sex + " " + schoolStr + " " + role);
                        if(nameStr.equals("")){
                            showAlertDialog("请输入姓名！");
                        }else if(userNameET.getText().toString().equals("")){
                            showAlertDialog("请输入用户名！");
                        }else if(emailET.getText().toString().equals("")){
                            showAlertDialog("请输入邮箱");
                        }else if(selectedYear.equals("")){
                            showAlertDialog("请选择出生年份！");
                        }else if(schoolStr == null){
                            showAlertDialog("请输入学校及院系！");
                        }else if(idNum.equals("")){
                            showAlertDialog("请输入学号/工号！");
                        }else{
                            OkHttpClient okHttpClient = new OkHttpClient();
//                        String str = "loginName=" + MainActivity.userName;
//                        str = catString(str, "userName", userNameET.getText().toString());
//                        str = catString(str, "email", emailET.getText().toString());
//                        str = catString(str, "realName", nameStr);
//                        str = catString(str, "studentId", idNum);
//                        str = catString(str, "birthday", selectedYear);
//                        str = catString(str, "sex", sex);
//                        str = catString(str, "school", schoolStr);
//                        str = catString(str, "role", role);
//                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"),
//                                str);

                            RequestBody requestBody;
                            if(cropFile != null){
                                requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("icon_image", cropFile.getName(),
                                                MultipartBody.create(MediaType.parse("img/*"), cropFile))
                                        .addFormDataPart("phoneNumber", MainActivity.phoneNumber)
                                        .addFormDataPart("userName", userNameET.getText().toString())
                                        .addFormDataPart("email", emailET.getText().toString())
                                        .addFormDataPart("realName", nameStr)
                                        .addFormDataPart("studentId", idNum)
                                        .addFormDataPart("birthday", selectedYear)
                                        .addFormDataPart("sex", sex)
                                        .addFormDataPart("school", schoolStr)
                                        .addFormDataPart("role", role)
                                        .build();
                            }else{
                                requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("phoneNumber", MainActivity.phoneNumber)
                                        .addFormDataPart("userName", userNameET.getText().toString())
                                        .addFormDataPart("email", emailET.getText().toString())
                                        .addFormDataPart("realName", nameStr)
                                        .addFormDataPart("studentId", idNum)
                                        .addFormDataPart("birthday", selectedYear)
                                        .addFormDataPart("sex", sex)
                                        .addFormDataPart("school", schoolStr)
                                        .addFormDataPart("role", role)
                                        .build();
                            }


//                        RequestBody requestBody = new FormBody.Builder()
//                                .add("loginName", MainActivity.userName)
//                                .add("userName", userNameET.getText().toString())
//                                .add("email", emailET.getText().toString())
//                                .add("realName", nameStr)
//                                .add("studentId", idNum)
//                                .add("birthday", selectedYear)
//                                .add("sex", sex)
//                                .add("school", schoolStr)
//                                .add("role", role)
//                                .build();

                            Request request = new Request.Builder()
                                    .url("http://47.98.236.0:8080/userinfosave")
                                    .post(requestBody)
                                    .build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Toast.makeText(UserInfoActivity.this, "Connection failed!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    String responseBodyStr = response.body().string();
                                    Log.i("returnLoginInfo", responseBodyStr);
                                    if(responseBodyStr.equals("save_success")){
                                        Log.i("returnLoginInfo", "responseBodyStr");
                                        showAlertDialog("您的个人信息保存成功！");
                                        if(cropFile != null){
                                            MeFragment.iconFile = new File(cropFile.getAbsolutePath());
                                        }
                                        if(!nameStr.equals("")){
                                            MainActivity.name = nameStr;
                                        }
                                    }else if(responseBodyStr.equals("userName_fail")){
                                        showAlertDialog("用户名设置成功后不可更改！");
                                        MeFragment.iconFile = cropFile;
                                    }else if(responseBodyStr.equals("userName_exist")){
                                        showAlertDialog("用户名已存在，请更换其他用户名！");
                                    }else if(responseBodyStr.equals("email_exist")){
                                        showAlertDialog("该邮箱已绑定其他用户，请更换您的其他邮箱！");
                                    }else if(responseBodyStr.equals("IDNumber_exist")){
                                        showAlertDialog("该学号/工号已存在，请重新输入！");
                                    }
                                }
                            });
                        }

                    }
                }).start();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCameraDialog.dismiss();
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode)
        {
            case RESULT_OK:
                if(requestCode == IMAGE_CAPTURE){
                    Uri uri = FileUtils.getUri(UserInfoActivity.this, "com.example.test.fileprovider", camera_file);
                    startCropImage(uri);
                }else if(requestCode == IMAGE_CUT){
                    Log.i("UserInfoInfo", Environment.getExternalStorageDirectory().toString());
//                    userIconIV.setImageURI(cropImageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(cropFile.getAbsolutePath());
                    userIconIV.setImageBitmap(bitmap);
                    Log.i("UserInfoInfo", "cropFile.toString()");
                }else if(requestCode == IMAGE_SELECT){
                    iconUri = data.getData();
                    startCropImage(iconUri);
                }
                break;
            default:
                break;
        }
    }

    private void setDialog(){
        mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, IMAGE_SELECT);
            }
        });
        root.findViewById(R.id.btn_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera_file = new File(path, System.currentTimeMillis() + ".jpg");
                try {
                    camera_file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iconUri = FileUtils.getUri(UserInfoActivity.this, "com.example.test.fileprovider", camera_file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, iconUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, IMAGE_CAPTURE);
            }
        });
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    private void startCropImage(Uri uri) {
        try{
            Intent intent = new Intent("com.android.camera.action.CROP");
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            intent.setDataAndType(uri, "image/*");
            // 使图片处于可裁剪状态
            intent.putExtra("crop", "true");
            // 裁剪框的比例（根据需要显示的图片比例进行设置）
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // 让裁剪框支持缩放
            intent.putExtra("scale", true);
            // 裁剪后图片的大小（注意和上面的裁剪比例保持一致）
            intent.putExtra("outputX", 1000);
            intent.putExtra("outputY", 1000);
            // 传递原图路径
//            cropFile = new File(Environment.getExternalStorageDirectory() + "/a/" + timeStamp + ".jpg");
            cropFile = new File(path + File.separator + MainActivity.userName + ".jpg");
            if(cropFile.exists()){
                cropFile.delete();
            }
            Uri temp_uri = Uri.fromFile(cropFile);
            cropImageUri = temp_uri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
            // 设置裁剪区域的形状，默认为矩形，也可设置为原形
            //intent.putExtra("circleCrop", true);
            // 设置图片的输出格式
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
            intent.putExtra("return-data", false);
            // 是否需要人脸识别
            intent.putExtra("noFaceDetection", true);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, IMAGE_CUT);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showAlertDialog(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if((msg.equals("您的个人信息保存成功!") || msg.equals("用户名设置成功后不可更改！")) && !userNameET.getText().toString().equals(null)){
                    userNameET.setEnabled(false);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this)
                        .setMessage(msg)
                        .setPositiveButton("确定", null);
                builder.show();
            }
        });
    }

    private String catString(String str, String str1, String str2){
        str = str + "&" + str1 + "=" + str2;
        return str;
    }

    private void initUi(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String str = "phoneNumber=" + MainActivity.phoneNumber;
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"),
                        str);
                Request request = new Request.Builder()
                        .url("http://47.98.236.0:8080/userinfoquery")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseBodyStr = response.body().string();
                        Log.i("returnLoginInfo", responseBodyStr);
                        parseUserInfo(responseBodyStr);
                    }
                });
            }
        }).start();
    }

    public void parseUserInfo(final String responseBodyStr){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject jsonObject = new JSONObject(responseBodyStr);
                    String phoneNumber = jsonObject.getString("phoneNumber");
                    if(!phoneNumber.equals("")){
                        userPhoneTV.setText(phoneNumber);
                    }
                    String userName = jsonObject.getString("userName");
                    if(!userName.equals("")){
                        userNameTV.setText(userName);
                        userNameET.setText(userName);
                        userNameET.setEnabled(false);
                    }
                    String email = jsonObject.getString("email");
                    if(!email.equals("")){
                        emailET.setText(email);
                    }
                    String IDNumber = jsonObject.getString("IDNumber");
                    if(!IDNumber.equals("")){
                        idET.setText(IDNumber);
                    }
                    String name = jsonObject.getString("name");
                    if(!name.equals("")){
                        nameET.setText(name);
                    }
                    String sex_temp = jsonObject.getString("sex");
                    if(sex_temp.equals("1")){
                        femaleRadioButton.setChecked(true);
                        maleRadioButton.setChecked(false);
                        sex = "1";
                    }else{
                        femaleRadioButton.setChecked(false);
                        maleRadioButton.setChecked(true);
                        sex = "0";
                    }
                    String school_department = jsonObject.getString("school_department");
                    if(!school_department.equals("")){
                        schoolTV.setText(school_department);
                        schoolStr = school_department;
                    }else{
                        schoolStr = null;
                    }
                    String role1 = jsonObject.getString("role");
                    if(role1.equals("student")){
                        studentRadioButton.setChecked(true);
                        teacherRadioButton.setChecked(false);
                        elseRadioButton.setChecked(false);
                        role = "student";
                    }else if(role1.equals("else")){
                        studentRadioButton.setChecked(false);
                        teacherRadioButton.setChecked(false);
                        elseRadioButton.setChecked(true);
                        role = "else";
                    }else {
                        studentRadioButton.setChecked(false);
                        teacherRadioButton.setChecked(true);
                        elseRadioButton.setChecked(false);
                        role = "teacher";
                    }
                    String birthYear = jsonObject.getString("birthYear");
                    if(!birthYear.equals("")){
                        birthYearTV.setText(birthYear);
                        selectedYear = birthYear;
                    }
                    String icon = jsonObject.getString("icon");
                    Log.i("userInfo", icon);
                    if(!icon.equals("")){
//                        downloadIcon(icon);
                        Log.i("userInfo", MeFragment.iconFile.getAbsolutePath());
                        Bitmap bitmap = BitmapFactory.decodeFile(MeFragment.iconFile.getAbsolutePath());
                        userIconIV.setImageBitmap(bitmap);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    OnCheckedChangeListener mChangeListener= new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.getId()==R.id.rbtn_male && isChecked){
                sex = "0";
                femaleRadioButton.setChecked(false);
            }else if(buttonView.getId()==R.id.rbtn_female && isChecked){
                sex = "1";
                maleRadioButton.setChecked(false);
            }
        }
    };

    OnCheckedChangeListener mChangeListener_1 = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.getId()==R.id.rbtn_teacher && isChecked){
                role = "teacher";
                studentRadioButton.setChecked(false);
                elseRadioButton.setChecked(false);
            }else if(buttonView.getId()==R.id.rbtn_student && isChecked){
                role = "student";
                teacherRadioButton.setChecked(false);
                elseRadioButton.setChecked(false);
            }else if(buttonView.getId()==R.id.rbtn_else && isChecked){
                role = "else";
                teacherRadioButton.setChecked(false);
                studentRadioButton.setChecked(false);
            }
        }
    };

}
