package com.learn.yhviews.notepad.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.learn.yhviews.notepad.R;

public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView budgeSetting;
    private TextView notificationSetting;
    private Button backBtn;
    private boolean change = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        budgeSetting = findViewById(R.id.budge_setting);
        notificationSetting = findViewById(R.id.notification_setting);
        backBtn = findViewById(R.id.btn_settingBack);
        budgeSetting.setOnClickListener(this);
        notificationSetting.setOnClickListener(this);
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
        }
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
