package com.example.hospitalserviceapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class AdminPanelActivity extends AppCompatActivity {

    private ListView lvRequests;
    private Button btnLogout;
    private BottomNavigationView bottomNavigationView;
    private HospitalDatabase db;
    private ArrayList<String> requestList;
    private ArrayList<Integer> requestIds;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        db = new HospitalDatabase(this);
        lvRequests = findViewById(R.id.lv_requests);
        btnLogout = findViewById(R.id.btn_admin_logout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        requestList = new ArrayList<>();
        requestIds = new ArrayList<>();

        loadRequests();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Handle clicking a request to update its status
        lvRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showUpdateStatusDialog(position);
            }
        });

        // Handle long-clicking a request to delete it
        lvRequests.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteRequestDialog(position);
                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_requests) {
                    loadRequests();
                    return true;
                } else if (id == R.id.nav_services) {
                    startActivity(new Intent(AdminPanelActivity.this, ServiceManagementActivity.class));
                    return true;
                } else if (id == R.id.nav_users) {
                    startActivity(new Intent(AdminPanelActivity.this, UserManagementActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void loadRequests() {
        requestList.clear();
        requestIds.clear();
        Cursor cursor = db.getAllRequests();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String service = cursor.getString(cursor.getColumnIndexOrThrow("service"));
                String ward = cursor.getString(cursor.getColumnIndexOrThrow("ward"));
                String bed = cursor.getString(cursor.getColumnIndexOrThrow("bed"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                
                requestIds.add(id);
                requestList.add(service + " - Ward: " + ward + ", Bed: " + bed + "\nStatus: " + status);
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requestList);
        lvRequests.setAdapter(adapter);
    }

    private void showUpdateStatusDialog(final int position) {
        final String[] statuses = {"Pending", "In Progress", "Completed"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Request Status");
        builder.setItems(statuses, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.updateRequestStatus(requestIds.get(position), statuses[which]);
                loadRequests();
                Toast.makeText(AdminPanelActivity.this, "Status updated to " + statuses[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void showDeleteRequestDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Request");
        builder.setMessage("Are you sure you want to delete this service request?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteRequest(requestIds.get(position));
                loadRequests();
                Toast.makeText(AdminPanelActivity.this, "Request deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadRequests();
        bottomNavigationView.setSelectedItemId(R.id.nav_requests);
    }
}