package com.sam_chordas.android.stockhawk;

import com.facebook.stetho.Stetho;

/**
 * User: huma
 * Date: 30-Aug-16
 */
public class Application extends android.app.Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
