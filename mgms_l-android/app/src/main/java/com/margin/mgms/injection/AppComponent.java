package com.margin.mgms.injection;

import android.content.Context;

import com.bumptech.glide.RequestManager;
import com.margin.mgms.database.DatabaseConnector;
import com.margin.mgms.model.APIError;
import com.margin.mgms.rest.StrongLoopApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created on March 31, 2016.
 *
 * @author Marta.Ginosyan
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    /**
     * @return Application context.
     */
    Context getAppContext();

    /**
     * @return {@link RequestManager} for loading images with {@link com.bumptech.glide.Glide Glide}.
     */
    RequestManager getGlide();

    /**
     * @return Singleton {@link StrongLoopApi} instance where {@link retrofit2.Retrofit} is
     * constructed with {@link retrofit2.CallAdapter CallAdapter}.
     */
    @Named(AppModule.RETROFIT_ADAPTER_ORDINARY)
    StrongLoopApi getOrdinaryStrongLoopApi();

    /**
     * @return Singleton {@link StrongLoopApi} instance where {@link retrofit2.Retrofit} is
     * constructed with {@link retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
     * RxJavaCallAdapterFactory}.
     */
    @Named(AppModule.RETROFIT_ADAPTER_REACTIVE)
    StrongLoopApi getReactiveStrongLoopApi();

    /**
     * @return Singleton {@link StrongLoopApi} mError response converter.
     */
    Converter<ResponseBody, APIError> getErrorConverter();

    /**
     * Injects {@link DatabaseConnector} to use the {@link com.squareup.sqlbrite.BriteDatabase}
     */
    void inject(DatabaseConnector databaseConnector);
}
