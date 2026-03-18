package com.example.hospitalserviceapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private RadioGroup rgLoginRole;
    private HospitalDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new HospitalDatabase(this);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        rgLoginRole = findViewById(R.id.rg_login_role);
        Button loginButton = findViewById(R.id.btn_login);
        TextView registerLink = findViewById(R.id.tv_register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUsername.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedId = rgLoginRole.getCheckedRadioButtonId();
                String selectedRole = (selectedId == R.id.rb_login_admin) ? "admin" : "patient";

                Cursor cursor = db.loginUser(user, pass);
                if (cursor != null && cursor.moveToFirst()) {
                    String dbRole = cursor.getString(cursor.getColumnIndexOrThrow("role"));
                    
                    // Verify if the selected role matches the database role
                    if (selectedRole.equals(dbRole)) {
                        Toast.makeText(LoginActivity.this, "Login Successful as " + dbRole, Toast.LENGTH_SHORT).show();
                        
                        Intent intent;
                        if ("admin".equals(dbRole)) {
                            intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "User role mismatch. Select the correct role.", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}