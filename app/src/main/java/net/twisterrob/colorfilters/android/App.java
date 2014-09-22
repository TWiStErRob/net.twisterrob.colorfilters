package net.twisterrob.colorfilters.android;

import android.app.Application;
import android.preference.PreferenceManager;

import net.twisterrob.colorfilters.android.R;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}
