package com.margin.mgms.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.margin.camera.models.GeoLocation;
import com.margin.camera.models.Note;
import com.margin.camera.models.Photo;
import com.margin.camera.models.Property;
import com.margin.mgms.LipstickApplication;
import com.margin.mgms.model.ShipmentLocation;
import com.margin.mgms.model.SpecialHandling;
import com.margin.mgms.model.Task;
import com.margin.mgms.util.PrefsUtils;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created on Jun 01, 2016.
 *
 * @author Marta.Ginosyan
 */
public class DatabaseConnector {

    private static final String USERS_REGEXP = "(^|,)(%s)(,|$)";
    private static final String LIKE_EXP = "%%%s%%";

    private static DatabaseConnector sInstance;
    @Inject
    BriteDatabase mDb;

    private DatabaseConnector() {
        LipstickApplication.getAppComponent().inject(this);
    }

    public static DatabaseConnector getInstance() {
        if (sInstance == null) {
            sInstance = new DatabaseConnector();
        }
        return sInstance;
    }

    public Observable<Task> getTask(int actionId, String reference) {
        return Observable.create((Observable.OnSubscribe<Task>) subscriber ->
                mDb.createQuery(Task.TABLE_NAME, Task.SELECT_BY_REFERENCE,
                        String.valueOf(actionId), String.format(LIKE_EXP, reference))
                        .mapToOneOrDefault(Task.MAPPER::map, null)
                        .subscribe(fullFillTask(subscriber)));
    }

    public Observable<List<Task>> getCompletedTasksOrderByDate(int actionId, boolean ascending) {
        return Observable.create((Observable.OnSubscribe<List<Task>>) subscriber -> {
            String user = PrefsUtils.getUsername();
            String query;
            if (ascending) query = Task.SELECT_BY_STATUS_ORDER_BY_DATE_ASC;
            else query = Task.SELECT_BY_STATUS_ORDER_BY_DATE_DESC;
            mDb.createQuery(Task.TABLE_NAME, query, String.valueOf(actionId), Task.COMPLETED, user,
                    String.format(USERS_REGEXP, user)).mapToList(Task.MAPPER::map)
                    .subscribe(fullFillTasks(subscriber));
            subscriber.unsubscribe();
        });
    }

    public Observable<List<Task>> getNotCompletedTasksOrderByDate(int actionId, boolean ascending) {
        String user = PrefsUtils.getUsername();
        String query;
        if (ascending) query = Task.SELECT_NOT_COMPLETED_ORDER_BY_DATE_ASC;
        else query = Task.SELECT_NOT_COMPLETED_ORDER_BY_DATE_DESC;
//        return Observable.create((Observable.OnSubscribe<List<Task>>) subscriber -> {
//            String user = PrefsUtils.getUsername();
//            String query;
//            if (ascending) query = Task.SELECT_NOT_COMPLETED_ORDER_BY_DATE_ASC;
//            else query = Task.SELECT_NOT_COMPLETED_ORDER_BY_DATE_DESC;
//            mDb.createQuery(Task.TABLE_NAME, query, String.valueOf(actionId), user,
//                    String.format(USERS_REGEXP, user)).mapToList(Task.MAPPER::map)
//                    .subscribe(fullFillTasks(subscriber));
//        });


        Observable<SqlBrite.Query> tasks = mDb.createQuery(Task.TABLE_NAME, query, String.valueOf(actionId), user,
                String.format(USERS_REGEXP, user));
        tasks.subscribe(new Action1<SqlBrite.Query>() {
            @Override
            public void call(SqlBrite.Query query) {
                Cursor cursor = query.run();
                Task.MAPPER.map(cursor);

                // TODO parse data...
            }
        });
        return null;
    }

    public Observable<List<Task>> getCompletedTasksOrderByDate(int actionId, boolean ascending,
                                                               String queryReference) {
        if (TextUtils.isEmpty(queryReference)) {
            return getCompletedTasksOrderByDate(actionId, ascending);
        } else {
            return Observable.create((Observable.OnSubscribe<List<Task>>) subscriber -> {
                String user = PrefsUtils.getUsername();
                String query;
                if (ascending) query = Task.SELECT_BY_STATUS_AND_REFERENCE_ORDER_BY_DATE_ASC;
                else query = Task.SELECT_BY_STATUS_AND_REFERENCE_ORDER_BY_DATE_DESC;
                mDb.createQuery(
                        Task.TABLE_NAME, query, String.valueOf(actionId), Task.COMPLETED, user,
                        String.format(USERS_REGEXP, user), String.format(LIKE_EXP, queryReference))
                        .mapToList(Task.MAPPER::map).subscribe(fullFillTasks(subscriber));
                subscriber.unsubscribe();
            });
        }
    }

