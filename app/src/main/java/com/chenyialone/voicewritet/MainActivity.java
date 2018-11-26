package com.chenyialone.voicewritet;

/**
 * 主活动界面
 * Created by chenyiAlone on 2018/5/20.
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chenyialone.voicewritet.util.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.chenyialone.voicewritet.util.Utils.delHTMLTag;

public class MainActivity
        extends AppCompatActivity
        implements AdapterView.OnItemClickListener {
    //退出判断标识
    private boolean isExit = false;

    public static MyDatabaseHelper mdbHelper;

    private ListView mListView;
    private SimpleAdapter mAdapter;
    private SQLiteDatabase db;

    // 储存主键id与ListView顺序对应
    private ArrayList<Integer> keylist;
    private ArrayList<String> htmlList;
    private ArrayList<Map<String,String>> list ;

    // 退出的handler对象
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 标记用户不退出状态
            isExit=false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        init();
    }

    /**
     * 成员对象初始化
     */
    public void init(){
        // 动态获取录音权限
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA}, 1);
        }

        list = new ArrayList<>();
        keylist = new ArrayList<>();
        htmlList = new ArrayList<>();


        mdbHelper = new MyDatabaseHelper(this,"TxtList.db",null,1);
        db = mdbHelper.getWritableDatabase();
        // 设置button_add的点击监听对象
        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.button_add){
                    Intent i = new Intent(MainActivity.this,EditorAcitvity.class);
                    startActivity(i);
                }
            }
        });

        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new SimpleAdapter(
                MainActivity.this,
                list,
                android.R.layout.simple_list_item_2,
                new String[]{"text1","text2"},
                new int[]{android.R.id.text1,android.R.id.text2});
        mListView.setOnItemClickListener(this);

        // 同部数据库,放在初始化以后
        syncDatabase();

        final SwipeRefreshLayout mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
        // 设置在listview上下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 下拉同步数据库到ListView
                syncDatabase();
                if(mSwipeLayout.isRefreshing())mSwipeLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 同步数据库到ListView
     */
    public void syncDatabase(){
        // 清空主键List和对应ListView的List
        list.clear();
        keylist.clear();
        htmlList.clear();
        Cursor c = db.query("Txt",null,null,null,null,null,null);
        if(c.moveToFirst()){
            do{
                String tille = c.getString(c.getColumnIndex("tittle"));
                String text = c.getString(c.getColumnIndex("text"));
                HashMap<String,String> map = new HashMap<>();
                Integer key = c.getInt(c.getColumnIndex("id"));
                keylist.add(key);
                htmlList.add(text);
                map.put("text1",tille);
                map.put("text2",delHTMLTag(text));
                list.add(map);
            }while(c.moveToNext());
        }
        c.close();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                System.exit(0);
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RESULT_OK){
            syncDatabase();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 通过view获取其内部的组件，进而进行操作
        String tittle = (String) ((TextView)view.findViewById(android.R.id.text1)).getText();

        Intent i = new Intent(this,EditorAcitvity.class);
        i.putExtra("tittle", tittle);
        i.putExtra("text", htmlList.get((int)id));
        i.putExtra("isModify",true);
        i.putExtra("id",keylist.get(position));

        startActivity(i);
    }
}
