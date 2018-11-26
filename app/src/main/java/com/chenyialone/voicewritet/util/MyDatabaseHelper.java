package com.chenyialone.voicewritet.util;

/**
 * MyDatabaseHelper 继承 SQLiteOpenHelper类
 * Created by chenyiAlone on 2018/5/20.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper
        extends SQLiteOpenHelper {

    private static final String CREATE_TXT = "create table Txt ("
            +"id integer primary key autoincrement, "
            +"tittle text, "
            +"text text)";
    private Context mContext;


    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TXT);
//        Toast.makeText(mContext,"创建成功",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
