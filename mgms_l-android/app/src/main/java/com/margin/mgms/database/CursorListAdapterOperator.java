package com.margin.mgms.database;

import android.database.Cursor;

import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;

/**
 * Created by marta.ginosyan on 8/29/2016.
 */
public class CursorListAdapterOperator<T> implements Observable.Operator<List<T>, SqlBrite.Query> {

    private final Func1<Cursor, T> mapper;

    public CursorListAdapterOperator(Func1<Cursor, T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Subscriber<? super SqlBrite.Query> call(final Subscriber<? super List<T>> subscriber) {
        return new Subscriber<SqlBrite.Query>(subscriber) {

            final CursorListAdapter<T> list = new CursorListAdapter<>(mapper);

            @Override
            public void onNext(SqlBrite.Query query) {
                try {
                    list.replaceCursor(query.run());
//                    list.closeCursor();
//                    list.setCursor(query.run());
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(list);
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    onError(OnErrorThrowable.addValueAsLastCause(e, query.toString()));
                }
            }

            @Override
            public void onCompleted() {
                try {
                    subscriber.onCompleted();
                } finally {
//                    list.closeCursor();
                }
            }

            @Override
            public void onError(Throwable e) {
                try {
                    subscriber.onError(e);
                } finally {
//                    list.closeCursor();
                }
            }
        };
    }
}
