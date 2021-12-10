package com.example.chatting_program;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatting_program.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    final static String IP = "127.0.0.1";
    final static int PORT = 9876;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        new Hi(binding).start();

    }
}

class Hi {
    boolean connect = false;
    ActivityMainBinding binding;

    Hi(ActivityMainBinding binding) {
        this.binding = binding;
    }

    Socket socket;
    // TextArea textArea;

    // 클라이언트 프로그램 동작 메소드
    public void startClient(String IP, int port) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    socket = new Socket(IP, port);
                    receive();
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        stopClient();
                        System.out.println("[서버 접속 실패]");
                        //  Platform.exit(); 자바FX용
                    }
                }
            }
        };
        thread.start();
    }

    // 클라이언트 프로그램 종료 메소드
    public void stopClient() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 서버로부터 메세지를 전달받는 메소드
    public void receive() {
        while (true) {
            try {
                InputStream in = socket.getInputStream();
                byte[] buffer = new byte[512];
                int length = in.read(buffer);
                if (length == -1) throw new IOException();
                String message = new String(buffer, 0, length, "UTF-8");
               /* Platform.runLater(() -> {
                    textArea.appendText(message);
                });*/
                binding.tvMessage.setText(message);

            } catch (Exception e) {
                stopClient();
                break;
            }
        }
    }

    // 서버로 메세지를 전송하는 메소드
    public void send(String message) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    OutputStream out = socket.getOutputStream();
                    byte[] buffer = message.getBytes("UTF-8");
                    out.write(buffer);
                    out.flush();
                } catch (Exception e) {
                    stopClient();
                }
            }
        };
        thread.start();
    }


    public void start() {
        Log.d("start 시도:","성공");
        //접속 클릭하면 startClient 실행

        binding.btConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("connect 시도:","성공");
                if (connect == false) {
                    try {
                        Log.d("connect 시도:","성공");
                        startClient(MainActivity.IP, MainActivity.PORT);
                        binding.tvMessage.setText("채팅방 접속");
                        binding.btConnect.setText("종료하기");
                        connect = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                   /* binding.tvMessage.setText("채팅방 퇴장");
                    binding.btConnect.setText("접속하기");
                    connect = false;*/
                }

            }
        });

        binding.btSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(binding.tvMessage.getText().toString());
            }
        });
    }
}