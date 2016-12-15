package com.margin.mgms.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.margin.camera.models.GeoLocation;
import com.margin.camera.models.Note;
import com.margin.camera.models.Photo;
import com.margin.camera.models.Property;
import com.margin.mgms.model.ShipmentLocation;
import com.margin.mgms.model.SpecialHandling;
import com.margin.mgms.model.Task;

/**
 * Created on Jun 01, 2016.
 *
 * @author Marta.Ginosyan
 */
final class MgmsOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static MgmsOpenHelper sInstance;

    private MgmsOpenHelper(Context context) {
        super(context, "mgms.db", null, DATABASE_VERSION);
    }

    static MgmsOpenHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MgmsOpenHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Task.CREATE_TABLE);
        db.execSQL(Task.CREATE_TASK_ID_INDEX);
        db.execSQL(Task.CREATE_ACTION_ID_INDEX);
        db.execSQL(Task.CREATE_DATE_INDEX);
        db.execSQL(Task.CREATE_STATUS_INDEX);
        db.execSQL(Task.CREATE_REFERENCE_INDEX);
        db.execSQL(SpecialHandling.CREATE_TABLE);
        db.execSQL(SpecialHandling.CREATE_TASK_ID_INDEX);
        db.execSQL(ShipmentLocation.CREATE_TABLE);
        db.execSQL(ShipmentLocation.CREATE_TASK_ID_INDEX);
        db.execSQL(Photo.CREATE_TABLE);
        db.execSQL(Photo.CREATE_TASK_ID_INDEX);
        db.execSQL(Photo.CREATE_PHOTO_ID_INDEX);
        db.execSQL(Photo.CREATE_REFERENCE_INDEX);
        db.execSQL(Note.CREATE_TABLE);
        db.execSQL(Note.CREATE_PHOTO_ID_INDEX);
        db.execSQL(Property.CREATE_TABLE);
        db.execSQL(Property.CREATE_PHOTO_ID_INDEX);
        db.execSQL(GeoLocation.CREATE_TABLE);
        db.execSQL(GeoLocation.CREATE_PHOTO_ID_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
