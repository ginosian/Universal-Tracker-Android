package com.margin.mgms;

import com.margin.components.BaseApplication;
import com.margin.mgms.injection.AppComponent;
import com.margin.mgms.injection.AppModule;
import com.margin.mgms.injection.DaggerAppComponent;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class LipstickApplication extends BaseApplication {

    private static AppComponent sAppComponent;

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }
}
