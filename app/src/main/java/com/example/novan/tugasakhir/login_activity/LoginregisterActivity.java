package com.example.novan.tugasakhir.login_activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.novan.tugasakhir.MainActivity;
import com.example.novan.tugasakhir.R;
import com.example.novan.tugasakhir.util.database.SessionManager;

public class LoginregisterActivity extends AppCompatActivity {
    Button login, signup;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginregisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginregisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signup = (Button) findViewById(R.id.btn_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginregisterActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
