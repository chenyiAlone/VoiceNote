package com.chenyialone.voicewritet;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.chenyialone.voicewritet.util.Speaker;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;

import static com.chenyialone.voicewritet.util.Utils.delHTMLTag;
import static com.chenyialone.voicewritet.util.Utils.subTittle;

public class EditorAcitvity extends AppCompatActivity {

    private RichEditor mEditor;

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;
    private static Speaker mspeaker;

    private File picture;

    private boolean isModify;
    private Integer id;
    private SQLiteDatabase db;
    private TextView mtittle;
    private  int frontColor = Color.BLACK;

    private  RelativeLayout insertInternetImageLayout;
    private Dialog mDialog;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_editor);


        mspeaker = new Speaker(this);
        mEditor = (RichEditor) findViewById(R.id.editor);
        mEditor.setEditorHeight(900);
        mEditor.setEditorFontSize(20);
//        mEditor.
//        mEditor.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
//        mEditor.setPadding(10, 10, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...");
        //mEditor.setInputEnabled(false);
        final String html =  "/storage/emulated/0/Pictures/Screenshots/Screenshot_20180628-000451.jpg";
        mEditor.setHtml("");
        db = MainActivity.mdbHelper.getWritableDatabase();

        StrictMode.VmPolicy.Builder builders = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builders.build());
        builders.detectFileUriExposure();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("插入图片");
        builder.setView(initView());
        mDialog= builder.create();
//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDialog.show();
//            }
//        });
        Intent intent = getIntent();
        String tittle = intent.getStringExtra("tittle");
        mtittle = (TextView)findViewById(R.id.toolbar_title_tv);

        //获取主键
        id = intent.getIntExtra("id",0);
        String text = intent.getStringExtra("text");
        //是否为修改的判断标识
        isModify = intent.getBooleanExtra("isModify",false);

        if(!"".equals(tittle)&&tittle!=null){
            mtittle.setText(tittle);
        } else {
            mtittle.setText("无标题");
        }
        mEditor.setHtml(text);


        final Button b = findViewById(R.id.toolbar_left_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(b);
            }
        });
        findViewById(R.id.toolbar_right_btn).setOnClickListener(new View.OnClickListener() {
            private boolean flag;
            @Override
            public void onClick(View v) {
                if(!flag){
                    String str = delHTMLTag(mEditor.getHtml());
                    mspeaker.speak(str);
                }else {
                    mspeaker.stopSpeaking();
                }
                flag=!flag;
            }
        });



        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_bold).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_italic).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_subscript).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_superscript).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_strikethrough).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_underline).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_heading1).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_heading2).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_heading3).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_heading4).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_heading5).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_heading6).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setHeading(6);
            }
        });

        final ImageButton imageButton = findViewById(R.id.action_txt_color);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                if(frontColor==Color.BLACK){
                    frontColor=Color.RED;
                }else if(frontColor==Color.RED){
                    frontColor=Color.YELLOW;
                }else if(frontColor==Color.YELLOW){
                    frontColor=Color.GREEN;
                }else if(frontColor==Color.GREEN){
                    frontColor=Color.BLUE;
                }else if(frontColor==Color.BLUE){
                    frontColor=Color.BLACK;
                }
                mEditor.setTextColor(frontColor);
                imageButton.setBackgroundColor(frontColor);

            }
        });


        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? (Color.TRANSPARENT): Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_indent).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_outdent).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                    mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_align_left).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_align_center).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_align_right).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                findViewById(R.id.action_insert_numbers).setBackgroundColor(isChanged?
                        Color.BLACK:Color.GRAY);
                isChanged = !isChanged;
                mEditor.setNumbers();
            }
        });
    //  插入图片
        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mDialog.show();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            public void alert_edit(View view){
                final EditText et = new EditText(EditorAcitvity.this);
                new AlertDialog.Builder(EditorAcitvity.this).setTitle("插入链接")
                        .setView(et)
                        .setPositiveButton("插入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
                                String url = et.getText().toString();
                                mEditor.insertLink(url,url);
                            }
                        }).setNegativeButton("取消",null).show();
            }
            @Override public void onClick(View v) {
                alert_edit(findViewById(R.id.action_insert_link));

            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }


    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()){
                    case R.id.action_delete:
                        mEditor.setHtml("");
                        break;
                    case R.id.action_copy:
                        String str = delHTMLTag(mEditor.getHtml());
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(str);
                        Toast.makeText(EditorAcitvity.this, "复制成功", Toast.LENGTH_SHORT).show();

                }
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
            // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
                public void onDismiss(PopupMenu menu) {
//                Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }





    private View initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.layout_image_select,null );
        insertInternetImageLayout = (RelativeLayout)view.findViewById(R.id.are_image_select_from_internet_layout);

        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.are_image_select_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(checkedId){
                    case R.id.are_image_take_photo:
                        takePhoto();
                        radioGroup.clearCheck();
                        mDialog.dismiss();
                        break;
                    case R.id.are_image_select_from_local:
                        chooesPhoto();
                        radioGroup.clearCheck();
                        mDialog.dismiss();
                        break;
                    case R.id.are_image_select_from_internet:
                        insertInternetImageLayout.setVisibility(View.VISIBLE);

//                        mDialog.dismiss();
                        break;
                    default:
                        mDialog.dismiss();
                        if(radioGroup.isClickable()){
                            radioGroup.clearCheck();
                        }
                        break;
                }
            }
        });

        final TextView insertInternetImage = (TextView)view.findViewById(R.id.are_image_select_insert);
        insertInternetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertInternetImage(view);
            }
        });
        return view;
    }
    private void insertInternetImage(View v) {
        EditText editText = (EditText)v.findViewById(R.id.are_image_select_internet_image_url);
        String imageUrl = editText.getText().toString();
        Toast.makeText(this,imageUrl,Toast.LENGTH_LONG).show();

        if (!(imageUrl.startsWith("http")
                &&  (imageUrl.endsWith("png") || imageUrl.endsWith("jpg") || imageUrl.endsWith("jpeg")))) {
            Toast.makeText(this,"Not a valid image",Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
            insertInternetImageLayout.setVisibility(View.GONE);
            return;
        }

        mEditor.insertImage(imageUrl,DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)).toString());
        mDialog.dismiss();
        insertInternetImageLayout.setVisibility(View.GONE);

    }

    public void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用系统相机
        String fileName = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        picture = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()+"/voicenote",fileName);
        Uri imageUri = Uri.fromFile(picture);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);          //直接使用，没有缩小
        startActivityForResult(intent, 100);// 100 是请求码
    }

    public void chooesPhoto(){
        if (ContextCompat.checkSelfPermission(EditorAcitvity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditorAcitvity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            openAlbum();
        }
    }
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    protected void onActivityResult(int requestCode, int result,Intent data) {
        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)).toString();
