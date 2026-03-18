package com.example.hospitalserviceapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class UserManagementActivity extends AppCompatActivity {

    private ListView lvUsers;
    private HospitalDatabase db;
    private ArrayList<String> usersList;
    private ArrayList<Integer> userIds;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        db = new HospitalDatabase(this);
        lvUsers = findViewById(R.id.lv_users_list);
        usersList = new ArrayList<>();
        userIds = new ArrayList<>();

        loadUsers();

        lvUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int userId = userIds.get(position);
                db.deleteUser(userId);
                loadUsers();
                Toast.makeText(UserManagementActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void loadUsers() {
        usersList.clear();
        userIds.clear();
        Cursor cursor = db.getAllUsers();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
                
                userIds.add(id);
                usersList.add(name + " [" + role + "]");
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersList);
        lvUsers.setAdapter(adapter);
    }
}