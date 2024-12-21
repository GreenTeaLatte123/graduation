package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ChadanActivity extends AppCompatActivity {

    private String TAG = ChadanActivity.class.getSimpleName();
    private WebView webView = null;
    private Handler mHandler;
    Socket socket;
    EditText inputText;
    EditText ipText; // IP 주소 입력 EditText
    TextView logText;
    private int port = 8080; // PORT번호

    @Override
    protected void onStop() {
        super.onStop();
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chadan);

        mHandler = new Handler();

        inputText = (EditText) findViewById(R.id.inputEditText);
        ipText = (EditText) findViewById(R.id.inputEditText); // IP 주소 입력 EditText
        Button sendMsgButton = (Button) findViewById(R.id.sendMsgButton);
        ImageButton imageButton = (ImageButton) findViewById(R.id.back_button);

        logText = (TextView) findViewById(R.id.logTextView);

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputText.getText().toString();
                if (message != null && !message.equals("")) {
                    String ip = ipText.getText().toString();
                    if (ip != null && !ip.equals("")) {
                        buttonThread th = new buttonThread(ip, message);
                        th.start();
                    } else {
                        logText.setText("IP 주소를 입력하세요.\n");
                    }
                }
            }
        });

        ImageButton backBtn = (ImageButton) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChadanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton chadanBtn = (ImageButton) findViewById(R.id.chadanBtn);
        chadanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipText.getText().toString();
                if (ip != null && !ip.equals("")) {
                    buttonThread bt = new buttonThread(ip, "STOP");
                    bt.start();
                } else {
                    logText.setText("IP 주소를 입력하세요.\n");
                }
            }
        });

        ImageButton comebackBtn = (ImageButton) findViewById(R.id.comebackBtn);
        comebackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipText.getText().toString();
                if (ip != null && !ip.equals("")) {
                    buttonThread bt = new buttonThread(ip, "BACK");
                    bt.start();
                } else {
                    logText.setText("IP 주소를 입력하세요.\n");
                }
            }
        });

    }

    class buttonThread extends Thread {
        String ip;
        String msg;

        public buttonThread(String ip, String msg) {
            this.ip = ip;
            this.msg = msg;
        }

        public void run() {
            try {
                // 소켓 생성
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, port);
                // 입력 메시지
                String sndMsg = msg;
                Log.d("=============", sndMsg);
                // 데이터 전송
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(sndMsg);
                // 데이터 수신
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String read = input.readLine();
                // 화면 출력
                mHandler.post(new msgUpdate(read));
                Log.d("=============", read);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 받은 메시지 출력
    class msgUpdate implements Runnable {
        private String msg;

        public msgUpdate(String str) {
            this.msg = str;
        }

        public void run() {
            logText.setText(logText.getText().toString() + msg + "\n");
        }
    }
}
