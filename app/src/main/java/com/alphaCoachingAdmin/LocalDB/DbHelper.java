package com.alphaCoachingAdmin.LocalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;


import com.alphaCoachingAdmin.ModelClass.Standard;
import com.alphaCoachingAdmin.ModelClass.Subject;

import java.util.ArrayList;
import java.util.Map;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "alphaAdmin.db";
    private static DbHelper mInstance = null;
    private Context mContext;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static DbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        create category table
        StringBuilder createStandardsTable = new StringBuilder();
        StringBuilder STANDARDS_QUERY = new StringBuilder();
        STANDARDS_QUERY.append(DbTables.StandardsTable.ID).append(" TEXT PRIMARY KEY, ")
                .append(DbTables.StandardsTable.STANDARD).append(" INTEGER");

        createStandardsTable.append("CREATE TABLE ")
                .append(DbTables.StandardsTable.TABLE_NAME)
                .append(" (")
                .append(STANDARDS_QUERY)
                .append(" );");
        db.execSQL(createStandardsTable.toString());


//        create item table
        StringBuilder createSubjectTable = new StringBuilder();
        StringBuilder ITEM_QUERY = new StringBuilder();
        ITEM_QUERY.append(DbTables.SubjectTable.ID).append(" TEXT PRIMARY KEY, ")
                .append(DbTables.SubjectTable.SUBJECT_NAME).append(" TEXT, ")
                .append(DbTables.SubjectTable.STANDARD_ID).append(" TEXT");

        createSubjectTable.append("CREATE TABLE ")
                .append(DbTables.SubjectTable.TABLE_NAME)
                .append(" (")
                .append(ITEM_QUERY)
                .append(" );");
        db.execSQL(createSubjectTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addStandard(String id, Long value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbTables.StandardsTable.ID, id);
        contentValues.put(DbTables.StandardsTable.STANDARD, value);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.insertWithOnConflict(DbTables.StandardsTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Standard> getAllStandards() {
        ArrayList<Standard> standards = new ArrayList<>();
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM ")
                    .append(DbTables.StandardsTable.TABLE_NAME);

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query.toString(), null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    Standard model = new Standard();
                    model.setId(cursor.getString(0));
                    model.setStandard(cursor.getLong(1));
                    standards.add(model);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return standards;
    }

    public Standard getStandard(String id) {
        Standard standard = new Standard();
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM ")
                    .append(DbTables.StandardsTable.TABLE_NAME)
                    .append(" WHERE ")
                    .append(DbTables.StandardsTable.ID)
                    .append(" = '")
                    .append(id)
                    .append("'");

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query.toString(), null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    Standard model = new Standard();
                    model.setId(cursor.getString(0));
                    model.setStandard(cursor.getLong(1));
                    standard = model;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return standard;
    }

    public void addSubject(String id, String standard, String subName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbTables.SubjectTable.ID, id);
        contentValues.put(DbTables.SubjectTable.STANDARD_ID, standard);
        contentValues.put(DbTables.SubjectTable.SUBJECT_NAME, subName);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.insertWithOnConflict(DbTables.SubjectTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> subjects = new ArrayList<>();
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM ")
                    .append(DbTables.SubjectTable.TABLE_NAME);

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query.toString(), null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    Subject model = new Subject();
                    model.setId(cursor.getString(0));
                    model.setName(cursor.getString(1));
                    model.setStandard(cursor.getString(2));
                    subjects.add(model);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public Subject getSubject(String id) {
        Subject subject = new Subject();
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM ")
                    .append(DbTables.SubjectTable.TABLE_NAME)
                    .append(" WHERE ")
                    .append(DbTables.SubjectTable.ID)
                    .append(" = '")
                    .append(id)
                    .append("'");

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query.toString(), null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                Subject model = new Subject();
                model.setId(cursor.getString(0));
                model.setName(cursor.getString(1));
                model.setStandard(cursor.getString(2));
                subject = model;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subject;
    }

//
//    public void deleteCategory(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.beginTransaction();
//        try {
//            db.delete(DbTables.CategoryTable.TABLE_NAME, DbTables.CategoryTable.ID + " = '" + id + "'", null);
//            db.setTransactionSuccessful();
//        } finally {
//            db.endTransaction();
//            db.close();
//        }
//    }
//
//
//    public void addItem(Item item, byte[] image) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DbTables.ItemTable.ID, item.getId());
//        contentValues.put(DbTables.ItemTable.ITEM_NAME, item.getItem_name());
//        contentValues.put(DbTables.ItemTable.CATEGORY_NAME, item.getParent_category_name());
//        contentValues.put(DbTables.ItemTable.CATEGORY_ID, item.getParent_category_id());
//        contentValues.put(DbTables.ItemTable.IMAGE_LINK, image);
//        contentValues.put(DbTables.ItemTable.IMAGE_FIREBASE_NAME, item.getItem_image());
//        contentValues.put(DbTables.ItemTable.VEG_TYPE, item.getVeg_type());
//        contentValues.put(DbTables.ItemTable.ITEM_DESCRIPTION, item.getItem_description());
//        contentValues.put(DbTables.ItemTable.ITEM_PRICE, item.getPrice());
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.beginTransaction();
//        try {
//            db.insertWithOnConflict(DbTables.ItemTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
//            db.setTransactionSuccessful();
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    public ArrayList<Item> getAllItems() {
//        ArrayList<Item> items = new ArrayList<>();
//        StringBuilder query = new StringBuilder();
//        query.append("SELECT * FROM ")
//                .append(DbTables.ItemTable.TABLE_NAME);
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query.toString(), null);
//        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//            do {
//                Item model = new Item();
//                model.setId(cursor.getString(0));
//                model.setItem_name(cursor.getString(1));
//                model.setParent_category_name(cursor.getString(2));
//                model.setParent_category_id(cursor.getString(3));
//                model.setItem_image(cursor.getString(5));
//                model.setVeg_type(cursor.getString(6));
//                model.setItem_description(cursor.getString(7));
//                model.setPrice(cursor.getInt(8));
//
//                items.add(model);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return items;
//    }
//
//    public ArrayList<Item> getAllItems(String categoryId) {
//        ArrayList<Item> items = new ArrayList<>();
//        StringBuilder query = new StringBuilder();
//        query.append("SELECT * FROM ")
//                .append(DbTables.ItemTable.TABLE_NAME)
//                .append(" WHERE ")
//                .append(DbTables.ItemTable.CATEGORY_ID)
//                .append(" = '")
//                .append(categoryId)
//                .append("'");
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query.toString(), null);
//        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//            do {
//                Item model = new Item();
//                model.setId(cursor.getString(0));
//                model.setItem_name(cursor.getString(1));
//                model.setParent_category_name(cursor.getString(2));
//                model.setParent_category_id(cursor.getString(3));
//                model.setItem_image(cursor.getString(5));
//                model.setVeg_type(cursor.getString(6));
//                model.setItem_description(cursor.getString(7));
//                model.setPrice(cursor.getInt(8));
//
//                items.add(model);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return items;
//    }
//
//    public byte[] getItemImage(String id) {
//        byte[] img = null;
//        StringBuilder query = new StringBuilder();
//        query.append("SELECT * FROM ")
//                .append(DbTables.ItemTable.TABLE_NAME)
//                .append(" WHERE ")
//                .append(DbTables.ItemTable.ID)
//                .append(" = '")
//                .append(id)
//                .append("'");
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query.toString(), null);
//        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//            img = cursor.getBlob(4);
//        }
//        return img;
//    }
//
//    public void deleteItem(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.beginTransaction();
//        try {
//            db.delete(DbTables.ItemTable.TABLE_NAME, DbTables.ItemTable.ID + " = '" + id + "'", null);
//            db.setTransactionSuccessful();
//        } finally {
//            db.endTransaction();
//            db.close();
//        }
//    }
}

