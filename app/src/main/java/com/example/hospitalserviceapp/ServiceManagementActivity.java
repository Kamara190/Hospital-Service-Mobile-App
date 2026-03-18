package com.example.hospitalserviceapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class ServiceManagementActivity extends AppCompatActivity {

    private TextInputEditText etName, etCode;
    private Button btnAdd;
    private ListView lvServices;
    private HospitalDatabase db;
    private ArrayList<String> servicesList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_management);

        db = new HospitalDatabase(this);
        etName = findViewById(R.id.et_new_service_name);
        etCode = findViewById(R.id.et_new_service_code);
        btnAdd = findViewById(R.id.btn_add_service);
        lvServices = findViewById(R.id.lv_services_list);
        servicesList = new ArrayList<>();

        loadServices();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String code = etCode.getText().toString().trim();

                if (!name.isEmpty() && !code.isEmpty()) {
                    db.addService(name, code);
                    etName.setText("");
                    etCode.setText("");
                    loadServices();
                    Toast.makeText(ServiceManagementActivity.this, "Service Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadServices() {
        servicesList.clear();
        Cursor cursor = db.getAllServices();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("service_name"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("service_id"));
                servicesList.add(name + " (" + code + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, servicesList);
        lvServices.setAdapter(adapter);
    }
}