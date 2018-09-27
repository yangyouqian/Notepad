package com.learn.yhviews.notepad.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.learn.yhviews.notepad.R;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "okhttp";
    //本机IP
    private static final String BASE_URL = "http://192.168.1.20:8080/up/";

    private TextView budgeSetting;
    private TextView notificationSetting;
    private TextView uploadDb;
    private ProgressBar mProgressBar;
    private Button backBtn;
    private boolean change = false;
    private boolean error = false;
    public static final int SUCCESS = 0;
    public static final int FAILED = 1;
    private OkHttpClient okHttpClient = new OkHttpClient();


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS:
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UserCenterActivity.this, "成功!", Toast.LENGTH_SHORT).show();
                    break;
                case FAILED:
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UserCenterActivity.this, "失败!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        init();
    }

    private void init() {
        budgeSetting = findViewById(R.id.budge_setting);
        notificationSetting = findViewById(R.id.notification_setting);
        backBtn = findViewById(R.id.btn_settingBack);
        uploadDb = findViewById(R.id.upload_db);
        mProgressBar = findViewById(R.id.progress);

        budgeSetting.setOnClickListener(this);
        notificationSetting.setOnClickListener(this);
        uploadDb.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.budge_setting:
                startActivityForResult(new Intent(UserCenterActivity.this, BudgetActivity.class), 1);
                break;
            case R.id.notification_setting:
                startActivity(new Intent(UserCenterActivity.this, NotificationActivity.class));
                break;
            case R.id.btn_settingBack:
                finish();
                break;
            case R.id.upload_db:
                //上传文件
                mProgressBar.setVisibility(View.VISIBLE);
                doPostFile();
                break;
        }
    }


    public void doPostFile(){
        File dbFile = new File(Environment.getDataDirectory().getAbsolutePath()+"/data/"+getPackageName()+"/databases/NoteMoney.db");
        Log.i(TAG, "doPostFile: 2" + dbFile.getPath());
        if (!dbFile.exists()){
            Log.i(TAG, "doPostFile: 不存在数据库文件!");
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), dbFile);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(BASE_URL+"postFile").post(requestBody).build();
        execute(request);
    }

    public void doPostString(){
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;chaset=utf-8"), "{username:yhviews, password:123}");
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(BASE_URL+"postString").post(requestBody).build();
        execute(request);
    }

    public void doPost(){
        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        //传递post的参数
        requestBodyBuilder.add("username","yhviews").add("password", "1234");

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(BASE_URL+"login").post(requestBodyBuilder.build()).build();
        execute(request);
    }
    public void doGet(){
        Request.Builder builder = new Request.Builder();
        //可以直接在URL后面拼接
        Request request = builder.get().url(BASE_URL + "/login?username=yh&password=123").build();
        execute(request);
    }

    private void execute(Request request) {
        Call call = okHttpClient.newCall(request);
        final Message message = new Message();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
                e.printStackTrace();
                message.what = FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
                message.what = SUCCESS;
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    if (data.getBooleanExtra("change", false)){
                        //修改了预算
                        change = true;
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (change)
            intent.putExtra("change", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}
