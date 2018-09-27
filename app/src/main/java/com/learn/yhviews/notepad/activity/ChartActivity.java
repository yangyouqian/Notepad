package com.learn.yhviews.notepad.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.learn.yhviews.notepad.R;
import com.learn.yhviews.notepad.adapter.ShowPreAdapter;
import com.learn.yhviews.notepad.db.MyOpenHelper;
import com.learn.yhviews.notepad.entity.Cost;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartActivity extends AppCompatActivity {
    private MyOpenHelper dbHelper;
    private LineChartView chart;
    boolean hasAxes = true;
    private List<Line> lines;
    private TextView tvChartMonth;
    String[] months;
    float[] moneys;

    private ListView showPreLv;
    private List<Cost> costList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chart = findViewById(R.id.lineChart);
        showPreLv = findViewById(R.id.show_pre_lv);
        tvChartMonth = findViewById(R.id.tv_chartMonth);
        dbHelper = new MyOpenHelper(this, "NoteMoney.db", null, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String date = sdf.format(new Date());
        tvChartMonth.setText(date);
        costList = new ArrayList<>();
        setListView(date);
        setChart();
        ValueTouchListener listener = new ValueTouchListener();
        chart.setOnValueTouchListener(listener);
    }

    private void setListView(String month) {
        costList.clear();
        showPreLv.setAdapter(null);
        Cursor cursor = dbHelper.select();
        //获得当月的消费记录
        while (cursor.moveToNext()){
            String m = cursor.getString(cursor.getColumnIndex("date"));
            if (m.contains(month)){
                Cost cost = new Cost();
                cost.setType(cursor.getString(cursor.getColumnIndex("type")));
                cost.setMoney(cursor.getString(cursor.getColumnIndex("money")));
                cost.setPay(cursor.getString(cursor.getColumnIndex("pay")));
                costList.add(cost);
            }
        }
        cursor.close();
        ShowPreAdapter adapter = new ShowPreAdapter(costList, this);
        showPreLv.setAdapter(adapter);
    }

    private void setChart(){
        months = dbHelper.getMonth();
        moneys = dbHelper.getMonthMoney();
        int numberOfPoints = months.length;
        List<AxisValue> axisXValues = new ArrayList<AxisValue>();
        lines = new ArrayList<Line>();
        int numberOfLines = months.length;
        for (int i = 0; i < numberOfLines; ++i) {
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j,moneys[j]));
                axisXValues.add(new AxisValue(j).setLabel(months[j]));
            }
            Line line = new Line(values);
            line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(ValueShape.CIRCLE); //节点的形状
            line.setHasLabels(true); //是否显示标签
            line.setHasLabelsOnlyForSelected(false);  //标签是否只能选中
            line.setHasLines(true); //是否显示折线
            line.setHasPoints(true); //是否显示节点
            lines.add(line);
        }
        LineChartData data = new LineChartData(lines);

        data.setAxisXBottom(new Axis(axisXValues).setHasLines(true).setTextColor(Color.BLACK).setName("月份").setHasTiltedLabels(true).setMaxLabelChars(7));
        data.setAxisYLeft(new Axis().setHasLines(true).setTextColor(Color.BLACK).setName("消费金额").setMaxLabelChars(5));


        data.setBaseValue(Float.NEGATIVE_INFINITY);
        //设置行为属性，支持缩放、滑动以及平移
        chart.setInteractive(true);
        chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        chart.setValueSelectionEnabled(true);
        chart.setLineChartData(data);



    }
    /**
     * 触摸监听类
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            //切换文字
            tvChartMonth.setText(months[pointIndex]);
            Log.d("yhviews", months[pointIndex]);
            setListView(months[pointIndex]);
        }

        @Override
        public void onValueDeselected() {


        }

    }

}
