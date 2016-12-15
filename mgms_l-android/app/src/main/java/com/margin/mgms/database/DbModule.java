package com.margin.mgms.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.schedulers.Schedulers;

/**
 * Created on Jun 01, 2016.
 *
 * @author Marta.Ginosyan
 */
@Module
public final class DbModule {
    @Provides
    @Singleton
    public SQLiteOpenHelper provideOpenHelper(Context context) {
        return MgmsOpenHelper.getInstance(context);
    }

    @Provides
    @Singleton
    public SqlBrite provideSqlBrite() {
        return SqlBrite.create(message -> {
                    // TODO: temporary switching off DB logs, because of logcat pollution
                    // if (BuildConfig.DEBUG) Log.d("MGMS Database", message);
                }
        );
    }

    @Provides
    @Singleton
    public BriteDatabase provideDatabase(SqlBrite sqlBrite, SQLiteOpenHelper helper) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
        db.setLoggingEnabled(true);
        return db;
    }
}
