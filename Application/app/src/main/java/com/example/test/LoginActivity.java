package com.example.test;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        // 회원가입 창으로 이동
        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        // 로그인 버튼
        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText idEditText = findViewById(R.id.id);
                EditText passwordEditText = findViewById(R.id.pass);
                signIn(idEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });


    }

    // 로그아웃하지 않을 시 자동 로그인, 회원가입시 바로 로그인 됨
    @Override
    public void onStart() {
        super.onStart();
        moveMainPage(auth.getCurrentUser());
    }

    // 로그인
    private void signIn(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "로그인에 성공 하였습니다.", Toast.LENGTH_SHORT).show();
                            moveMainPage(auth.getCurrentUser());
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    private void moveMainPage(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}