package com.example.test;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import android.Manifest;

public class MainActivity extends AppCompatActivity{
    private FirebaseAuth auth;
    private static final int REQUEST_PERMISSIONS_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        requestPermissions();

        ImageButton listBtn = findViewById(R.id.list_btn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        ImageButton cctvBtn = findViewById(R.id.cctv_btn);
        cctvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CCTVActivity.class);
                startActivity(intent);
            }
        });

        ImageButton chadanBtn = findViewById(R.id.chadan_btn);
        chadanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChadanActivity.class);
                startActivity(intent);
            }
        });

        ImageButton sirenBtn = findViewById(R.id.siren_btn);
        sirenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:010-2351-6364"));
                startActivity(intent);
            }
        });

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃
                auth.signOut();
                // 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void requestPermissions() {
        // 요청할 권한 배열
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.POST_NOTIFICATIONS
        };

        // 권한이 허용되지 않은 경우 요청합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!allPermissionsGranted(permissions)) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE);
            }
        }
    }
    // 모든 권한이 허용되었는지 확인합니다.
    private boolean allPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        if (intent != null) {
            String notificationData = intent.getStringExtra("test");
            if (notificationData != null) {
                Log.d(TAG, "FCM Notification Data: " + notificationData);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    // 권한이 허용되지 않은 경우 필요한 동작을 수행합니다.
                    // 예: 권한이 필요하다는 메시지를 사용자에게 표시하거나 앱을 종료합니다.
                }
            }
            if (!allGranted) {
                // 권한이 거부된 경우 사용자에게 메시지를 표시합니다.
                new AlertDialog.Builder(this)
                        .setMessage("앱의 원활한 사용을 위해 권한이 필요합니다.")
                        .setPositiveButton("설정으로 이동", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("종료", (dialog, which) -> {
                            finish();
                        })
                        .create()
                        .show();
                }
            }
    }
}