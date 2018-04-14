package com.learn.yhviews.notepad.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.learn.yhviews.notepad.R;
import com.learn.yhviews.notepad.entity.Type;

import java.util.List;

/**
 * Created by yhviews on 2018/4/10.
 */

public class MyGridAdapter extends BaseAdapter {
    private Context mContext;
    private int click = -1;
    private int[] clickedList;
    private List<Type> typeList;

    private int[] iconBig = {R.drawable.eat_big, R.drawable.shop_big,R.drawable.daily_big,R.drawable.traff_big,R.drawable.party_big,
            R.drawable.eat_big, R.drawable.room_big, R.drawable.bill_big,R.drawable.tour_big,
            R.drawable.medical_big,R.drawable.edu_big};

    public MyGridAdapter(Context mContext, List<Type> list) {
        this.mContext = mContext;
        this.typeList = list;

        clickedList = new int[list.size()];
        for (int i =0; i < clickedList.length; i++){
            clickedList[i]=0;      //初始化item点击状态的数组
        }
    }

    public void refreshData(List<Type> list, int[] iconBig){
        this.typeList = list;
        this.iconBig = iconBig;
        clickedList = new int[list.size()];
        for (int i =0; i < clickedList.length; i++){
            clickedList[i]=0;      //初始化item点击状态的数组
        }
        notifyDataSetChanged();
    }

    public void setSelection(int position) {
        click = position;
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public Object getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
        ImageView imageView = view.findViewById(R.id.iv_grid_item);
        TextView textView = view.findViewById(R.id.tv_grid_item);
        Type type = typeList.get(position);
        imageView.setImageResource(type.getImageId());
        textView.setText(type.getName());

        for (int i = 1; i < clickedList.length; i++) {
            //确保未点击的子项状态正确
            if (i != click)
                clickedList[i] = 0;
        }

        //检测键盘弹出，键盘弹出会导致getView方法执行
        Log.d("getView", "getView");
        //键盘弹出导致选中效果消失
        //滑动gridview导致选中效果消失

        //点击第一个子项，会调用两次getView方法
        if (click == 0 && position == click){
            textView.setTextColor(mContext.getResources().getColor(R.color.mainBlack));
            textView.setTextSize(22);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            imageView.setImageResource(iconBig[position]);
        } else {
            //根据点击的Item设置当前状态
            if(click == position){
                if (clickedList[position]==0){
                    //设置为点击效果
                    textView.setTextColor(mContext.getResources().getColor(R.color.mainBlack));
                    textView.setTextSize(22);
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    imageView.setImageResource(iconBig[position]);
                    clickedList[position]=1;
                } else {
                    //还原默认效果
                    textView.setTextColor(mContext.getResources().getColor(R.color.normalTvColor));
                    textView.setTextSize(20);
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    imageView.setImageResource(type.getImageId());
                    clickedList[position]=0;
                }
            }
        }
        return view;
    }

    //TODO 优化
    class ViewHolder{
        private ImageView imageView;
        private TextView textView;
    }
}