//        Toast.makeText(Main2Activity.this,"req"+requestCode+"result"+result)
        if(result==-1&& requestCode==100){    //result 返回的是-1 表示拍照成功 返回的是 0 表示拍照失败
            Uri uri = Uri.fromFile(picture);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            this.sendBroadcast(intent);  // 这里我们发送广播让MediaScanner 扫描我们制定的文件
            mEditor.insertImage(uri.getPath(),name);
            // 这样在系统的相册中我们就可以找到我们拍摄的照片了
        }else if(result==-1 &&requestCode==CHOOSE_PHOTO){
//            Uri uri =data.getData();
            String path = handleImageOnKitKat(data);
            mEditor.insertImage(path,name);
//            Toast.makeText(EditorAcitvity.this,path,Toast.LENGTH_LONG).show();
        }
    }
    @TargetApi(19)
    private String  handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        Log.d("inagePath",imagePath);
        return imagePath;
//        displayImage(imagePath); // 根据图片路径显示图片
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onBackPressed() {
        String str = mEditor.getHtml();
        Log.d("-----------------------",str);
        if(!"".equals(str)
                &&!(str==null)){
            ContentValues contentValues = new ContentValues();

            contentValues.put("tittle",subTittle(str));
            contentValues.put("text",str);
            if(isModify){

                db.update("Txt",contentValues,"id = ?",new String[]{id.toString()});
                contentValues.clear();
            }else {
                db.insert("Txt",null,contentValues);
                contentValues.clear();
            }
        }else {
            db.delete("Txt","id = ?",new String[]{id.toString()});
        }

        Intent intent = new Intent(this,MainActivity.class);
        setResult(RESULT_OK,intent);
        startActivityForResult(intent,RESULT_OK);
        finish();
    }


}
