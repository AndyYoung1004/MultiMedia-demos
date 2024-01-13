package com.example.multimedia.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.multimedia.R;

public class LooperHandlerActivity extends AppCompatActivity {
    private static final String TAG = "LooperHandlerActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
//                mHandler1.sendEmptyMessage(0);
            }
        }).start();
//        mHandler2.sendEmptyMessage(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler3 = new Handler(Looper.myLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 0) {
                            Toast.makeText(getApplicationContext(), "3收到消息", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                Looper.loop();
            }
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        mHandler3.sendEmptyMessage(0);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "1收到消息", Toast.LENGTH_SHORT).show();
            }
        }
    };
    Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(getApplicationContext(), "2收到消息", Toast.LENGTH_LONG).show();
            }
        }
    };
    Handler mHandler3;
}