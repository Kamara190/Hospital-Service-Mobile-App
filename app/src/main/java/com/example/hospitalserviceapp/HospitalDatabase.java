package com.example.hospitalserviceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HospitalDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HospitalDB";
    private static final int DATABASE_VERSION = 2;

    public HospitalDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table for Users
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, password TEXT, role TEXT)");
        
        // Table for Service Requests
        db.execSQL("CREATE TABLE requests (id INTEGER PRIMARY KEY AUTOINCREMENT, service TEXT, notes TEXT, ward TEXT, bed TEXT, status TEXT)");
        
        // Table for Services
        db.execSQL("CREATE TABLE services (id INTEGER PRIMARY KEY AUTOINCREMENT, service_name TEXT, service_id TEXT)");
        
        // Default Admin User
        db.execSQL("INSERT INTO users (username, email, password, role) VALUES ('admin', 'admin@hospital.com', 'admin123', 'admin')");
        
        // Initial services as per Figure 1
        db.execSQL("INSERT INTO services (service_name, service_id) VALUES ('Cleaning', 'CL001')");
        db.execSQL("INSERT INTO services (service_name, service_id) VALUES ('Equipment assistance', 'EP002')");
        db.execSQL("INSERT INTO services (service_name, service_id) VALUES ('Linen change', 'LC001')");
        db.execSQL("INSERT INTO services (service_name, service_id) VALUES ('Porter services', 'PS001')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS requests");
        db.execSQL("DROP TABLE IF EXISTS services");
        onCreate(db);
    }

    // User Operations
    public long registerUser(String username, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);
        return db.insert("users", null, values);
    }

    public Cursor loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users", null);
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", "id=?", new String[]{String.valueOf(id)});
    }

    // Request Operations
    public long addRequest(String service, String notes, String ward, String bed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("service", service);
        values.put("notes", notes);
        values.put("ward", ward);
        values.put("bed", bed);
        values.put("status", "Pending");
        return db.insert("requests", null, values);
    }

    public Cursor getAllRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM requests", null);
    }

    public void updateRequestStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update("requests", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteRequest(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("requests", "id=?", new String[]{String.valueOf(id)});
    }

    // Service Operations
    public Cursor getAllServices() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM services", null);
    }
    
    public void addService(String name, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("service_name", name);
        values.put("service_id", code);
        db.insert("services", null, values);
    }
    
    public void deleteService(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("services", "id=?", new String[]{String.valueOf(id)});
    }
}