package com.learn.yhviews.notepad.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.learn.yhviews.notepad.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yhviews on 2018/4/8.
 */

public class CircleView extends View {
    private String TAG="Circle";
    private int mMeasureHeight;
    private int mMeasureWidth;

    private float mCircleXY;
    //外圈的弧线
    private Paint mArcPaint;
    private RectF mArvRectF;
    private float mSweepAngle;
    private float mSweepValue = 100;
    private float mStartAngle = -90;
    //外圈的灰色弧线
    private Paint mArcPaintGray;
    private float mSweepAngleGray;
    private float mSweepValueGray = 0;
    //圆中的文字
    private Paint mTextPaint;
    private Paint mTextDatePaint;
    private Paint mTextMoneyPaint;
    private String reMoney;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);//getWidth?
        mMeasureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mMeasureWidth, mMeasureHeight);
        Log.d(TAG, "mMeasureWidth" + mMeasureWidth);
        Log.d(TAG, "mMeasureHeight" + mMeasureHeight);

        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "getWidth" + getWidth());
        Log.d(TAG, "getHeight" + getHeight());
        //这些坐标应该是一个相对坐标，相对与该View在布局上的左上角顶点坐标

        //绘制弧线,startAngle：从哪个角度顺时针绘画，时针3点钟方向为0
        // sweepAngle: 扫过的角度，顺时针画圆弧
        //useCenter：圆弧是否经过圆心（变成扇形）
        canvas.drawArc(mArvRectF, -90, mSweepAngleGray, false, mArcPaintGray);
        canvas.drawArc(mArvRectF, mStartAngle, mSweepAngle, false, mArcPaint);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        String date = sdf.format(new Date());
        canvas.drawText(date, 0, date.length(), mCircleXY, mCircleXY - 100, mTextDatePaint);
        String re = "预算剩余";
        canvas.drawText(re, 0, re.length(), mCircleXY, mCircleXY - (50 / 4), mTextPaint);
        canvas.drawText(reMoney, 0, reMoney.length(), mCircleXY, mCircleXY + 70, mTextMoneyPaint);
    }

    private void initView(){
        float length = 0;
        if (mMeasureHeight >= mMeasureWidth){
            length = mMeasureWidth;
        } else {
            length = mMeasureHeight;
        }

        mCircleXY = length / 2;
        //绘制弧线需要指定其外部矩形的 left top（左上角坐标） right bottom（右下角坐标）
        mArvRectF = new RectF(
                (float) (length * 0.1),
                (float)(length * 0.1),
                (float)(length * 0.9),
                (float)(length * 0.9));
        mSweepAngle = (mSweepValue / 100f) * 360f;
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(getResources().getColor(R.color.circleColor));
        mArcPaint.setStrokeWidth((float) (length * 0.05));
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mSweepAngleGray = (mSweepValueGray / 100f) * 360f;
        mArcPaintGray = new Paint();
        mArcPaintGray.setAntiAlias(true);
        mArcPaintGray.setColor(getResources().getColor(R.color.gray));
        mArcPaintGray.setStrokeWidth((float) (length * 0.05));
        mArcPaintGray.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(50);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextDatePaint = new Paint();
        mTextDatePaint.setTextSize(35);
        mTextDatePaint.setTextAlign(Paint.Align.CENTER);

        mTextMoneyPaint = new Paint();
        mTextMoneyPaint.setTextSize(60);
        mTextMoneyPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setReMoney(String reMoney){
        this.invalidate();
        this.reMoney = reMoney;
    }


    public void forceInvalidate() {
        this.invalidate();
    }

    public void setSweepValue(float sweepValue) {
        if (sweepValue > 0) {
            mSweepValue = sweepValue;
            mSweepValueGray = 100 - sweepValue;
        } else {
            mSweepValue = 100;
            mSweepValueGray = 0;
        }
        this.invalidate();
    }

    public void setStartAngle(float startAngle){
        mStartAngle = startAngle;
        this.invalidate();
    }

    public void drawReMoney(float allMoney, float reMoney){
        float percent = reMoney / allMoney;
        setSweepValue(percent * 100);
        float startAngle = -(float) ((percent - 0.75) * 360);
        setStartAngle(startAngle);
    }
}