    public Observable<List<Task>> getNotCompletedTasksOrderByDate(int actionId, boolean ascending,
                                                                  String queryReference) {
        if (TextUtils.isEmpty(queryReference)) {
            return getNotCompletedTasksOrderByDate(actionId, ascending);
        } else {
            return Observable.create((Observable.OnSubscribe<List<Task>>) subscriber -> {
                String user = PrefsUtils.getUsername();
                String query;
                if (ascending) query = Task.SELECT_NOT_COMPLETED_BY_REFERENCE_ORDER_BY_DATE_ASC;
                else query = Task.SELECT_NOT_COMPLETED_BY_REFERENCE_ORDER_BY_DATE_DESC;
                mDb.createQuery(
                        Task.TABLE_NAME, query, String.valueOf(actionId), user,
                        String.format(USERS_REGEXP, user), String.format(LIKE_EXP, queryReference))
                        .mapToList(Task.MAPPER::map).subscribe(fullFillTasks(subscriber));
                subscriber.unsubscribe();
            });
        }
    }

    public Observable<List<Task>> getNotAssignedTasksOrderByDate(int actionId, boolean ascending) {
        return Observable.create((Observable.OnSubscribe<List<Task>>) subscriber -> {
            String query;
            if (ascending) query = Task.SELECT_NOT_ASSIGNED_ORDER_BY_DATE_ASC;
            else query = Task.SELECT_NOT_ASSIGNED_ORDER_BY_DATE_DESC;
            mDb.createQuery(
                    Task.TABLE_NAME, query, String.valueOf(actionId))
                    .mapToList(Task.MAPPER::map).subscribe(fullFillTasks(subscriber));
            subscriber.unsubscribe();
        });
    }

    public Observable<List<Task>> getNotAssignedTasksOrderByDate(int actionId, boolean ascending,
                                                                 String queryReference) {
        if (TextUtils.isEmpty(queryReference)) {
            return getNotAssignedTasksOrderByDate(actionId, ascending);
        } else return Observable.create((Observable.OnSubscribe<List<Task>>) subscriber -> {
            String query;
            if (ascending) query = Task.SELECT_NOT_ASSIGNED_BY_REFERENCE_ORDER_BY_DATE_ASC;
            else query = Task.SELECT_NOT_ASSIGNED_BY_REFERENCE_ORDER_BY_DATE_DESC;
            mDb.createQuery(Task.TABLE_NAME, query, String.valueOf(actionId),
                    String.format(LIKE_EXP, queryReference))
                    .mapToList(Task.MAPPER::map).subscribe(fullFillTasks(subscriber));
            subscriber.unsubscribe();
        });
    }

    public Observable<SpecialHandling> getSpecialHandling(String taskId) {
        return mDb.createQuery(SpecialHandling.TABLE_NAME, SpecialHandling.SELECT_BY_TASK_ID,
                taskId).mapToOne(SpecialHandling.MAPPER::map);
    }

    public Observable<List<ShipmentLocation>> getLocations(String taskId) {
        return mDb.createQuery(ShipmentLocation.TABLE_NAME, ShipmentLocation.SELECT_BY_TASK_ID,
                taskId).mapToList(ShipmentLocation.MAPPER::map);
    }

    public Observable<Void> updateTaskOwner(String taskId, String owner) {
        return Observable.create(subscriber -> {
            ContentValues data = new ContentValues();
            data.put(Task.OWNER, owner);
            mDb.update(Task.TABLE_NAME, data, "task_id = ?", taskId);
            subscriber.onNext(null);
            subscriber.onCompleted();
        });
    }

    public Observable<Void> updateTaskStatus(String taskId, Task.Status status) {
        return Observable.create(subscriber -> {
            ContentValues data = new ContentValues();
            data.put(Task.STATUS, status.toString());
            switch (status) {
                case InProgress:
                    data.put(Task.START_DATE, System.currentTimeMillis());
                    break;
                case Completed:
                    data.put(Task.END_DATE, System.currentTimeMillis());
                    break;
            }
            mDb.update(Task.TABLE_NAME, data, "task_id = ?", taskId);
            subscriber.onNext(null);
            subscriber.onCompleted();
        });
    }

