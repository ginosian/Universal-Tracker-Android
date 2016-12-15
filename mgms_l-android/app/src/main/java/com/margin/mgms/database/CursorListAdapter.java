package com.margin.mgms.database;

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.util.Log;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 * Created by marta.ginosyan on 8/29/2016.
 */
public class CursorListAdapter<T>
        extends AbstractList<T> {

    private static final String TAG = "FG/CursorListAdapter";

    private final Func1<Cursor, T> mapper;
    private final DataSetObserver dataSetObserver;
    private final ContentObserver contentObserver;

    private Cursor cursor;
    private List<T> cache; // TODO: replace with LRU

    public CursorListAdapter(Func1<Cursor, T> mapper) {
        this.mapper = mapper;
        this.dataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                Log.d(TAG, "DataSetObserver.onChanged(" + cursor.hashCode() + ")");
                if (cache != null) {
                    cache.clear();
                }
            }

            @Override
            public void onInvalidated() {
                Log.d(TAG, "DataSetObserver.onInvalidated(" + cursor.hashCode() + ")");
                replaceCursor(null);
            }
        };
        this.contentObserver = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                Log.d(TAG, "ContentObserver.onChanged(" + cursor.hashCode() + ")");
                if (cache != null) {
                    cache.clear();
                }
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                Log.d(TAG, "ContentObserver.onChanged(" + cursor.hashCode() + ")");
                if (cache != null) {
                    cache.clear();
                }
            }
        };
    }

    public synchronized Cursor getCursor() {
        return this.cursor;
    }

    public synchronized List<T> getCache() {
        return this.cache;
    }

    public synchronized void replaceCursor(Cursor cursor) {
        Cursor oldCursor = this.cursor;
        this.cursor = cursor;
        if (oldCursor != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver);
            oldCursor.unregisterContentObserver(contentObserver);
            if (!oldCursor.isClosed()) {
                oldCursor.close();
            }
        }
        if (cursor != null) {
            cache = new ArrayList<>(cursor.getCount());
            cursor.registerDataSetObserver(dataSetObserver);
            cursor.registerContentObserver(contentObserver);
        } else {
            cache = null;
        }
    }

    @Override
    public synchronized T get(int location) {
        T ret = cache.get(location);
        if (ret == null) {
            if (cursor == null) {
                throw new IndexOutOfBoundsException("cursor is null");
            } else if (cursor.isClosed()) {
                throw new IndexOutOfBoundsException("cursor is closed");
            } else if (!cursor.moveToPosition(location)) {
                throw new IndexOutOfBoundsException("moveToPosition failed: " + location);
            }
            ret = mapper.call(cursor);
            cache.add(location, ret);
        }
        return ret;
    }

    @Override
    public synchronized int size() {
        return cursor != null && !cursor.isClosed()
                ? cursor.getCount()
                : 0;
    }

    @Override
    public synchronized boolean isEmpty() {
        return cursor == null
                || cursor.isClosed()
                || cursor.getCount() <= 0;
    }
}
