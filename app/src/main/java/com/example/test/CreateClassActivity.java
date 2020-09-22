package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.util.FileUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateClassActivity extends AppCompatActivity {

    private ImageView classIconIV;
    private LinearLayout termLayout;
    private TextView termTV;
    private EditText classNameET;
    private EditText schoolET;
    private EditText gradeClassET;
    private EditText classIntroductionET;
    private Button createClassBtn;
    private Button backBtn;
    private final int IMAGE_SELECT = 1;
    private final int IMAGE_CUT = 2;
    private File cropFile = null;
    private String selectedTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        termTV = findViewById(R.id.term_Tv);
        classNameET = findViewById(R.id.class_name_Et);
        schoolET = findViewById(R.id.school_Et);
        gradeClassET = findViewById(R.id.grade_class_Et);
        classIntroductionET = findViewById(R.id.class_introduction_Et);
        createClassBtn = findViewById(R.id.create_class_btn);
        backBtn = findViewById(R.id.toolbar_left_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        classIconIV = findViewById(R.id.class_icon_Iv);
        classIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, IMAGE_SELECT);
            }
        });

        final String[] term = new String[]{"2016-2017-1", "2016-2017-2", "2017-2018-1", "2017-2018-2",
                "2018-2019-1", "2018-2019-2", "2019-2020-1", "2019-2020-2", "2020-2021-1", "2020-2021-2",
                "2021-2022-1", "2021-2022-2", "2022-2023-1", "2022-2023-2", "2023-2024-1", "2023-2024-2",
                "2024-2025-1", "2024-2025-2", "2025-2026-1", "2025-2026-2", "2026-2027-1", "2026-2027-2", };
        termLayout = findViewById(R.id.term_layout);
        termLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateClassActivity.this)
                        .setTitle("选择班课学期")
                        .setSingleChoiceItems(term, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedTerm = term[which];
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        termTV.setText(selectedTerm);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        createClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classNameET.getText().toString().equals("")){
                    showAlertDialog("请输入班课名！");
                }else if(schoolET.getText().toString().equals("")){
                    showAlertDialog("请输入学校院系！");
                }else if(gradeClassET.getText().toString().equals("")){
                    showAlertDialog("请输入班级！");
                }else if(termTV.getText().toString().equals("班课学期未选择")){
                    showAlertDialog("请选择班课学期！");
                }else if(classIntroductionET.getText().toString().equals("")){
                    showAlertDialog("请输入班课简介！");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody requestBody;
                        if(cropFile != null){
                            requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("class_icon", cropFile.getName(),
                                            MultipartBody.create(MediaType.parse("img/jpg"), cropFile))
                                    .addFormDataPart("className", classNameET.getText().toString())
                                    .addFormDataPart("school", schoolET.getText().toString())
                                    .addFormDataPart("gradeClass", gradeClassET.getText().toString())
                                    .addFormDataPart("term", termTV.getText().toString())
                                    .addFormDataPart("classIntruction", classIntroductionET.getText().toString())
                                    .addFormDataPart("phoneNumber", MainActivity.phoneNumber)
                                    .addFormDataPart("name", MainActivity.name)
                                    .build();
                        }else{
                            requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("className", classNameET.getText().toString())
                                    .addFormDataPart("school", schoolET.getText().toString())
                                    .addFormDataPart("gradeClass", gradeClassET.getText().toString())
                                    .addFormDataPart("term", termTV.getText().toString())
                                    .addFormDataPart("classIntruction", classIntroductionET.getText().toString())
                                    .addFormDataPart("phoneNumber", MainActivity.phoneNumber)
                                    .addFormDataPart("name", MainActivity.name)
                                    .build();
                        }
                        Request request = new Request.Builder()
                                .url("http://47.98.236.0:8080/createclass")
                                .post(requestBody)
                                .build();
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                final String responseBodyStr = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final String path;
                                        if(cropFile == null){
                                            path = "";
                                        }else{
                                            path = cropFile.getAbsolutePath();
                                        }
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateClassActivity.this)
                                                .setTitle("创建成功")
                                                .setMessage("班课创建成功！七位班课号为：" + responseBodyStr + ",请邀请同学加入班课吧！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        File classFile = new File(Environment.getExternalStorageDirectory()
                                                                        + "/daoyun/" + MainActivity.phoneNumber + ".json");
                                                        try {
                                                            FileInputStream in = new FileInputStream(classFile);
                                                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                                                            byte[] bt = new byte[4096];
                                                            String classJsonStr = reader.readLine();
//                                                            Log.i("CreateClassInfo", classJsonStr+" "+"hello");
                                                            JSONArray classJsonArray = new JSONArray(classJsonStr);
                                                            JSONObject jsonObject = new JSONObject();
                                                            jsonObject.put("classId", responseBodyStr);
                                                            jsonObject.put("className",classNameET.getText().toString());
                                                            jsonObject.put("classIcon", cropFile.getName());
                                                            jsonObject.put("gradeClass", gradeClassET.getText().toString());
                                                            jsonObject.put("school", schoolET.getText().toString());
                                                            jsonObject.put("term", termTV.getText().toString());
                                                            jsonObject.put("classIntruction", classIntroductionET.getText().toString());
//                                                            Log.i("CreateClassInfo", jsonObject.toString());
                                                            classJsonArray.put(jsonObject);
                                                            if(classFile.exists()){
                                                                classFile.delete();
                                                            }
//                                                            Log.i("CreateClassInfo", classJsonArray.get(classJsonArray.length()-1).toString());
                                                            FileOutputStream out = new FileOutputStream(classFile);
                                                            out.write(classJsonArray.toString().getBytes("utf-8"));
                                                            out.close();
                                                        } catch (FileNotFoundException e) {
                                                            e.printStackTrace();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Intent intent = new Intent();
                                                        intent.putExtra("classId", responseBodyStr);
                                                        intent.putExtra("classIcon", path);
                                                        intent.putExtra("className", classNameET.getText().toString());
                                                        intent.putExtra("gradeClass", gradeClassET.getText().toString());
                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                    }
                                                });
                                        builder.show();
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode)
        {
            case RESULT_OK:
                if(requestCode == IMAGE_CUT){
                    Log.i("UserInfoInfo", Environment.getExternalStorageDirectory().toString());
//                    userIconIV.setImageURI(cropImageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(cropFile.getAbsolutePath());
                    classIconIV.setImageBitmap(bitmap);
                    Log.i("UserInfoInfo", "cropFile.toString()");
                }else if(requestCode == IMAGE_SELECT){
                    Uri iconUri = data.getData();
                    startCropImage(iconUri);
                }
                break;
            default:
                break;
        }
    }

    private void startCropImage(Uri uri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
            cropFile = new File(Environment.getExternalStorageDirectory() + "/daoyun/" + timeStamp + ".jpg");
//            cropFile = new File(path + File.separator + MainActivity.userName + ".jpg");
            if (cropFile.exists()) {
                cropFile.delete();
            }
            Uri cropImageUri = Uri.fromFile(cropFile);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlertDialog(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateClassActivity.this)
                        .setMessage(msg)
                        .setPositiveButton("确定", null);
                builder.show();
            }
        });
    }

}
