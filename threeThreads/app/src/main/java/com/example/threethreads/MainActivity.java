package com.example.threethreads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.threethreads.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    static Object lock1 = new Object();
    public static String itog = "";
    Handler handler;

    static int order = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                binding.tv.setText((String) msg.obj);
            }
        };
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tv.setText("");
                itog = "";
                String s1 = binding.et1.getText().toString();
                String s2 = binding.et2.getText().toString();
                String s3 = binding.et3.getText().toString();
                MyThread t1 = new MyThread(s1, 0);
                MyThread t2 = new MyThread(s2, 1);
                MyThread t3 = new MyThread(s3, 2);
                t1.start();
                t2.start();
                t3.start();
            }
        });



    }

    class MyThread extends Thread {
        private String text;
        private int number;


        public MyThread(String text, int number) {
            this.text = text;
            this.number = number;
        }

        @Override
        public void run() {
            super.run();
            char[] textchars = text.toCharArray();
            for (int i = 0; i < textchars.length; i++) {
                synchronized (lock1) {
                    while (order != this.number) {
                        try {
                            lock1.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (textchars[i] == ' ') {
                        itog += textchars[i]+" ";
                        Message msg = new Message();
                        msg.obj = itog;
                        handler.sendMessage(msg);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        order = (this.number + 1) % 3;
                        lock1.notifyAll();
                    } else if (i == textchars.length - 1) {
                        try {
                            Thread.sleep(200);
                            itog += textchars[i]+" ";
                            Message msg = new Message();
                            msg.obj = itog;
                            handler.sendMessage(msg);
                            order = (this.number + 1) % 3;
                            lock1.notifyAll();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Thread.sleep(200);
                            itog += textchars[i];
                            Message msg = new Message();
                            msg.obj = itog;
                            handler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}