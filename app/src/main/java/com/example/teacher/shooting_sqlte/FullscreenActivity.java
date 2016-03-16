package com.example.teacher.shooting_sqlte;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;




    GridView gv;

    ImageView picture_pop;

    TextView number_pop,name_pop,price_pop;



    ArrayList<Integer> numbers = new ArrayList<Integer>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Float> prices = new ArrayList<Float>();
    ArrayList<Bitmap> pictures= new ArrayList<Bitmap>();

    Dialog pop_insert;

    int rember_choose;
    boolean is_delete;

    class my_adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return numbers.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout root = (LinearLayout) FullscreenActivity.this.getLayoutInflater().inflate(R.layout.grid,null);

            ImageView picture;

            TextView number,name,price;

            picture = (ImageView) root.findViewById(R.id.picture);
            number = (TextView) root.findViewById(R.id.number);
            price = (TextView) root.findViewById(R.id.price);
            name = (TextView) root.findViewById(R.id.name);

            picture.setImageBitmap(pictures.get(position));
            number.setText(numbers.get(position) + "");
            name.setText(names.get(position));
            price.setText(prices.get(position)+"");

            return root;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        gv = (GridView) this.findViewById(R.id.gridView);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(FullscreenActivity.this,parent.toString(),Toast.LENGTH_LONG).show();

                for(int i=0; i<parent.getChildCount(); i++) {
                    LinearLayout child = (LinearLayout)parent.getChildAt(i);
                     //Color.argb(255,135,180,38); //#87b426
                    //child.setBackgroundColor(Color.argb(255,135,180,38));
                    child.setBackgroundColor(Color.argb(0xff,0x87,0xb4,0x26));
                }

                final Dialog check_delete = new Dialog(FullscreenActivity.this);

                LinearLayout root = (LinearLayout) FullscreenActivity.this.getLayoutInflater().inflate(R.layout.check_delete, null);



                final RadioButton delete_button = (RadioButton) root.findViewById(R.id.radio_delete);
                Button radio_button = (Button) root.findViewById(R.id.radio_button);

                radio_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if(delete_button.isChecked()){
                            is_delete = true;
                        }
                        check_delete.dismiss();


                    }
                });

                check_delete.setContentView(root);

                check_delete.setTitle("資料處理動作");
                check_delete.show();


                if(!is_delete){ //如果不是delete才要重新點回紅色背景(delete刪掉了再點會當)
                    view.setBackgroundColor(Color.RED);
                    rember_choose = position;
                }

            }
        });


        //TODO read sqlite data
        SQLiteDatabase fruits;
        fruits = SQLiteDatabase.openDatabase(
                "/data/data/com.example.teacher.shooting_sqlte/fruits.sqlite",
                null,
                SQLiteDatabase.OPEN_READONLY
        );

       Cursor cursor;
       cursor=fruits.query(
              "summer",
               new String[]{"number","name","price","picture"},
               null,
               null,
               null,
               null,
               null
       );


        int number ;
        String name ;
        float price ;
        byte[] pic_bytes;


        while(cursor.moveToNext()){

            number = cursor.getInt(0);
            name = cursor.getString(1);
            price = cursor.getFloat(2);
            pic_bytes = cursor.getBlob(3);
            Bitmap real_pic = BitmapFactory.decodeByteArray(pic_bytes,0,pic_bytes.length);
            numbers.add(number);
            names.add(name);
            prices.add(price);
            pictures.add(real_pic);

        }


        my_adapter controler = new my_adapter();
        gv.setAdapter(controler);


        fruits.close();
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        //gv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,test_data));

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                toggle();


            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //TODO 新增資料按鈕按下
        findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Toast.makeText(FullscreenActivity.this, "insert", Toast.LENGTH_SHORT).show();
                //彈出一個字自制的對話方塊(可以輸入資料)

                pop_insert = new Dialog(FullscreenActivity.this);
                //pop_insert.setContentView(R.layout.insert_update_pop);

                LinearLayout root = (LinearLayout) FullscreenActivity.this.getLayoutInflater().inflate(R.layout.insert_update_pop,null);



                picture_pop = (ImageView) root.findViewById(R.id.iu_picture);
                number_pop = (TextView) root.findViewById(R.id.iu_number);
                price_pop = (TextView) root.findViewById(R.id.iu_price);
                name_pop = (TextView) root.findViewById(R.id.iu_name);

                pop_insert.setContentView(root);

                pop_insert.setTitle("新增水果資料");
                pop_insert.show();




                //mHideHandler.removeCallbacks(mHideRunnable);
                //mHideHandler.postDelayed(mHideRunnable, 0);
            }
        });
        //TODO 修改資料按鈕按下
        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Toast.makeText(FullscreenActivity.this, "update", Toast.LENGTH_SHORT).show();
                //彈出一個字自制的對話方塊(可以輸入資料)

                pop_insert = new Dialog(FullscreenActivity.this);
                //pop_insert.setContentView(R.layout.insert_update_pop);

                LinearLayout root = (LinearLayout) FullscreenActivity.this.getLayoutInflater().inflate(R.layout.insert_update_pop,null);



                picture_pop = (ImageView) root.findViewById(R.id.iu_picture);
                picture_pop.setImageBitmap(pictures.get(rember_choose));
                number_pop = (TextView) root.findViewById(R.id.iu_number);
                number_pop.setText(numbers.get(rember_choose)+"");
                price_pop = (TextView) root.findViewById(R.id.iu_price);
                price_pop.setText(prices.get(rember_choose)+"");
                name_pop = (TextView) root.findViewById(R.id.iu_name);
                name_pop.setText(names.get(rember_choose));
                pop_insert.setContentView(root);

                pop_insert.setTitle("修改水果資料");

                ((Button)root.findViewById(R.id.insert_update_button)).setText("修改");

                pop_insert.show();



            }
        });
        //TODO 刪除資料按鈕按下
        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //TODO 按下pop刪除按鈕

                //抓取迸現視窗上的number(索引)


                // HOMEWORK
                LinearLayout cell= (LinearLayout) gv.getChildAt(rember_choose);
                TextView cell_number = (TextView) cell.findViewById(R.id.number);
                int number = Integer.parseInt(cell_number.getText().toString());
                Toast.makeText(FullscreenActivity.this,number+"",Toast.LENGTH_SHORT).show();
                //打開資料庫

                SQLiteDatabase db = SQLiteDatabase.openDatabase(
                        "/data/data/com.example.teacher.shooting_sqlte/fruits.sqlite",
                        null,
                        SQLiteDatabase.OPEN_READWRITE
                );

                if(db != null){


                    int result=db.delete("summer","number=?",new String[]{number+""});
                    if(result==1){
                        Toast.makeText(FullscreenActivity.this,"刪除成功!",Toast.LENGTH_SHORT).show();
                    }

                    //重新裝載資料　更新GridView畫面

                    Cursor cur_new = db.query(
                            "summer",
                            new String[]{"number","name","price","picture"},
                            null,
                            null,
                            null,
                            null,
                            null
                    );
                    numbers.clear();
                    names.clear();
                    prices.clear();
                    pictures.clear();
                    while(cur_new.moveToNext()){

                        int no = cur_new.getInt(0);
                        String na = cur_new.getString(1);
                        float pr = cur_new.getFloat(2);
                        byte[] pi = cur_new.getBlob(3);
                        Bitmap pi_real = BitmapFactory.decodeByteArray(pi,0,pi.length);
                        numbers.add(no);
                        names.add(na);
                        prices.add(pr);
                        pictures.add(pi_real);
                    }

                    my_adapter controler = new my_adapter();
                    gv.setAdapter(controler);


                    db.close();


                    mHideHandler.removeCallbacks(mHideRunnable);
                    mHideHandler.postDelayed(mHideRunnable, 0);


                }else{
                    //開起失敗通知

                }



            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);


    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);



    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);

        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);

    }

     //TODO 碰現視窗按鈕事件處理
    public void click(View v){

        switch(v.getId()){

            //TODO 碰現視窗按鈕事件處理 ---->按下拍照
            case R.id.take_picture_button:

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //this.startActivity(takePictureIntent);
                this.startActivityForResult(takePictureIntent,777);
                break;

            //TODO 碰現視窗按鈕事件處理 ---->按下新增或更新
            case R.id.insert_update_button:



                if(((Button)v).getText().equals("新增")){

                    //TODO 按下新增按鈕

                    //抓取迸現視窗上的各個值
                    //新增入資料庫
                    int number = Integer.parseInt(number_pop.getText().toString());
                    String name = name_pop.getText().toString();
                    float price = Float.parseFloat(price_pop.getText().toString());
                    ////由ImageView取出圖片  再轉成byte[] (因為資料庫blob是byte[])


                    //呼叫ImageView的setDrawingCacheEnabled並傳入true(將IMageView中的圖打開Cache-->表示可以佔存到記憶體)
                    picture_pop.setDrawingCacheEnabled(true);
                    //呼叫ImageView的buildDrawingCache(執行Cache--->把圖真的放到Cache記憶體了)
                    picture_pop.buildDrawingCache();
                    //呼叫ImageView的getDrawingCache(取出放到記憶體的Cache--->就是一張Bitmap)
                    Bitmap bm = picture_pop.getDrawingCache();

                    //準備一個"記憶體空間"(這塊空間要能被Output出我的資料)
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //呼叫Bitmap的compress函數將圖以指定的格式輸出到記憶體空間中
                    //參數一 圖片格式
                    //參數二 壓縮率(0~100 100不壓)
                    //參數三 記憶體空間
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    //再將記憶體中的資料轉成程式可以處理的byte陣列
                    byte[] picture = stream.toByteArray();


                    //打開資料庫 將上述資料insert

                    SQLiteDatabase db = SQLiteDatabase.openDatabase(
                            "/data/data/com.example.teacher.shooting_sqlte/fruits.sqlite",
                            null,
                            SQLiteDatabase.OPEN_READWRITE
                    );

                    if(db != null){

                        ContentValues cv = new ContentValues();
                        cv.put("number", number);
                        cv.put("name", name);
                        cv.put("price", price);
                        cv.put("picture", picture);
                        long result=db.insert("summer",null,cv);
                        if(result!=-1){
                            Toast.makeText(this,"新增成功!",Toast.LENGTH_SHORT).show();
                        }

                        //重新裝載資料　更新GridView畫面

                        Cursor cur_new = db.query(
                                "summer",
                                new String[]{"number","name","price","picture"},
                                null,
                                null,
                                null,
                                null,
                                null
                        );
                        numbers.clear();
                        names.clear();
                        prices.clear();
                        pictures.clear();
                        while(cur_new.moveToNext()){

                            int no = cur_new.getInt(0);
                            String na = cur_new.getString(1);
                            float pr = cur_new.getFloat(2);
                            byte[] pi = cur_new.getBlob(3);
                            Bitmap pi_real = BitmapFactory.decodeByteArray(pi,0,pi.length);
                            numbers.add(no);
                            names.add(na);
                            prices.add(pr);
                            pictures.add(pi_real);
                        }

                        my_adapter controler = new my_adapter();
                        gv.setAdapter(controler);


                        db.close();

                        pop_insert.dismiss();
                        mHideHandler.removeCallbacks(mHideRunnable);
                        mHideHandler.postDelayed(mHideRunnable, 0);


                    }else{
                        //開起失敗通知

                    }



                }else{

                    //TODO 按下更新按鈕

                    //抓取迸現視窗上的各個值
                    //新增入資料庫
                    int number = Integer.parseInt(number_pop.getText().toString());
                    String name = name_pop.getText().toString();
                    float price = Float.parseFloat(price_pop.getText().toString());
                    ////由ImageView取出圖片  再轉成byte[] (因為資料庫blob是byte[])


                    //呼叫ImageView的setDrawingCacheEnabled並傳入true(將IMageView中的圖打開Cache-->表示可以佔存到記憶體)
                    picture_pop.setDrawingCacheEnabled(true);
                    //呼叫ImageView的buildDrawingCache(執行Cache--->把圖真的放到Cache記憶體了)
                    picture_pop.buildDrawingCache();
                    //呼叫ImageView的getDrawingCache(取出放到記憶體的Cache--->就是一張Bitmap)
                    Bitmap bm = picture_pop.getDrawingCache();

                    //準備一個"記憶體空間"(這塊空間要能被Output出我的資料)
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //呼叫Bitmap的compress函數將圖以指定的格式輸出到記憶體空間中
                    //參數一 圖片格式
                    //參數二 壓縮率(0~100 100不壓)
                    //參數三 記憶體空間
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    //再將記憶體中的資料轉成程式可以處理的byte陣列
                    byte[] picture = stream.toByteArray();


                    //打開資料庫 將上述資料insert

                    SQLiteDatabase db = SQLiteDatabase.openDatabase(
                            "/data/data/com.example.teacher.shooting_sqlte/fruits.sqlite",
                            null,
                            SQLiteDatabase.OPEN_READWRITE
                    );

                    if(db != null){

                        ContentValues cv = new ContentValues();
                        cv.put("number", number);
                        cv.put("name", name);
                        cv.put("price", price);
                        cv.put("picture", picture);
                        int result=db.update("summer",cv,"number=?",new String[]{number+""});
                        if(result==1){
                            Toast.makeText(this,"更新成功!",Toast.LENGTH_SHORT).show();
                        }

                        //重新裝載資料　更新GridView畫面

                        Cursor cur_new = db.query(
                                "summer",
                                new String[]{"number","name","price","picture"},
                                null,
                                null,
                                null,
                                null,
                                null
                        );
                        numbers.clear();
                        names.clear();
                        prices.clear();
                        pictures.clear();
                        while(cur_new.moveToNext()){

                            int no = cur_new.getInt(0);
                            String na = cur_new.getString(1);
                            float pr = cur_new.getFloat(2);
                            byte[] pi = cur_new.getBlob(3);
                            Bitmap pi_real = BitmapFactory.decodeByteArray(pi,0,pi.length);
                            numbers.add(no);
                            names.add(na);
                            prices.add(pr);
                            pictures.add(pi_real);
                        }

                        my_adapter controler = new my_adapter();
                        gv.setAdapter(controler);


                        db.close();

                        pop_insert.dismiss();
                        mHideHandler.removeCallbacks(mHideRunnable);
                        mHideHandler.postDelayed(mHideRunnable, 0);


                    }else{
                        //開起失敗通知

                    }





                }





                break;
            case R.id.iu_cancel_button:
                break;


        }

    }
    //TODO 拍照結果處理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Toast.makeText(this,"xxxx!", Toast.LENGTH_SHORT).show();
        if(requestCode==777){

            if(resultCode == Activity.RESULT_OK){


                if(data!=null){
                    Bundle extras = data.getExtras();
                    if(extras!=null){

                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if(imageBitmap!=null){
                            picture_pop.setImageBitmap(imageBitmap);
                        }else{
                            Toast.makeText(this,"圖片空!", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Toast.makeText(this,"夾帶空!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(this,"Intent空!", Toast.LENGTH_SHORT).show();
                }


            }


        }

    }


}
