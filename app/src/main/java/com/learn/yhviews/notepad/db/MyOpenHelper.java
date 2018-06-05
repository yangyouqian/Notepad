package com.learn.yhviews.notepad.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learn.yhviews.notepad.entity.DayCostHelper;
import com.learn.yhviews.notepad.entity.Notify;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhviews on 2018/4/4.
 */

public class MyOpenHelper extends SQLiteOpenHelper {
    //INSERT INTO account(type, money, date, pay, note) VALUES ( 'test', '100','2018-03-11','1','note');
    //添加一个字段是月份
    /*CREATE TABLE `user`.`account` (
          `id` INT NOT NULL AUTO_INCREMENT,
          `type` VARCHAR(20) NULL,
          `money` DECIMAL(20) NULL,
          `date` DATETIME NULL,
          `pay` INT NULL,
          `month` VARCHAR(15) NULL,
          `day` VARCHAR(15) NULL,
          PRIMARY KEY (`id`));*/
    public static final String CREATE_NOTEPAD = "create table if not exists account(" +
            "id integer primary key autoincrement," +
            "type text, money real, date text, pay integer, note text)";
            //消费类型，金额，日期，是否支出，备注

    private static final String CREATE_NOTIFICATION = "create table if not exists notifi(" +
            "id integer primary key autoincrement, time text, open text, repeat text)";//重复 day week one
    private Context context;

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public boolean checkNitifyMaxNum(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from notifi", null);
        int count = 0;
        if (cursor != null){
            while (cursor.moveToNext()){
                count++;
            }
            cursor.close();
        }
        return count < 5;
    }

    public List<DayCostHelper> getEveryDayMoney(String month){
        List<DayCostHelper> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from account", null);
        String date = "";
        DayCostHelper dch = new DayCostHelper();
        int index = 0;
        float sum = 0;
        int listIndex = 0;
        while (cursor.moveToNext()){
            String lastDate = date;
            date = cursor.getString(cursor.getColumnIndex("date"));
            if (date.contains(month)){
                if (!lastDate.equals(date)) {
                    String d = date.split("-")[2];//日期的格式是yyyy-MM-dd,所以以 "-" 分割的第三个数据就是天数
                    dch.setDay(d);
                    dch.setPosition(index);
                    list.add(dch);
                    if (listIndex > 0)
                        list.get(listIndex - 1).setSumMoney(sum);
                    dch = new DayCostHelper();
                    sum = 0;//计算下一天的消费总额
                    listIndex++;
                }
                float money = cursor.getFloat(cursor.getColumnIndex("money"));
                String isPay = cursor.getString(cursor.getColumnIndex("pay"));
                if (isPay.equals("1")){
                    sum = sum - money;
                } else {
                    sum = sum + money;
                }
                index++;
            }
        }
        if (listIndex > 0)
            list.get(listIndex - 1).setSumMoney(sum);
        cursor.close();

        for (DayCostHelper d:list) {
            Log.d("list data", d.getPosition() + d.getDay());
        }

        Log.d("list size", list.size() + "size");
        return list;
    }

    public void addNotifyToDB(Notify n){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", n.getTime());
        values.put("open", n.isOpen() ? "1" : "0");
        values.put("repeat", n.getRepeatCycle());
        db.insert("notifi", null, values);
    }

    public void add(String type, String money, String date, String pay, String note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("money", money);
        values.put("date", date);
        values.put("pay", pay);
        values.put("note", note);
        db.insert("account", null, values);
    }

    //TODO 应改为查询本月记录
    //查询所有记录
    public Cursor select(){
        SQLiteDatabase db = getWritableDatabase();
        return db.query("account", null, null, null, null, null, null);
    }

    //查询所有提醒事项
    public Cursor selectNotifyFormDB(){
        SQLiteDatabase db = getWritableDatabase();
        return db.query("notifi", null, null, null, null, null, null);
    }

    //获取总支出/收入金额: true为支出，false为收入
    public float getPayMoney(boolean isPay, String month){
        SQLiteDatabase db = getWritableDatabase();
        String flag = isPay ? "1" : "0";
        String selectSql = "select * from account where pay=?";
        Cursor cursor = db.rawQuery(selectSql, new String[]{flag});
        float sum = 0;
        if (cursor.getCount() != 0){
            while (cursor.moveToNext()){
                String date = cursor.getString(cursor.getColumnIndex("date"));
                if (date.contains(month)){
                    float money = cursor.getFloat(cursor.getColumnIndex("money"));
                    sum += money;
                }
            }
        }
        return sum;
    }

    //返回所有月份,含年
    public String[] getMonth(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select date from account", null);
        String date = "";
        List<String> list = new ArrayList<>();
        if (cursor != null){
            while (cursor.moveToNext()){
                String lastDate = date;
                date = cursor.getString(cursor.getColumnIndex("date"));
                if (!lastDate.equals(date)){
                    String d = date.substring(0, 7);
                    list.add(d);
                }
            }
            cursor.close();
        }
        String[] months = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            months[i] = list.get(i);
        }
        return months;
    }
    //TODO 每月的消费应该在下一个月的时候存入数据库中
    //返回每月的消费
    public float[] getMonthMoney(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from account", null);
        List<Float> list = new ArrayList<>();
        float sum = 0;
        String date = "";
        int listIndex = 0;
        if (cursor != null){
            while (cursor.moveToNext()){
                String lastDate = date;
                date = cursor.getString(cursor.getColumnIndex("date"));
                if (!lastDate.equals(date)){
                    list.add(sum);
                    sum = 0;
                }
                float money = cursor.getFloat(cursor.getColumnIndex("money"));
                String isPay = cursor.getString(cursor.getColumnIndex("pay"));
                if (isPay.equals("1")){
                    sum = sum + money;
                }
            }
            cursor.close();
        }
        list.add(sum);
        float[] moneys = new float[list.size() - 1];
        for (int i = 1; i < list.size(); i++) {
            moneys[i - 1] = list.get(i);
        }
        return moneys;
    }

    //删除提醒
    public void delete(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("notifi", "id = ?", new String[]{id+""});
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTEPAD);
        db.execSQL(CREATE_NOTIFICATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateNotify(Notify notify) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", notify.getTime());
        values.put("repeat", notify.getRepeatCycle());
        db.update("notifi", values, "id = ?", new String[]{notify.getId() + ""});
    }

    public void updateNotifyOpenState(boolean isOpen, int id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("open", isOpen);
        db.update("notifi", values, "id = ?", new String[]{id + ""});
    }
}
