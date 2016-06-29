package de.ifgi.sc.smartcitiesapp.main;

import android.app.Application;

import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

/**
 * Base class for maintaining global application state.
 * The Application class is instantiated before any other class
 * when the process for your application/package is created.
 */
public class App extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        initSingletons();
    }

    /**
     * initialise all global singleton variables
     */
    protected void initSingletons(){

        UIMessageManager.initInstance();    // Singleton of UIMessageManager
        ZoneManager.initInstance(this);     // Singleton of ZoneManager
        Messenger.initInstance(this);

        // add your singleton classes below:

    }

}