    public Observable<Void> saveTask(@NonNull Task task) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mDb.newTransaction();
            try {
                insertTask(task);
                transaction.markSuccessful();
            } finally {
                transaction.end();
            }
            subscriber.onNext(null);
            subscriber.onCompleted();
        });
    }

    public Observable<Void> saveTasks(@NonNull List<Task> tasks, int actionId) {
        return Observable.create(subscriber -> {
            if (!tasks.isEmpty()) {
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    mDb.delete(Task.TABLE_NAME, "task.action_id = ?", String.valueOf(actionId));
                    for (Task task : tasks) insertTask(task);
                    transaction.markSuccessful();
                } finally {
                    transaction.end();
                }
            }
            subscriber.onNext(null);
            subscriber.onCompleted();
        });
    }

    private void insertTask(Task task) {
        mDb.insert(Task.TABLE_NAME, new Task.Marshal(task).asContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
        mDb.insert(SpecialHandling.TABLE_NAME,
                new SpecialHandling.Marshal(task.getSpecialHandling()).asContentValues());
        List<ShipmentLocation> locations = task.getLocations();
        if (locations != null && !locations.isEmpty()) {
            for (ShipmentLocation location : locations) {
                mDb.insert(ShipmentLocation.TABLE_NAME,
                        new ShipmentLocation.Marshal(location).asContentValues());
            }
        }
    }

    @NonNull
    private Action1<List<Task>> fullFillTasks(Subscriber<? super List<Task>> subscriber) {
        return tasks -> {
            for (Task task : tasks) fillTask(task);
            subscriber.onNext(tasks);
            subscriber.onCompleted();
        };
    }

    @NonNull
    private Action1<Task> fullFillTask(Subscriber<? super Task> subscriber) {
        return task -> {
            fillTask(task);
            subscriber.onNext(task);
            subscriber.onCompleted();
        };
    }

    private void fillTask(Task task) {
        if (task != null) {
            getSpecialHandling(task.task_id()).subscribe(task::setSpecialHandling);
            getLocations(task.task_id()).subscribe(task::setLocations);
        }
    }

    private Observable<List<Photo>> getPhotos(String query, boolean byReference) {
        String select = byReference ? Photo.SELECT_BY_REFERENCE : Photo.SELECT_BY_TASK_ID;
        return Observable.create((Observable.OnSubscribe<List<Photo>>) subscriber ->
                mDb.createQuery(Photo.TABLE_NAME, select, query)
                        .mapToList(Photo.MAPPER::map).subscribe(fullFillPhotos(subscriber)));
    }

    public Observable<List<Photo>> getPhotosByTaskId(String taskId) {
        return getPhotos(taskId, false);
    }

    public Observable<List<Photo>> getPhotosByReference(String reference) {
        return getPhotos(reference, true);
    }

    public Observable<GeoLocation> getGeoLocation(String photoId) {
        return mDb.createQuery(GeoLocation.TABLE_NAME, GeoLocation.SELECT_BY_PHOTO_ID, photoId)
                .mapToOne(GeoLocation.MAPPER::map);
    }

    public Observable<List<Note>> getNotes(String photoId) {
        return mDb.createQuery(Note.TABLE_NAME, Note.SELECT_BY_PHOTO_ID, photoId)
                .mapToList(Note.MAPPER::map);
    }

    public Observable<List<Property>> getProperties(String photoId) {
        return mDb.createQuery(Property.TABLE_NAME, Property.SELECT_BY_PHOTO_ID, photoId)
                .mapToList(Property.MAPPER::map);
    }

    public Observable<Void> savePhoto(@NonNull Photo photo) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mDb.newTransaction();
            try {
                insertPhoto(photo);
                transaction.markSuccessful();
            } finally {
                transaction.end();
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Void> savePhotos(@NonNull List<Photo> photos) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mDb.newTransaction();
            try {
                for (Photo photo : photos) insertPhoto(photo);
                transaction.markSuccessful();
            } finally {
                transaction.end();
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Void> deletePhoto(String photoId) {
        return Observable.create(subscriber -> {
            mDb.delete(Photo.TABLE_NAME, "photo_id = ?", photoId);
            subscriber.onNext(null);
            subscriber.onCompleted();
        });
    }

    public Observable<Void> deletePhotos(String taskId) {
        return Observable.create(subscriber -> {
            mDb.delete(Photo.TABLE_NAME, "task_id = ?", taskId);
            subscriber.onNext(null);
            subscriber.onCompleted();
        });
    }

    public Observable<Void> updatePhotoStatus(String photoId, boolean isSend) {
        return Observable.create(subscriber -> {
            ContentValues data = new ContentValues();
            data.put(Photo.IS_SENT, isSend ? String.valueOf(1) : String.valueOf(0));
            mDb.update(Photo.TABLE_NAME, data, "photo_id = ?", photoId);
            subscriber.onNext(null);
            subscriber.onCompleted();
        });
    }

    private void insertPhoto(@NonNull Photo photo) {
        String photoId = photo.photo_id();
        mDb.insert(Photo.TABLE_NAME, new Photo.Marshal(photo).asContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
        GeoLocation geoLocation = photo.getLocation();
        geoLocation.setPhotoId(photoId);
        mDb.insert(GeoLocation.TABLE_NAME, new GeoLocation.Marshal(geoLocation).asContentValues());
        List<Note> notes = photo.getNotes();
        if (notes != null && !notes.isEmpty()) {
            for (Note note : notes) {
                note.setPhotoId(photoId);
                mDb.insert(Note.TABLE_NAME, new Note.Marshal(note).asContentValues());
            }
        }
        List<Property> properties = photo.getProperties();
        if (properties != null && !properties.isEmpty()) {
            for (Property property : properties) {
                property.setPhotoId(photoId);
                mDb.insert(Property.TABLE_NAME,
                        new Property.Marshal(property).asContentValues());
            }
        }
    }

    @NonNull
    private Action1<List<Photo>> fullFillPhotos(Subscriber<? super List<Photo>> subscriber) {
        return photos -> {
            for (Photo photo : photos) {
                getGeoLocation(photo.photo_id()).subscribe(photo::setLocation);
                getNotes(photo.photo_id()).subscribe(photo::setNotes);
                getProperties(photo.photo_id()).subscribe(photo::setProperties);
            }
            subscriber.onNext(photos);
            subscriber.onCompleted();
        };
    }
}
