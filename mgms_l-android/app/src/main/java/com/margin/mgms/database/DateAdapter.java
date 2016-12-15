package com.margin.mgms.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.squareup.sqldelight.ColumnAdapter;

import java.util.Date;

/**
 * Created on Jun 02, 2016.
 *
 * @author Marta.Ginosyan
 */
public final class DateAdapter implements ColumnAdapter<Date> {

    @Override
    public void marshal(ContentValues contentValues, String columnName, Date date) {
        contentValues.put(columnName, date.getTime());
    }

    @Override
    public Date map(Cursor cursor, int columnIndex) {
        return new Date(cursor.getLong(columnIndex));
    }
}
