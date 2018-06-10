package com.frank.mycontrol;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.ConsumerIrManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private final int MSG_SMS = 1;
    private String TAG = "MainActivity";
    private Button start, pause, stop;
    private TextView txtNum;
    private SMSContent smsContent;          //监听短信
    private int carrierFrequency = 38000;   //红外线频率
    private ConsumerIrManager IR;           //红外线管理器
    boolean IRBack = false;
    private SoundPool soundPool;            //管理声音
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button)findViewById(R.id.start);
        pause = (Button)findViewById(R.id.pause);
        stop = (Button)findViewById(R.id.stop);
        txtNum = (TextView)findViewById(R.id.txt_num);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        IR = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
        if(IR != null){
            IRBack = IR.hasIrEmitter();
        }
        if (!IRBack){
            start.setEnabled(false);
            pause.setEnabled(false);
            stop.setEnabled(false);
            Toast.makeText(MainActivity.this, "我木有红外设备", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "红外设备准备就绪", Toast.LENGTH_SHORT).show();
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartSession();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PauseSession();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopSession();
            }
        });

        //设置短信监听器
        smsContent = new SMSContent(handler);
    }

    // 投放 1
    private void StartSession(){
        soundPool.load(getApplicationContext(), R.raw.start, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                soundPool.play(i, 0.9f, 0.9f, 0, 0, 1);
            }
        });

        IR.transmit(carrierFrequency, CodeCommand.start);
        Log.e(TAG, "start");
    }

    // 暂停 3
    private void PauseSession(){
        soundPool.load(getApplicationContext(), R.raw.pause, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                soundPool.play(i, 0.9f, 0.9f, 0, 0, 1);
            }
        });
        IR.transmit(carrierFrequency, CodeCommand.pause);
        Log.e(TAG, "pause");
    }

    // 回收 2
    private void StopSession(){
        soundPool.load(getApplicationContext(), R.raw.stop, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                soundPool.play(i, 0.9f, 0.9f, 0, 0, 1);
            }
        });
        IR.transmit(carrierFrequency, CodeCommand.stop);
        Log.e(TAG, "stop");
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MSG_SMS:
                    setSMS();
                    break;
            }
        }
    };

    //获取短信内容时注意权限问题，版本23以上都需要动态申请权限，权宜之计就是把targetapi为23以下
    private void setSMS(){
        Log.d(TAG, "收到短信了");
        Cursor cursor = null;
        try{
            cursor = getContentResolver().query(
                    Uri.parse("content://sms/inbox"),
                    null,
                    null, null, "date desc");
            if(cursor!=null){
                if(cursor.moveToNext()){
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    if(body!=null){
                        char num = body.charAt(0);
                        if(num>='0' && num<='3'){
                            txtNum.setText(body);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

    }

    // 短信监听器
    class SMSContent extends ContentObserver{
        private Handler mHandler;

        public SMSContent(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mHandler.obtainMessage(MSG_SMS, "Acquire SMS").sendToTarget();
        }

    }

    @Override
    protected void onDestroy() {
        soundPool.release();
        super.onDestroy();
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (smsContent != null) {
            getContentResolver().registerContentObserver(
                    Uri.parse("content://sms/"), true, smsContent);// 注册监听短信数据库的变化
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (smsContent != null) {
            getContentResolver().unregisterContentObserver(smsContent);// 取消监听短信数据库的变化
        }

    }

}
