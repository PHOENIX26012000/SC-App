package de.ifgi.sc.smartcitiesapp.main;

import android.app.Application;

/**
 * Base class for maintaining global application state.
 * The Application class is instantiated before any other class
 * when the process for your application/package is created.
 */
public class App extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
    }
}
