package com.example.hospitalserviceapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerServices;
    private TextInputEditText etNotes, etWard, etBed;
    private Button btnSubmit, btnLogout;
    private HospitalDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new HospitalDatabase(this);
        spinnerServices = findViewById(R.id.spinner_services);
        etNotes = findViewById(R.id.et_notes);
        etWard = findViewById(R.id.et_ward);
        etBed = findViewById(R.id.et_bed);
        btnSubmit = findViewById(R.id.btn_submit_request);
        btnLogout = findViewById(R.id.btn_logout);

        loadServices();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadServices() {
        ArrayList<String> services = new ArrayList<>();
        Cursor cursor = db.getAllServices();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                services.add(cursor.getString(cursor.getColumnIndexOrThrow("service_name")));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Add defaults if database is empty
        if (services.isEmpty()) {
            services.add("Cleaning");
            services.add("Equipment assistance");
            services.add("Linen change");
            services.add("Porter services");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, services);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServices.setAdapter(adapter);
    }

    private void submitRequest() {
        String selectedService = spinnerServices.getSelectedItem() != null ? spinnerServices.getSelectedItem().toString() : "";
        String notes = etNotes.getText().toString().trim();
        String ward = etWard.getText().toString().trim();
        String bed = etBed.getText().toString().trim();

        if (selectedService.isEmpty() || ward.isEmpty() || bed.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = db.addRequest(selectedService, notes, ward, bed);
        if (id != -1) {
            Toast.makeText(this, "Request Submitted Successfully!", Toast.LENGTH_LONG).show();
            etNotes.setText("");
            etWard.setText("");
            etBed.setText("");
        } else {
            Toast.makeText(this, "Submission Failed", Toast.LENGTH_SHORT).show();
        }
    }
}