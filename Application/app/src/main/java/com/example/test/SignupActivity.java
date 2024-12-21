package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_sign);

        // 계정 생성 버튼
        Button signupOkButton = findViewById(R.id.signup_okButton);
        signupOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText signupID = findViewById(R.id.signupID);
                EditText signupPassword = findViewById(R.id.signupPassword);
                createAccount(signupID.getText().toString(), signupPassword.getText().toString());
            }
        });
    }

    // 계정 생성
    private void createAccount(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "계정 생성 완료.", Toast.LENGTH_SHORT).show();
                            finish(); // 가입창 종료
                        } else {
                            Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}