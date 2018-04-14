package com.learn.yhviews.notepad.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.learn.yhviews.notepad.R;

public class BudgetActivity extends AppCompatActivity {

    private EditText etSetAllMoney;
    private Button btnConfirmMoney;
    private boolean change = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        etSetAllMoney = findViewById(R.id.et_setAllMoney);
        btnConfirmMoney = findViewById(R.id.btn_confirmMoney);
        btnConfirmMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 到了下月清空本月设置的预算
                //设置本月预算，存储在sp中
                SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                float money = Float.parseFloat(etSetAllMoney.getText().toString());
                editor.putFloat("allMoney", money);
                editor.apply();
                change = true;
                Toast.makeText(BudgetActivity.this, "这个月我不做月光族！",Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        float lastMoney = sp.getFloat("allMoney", 0);
        etSetAllMoney.setText(lastMoney + "");
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
