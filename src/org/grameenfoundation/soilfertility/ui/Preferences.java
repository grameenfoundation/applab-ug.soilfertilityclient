package org.grameenfoundation.soilfertility.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import org.grameenfoundation.soilfertility.R;

/**
 * Copyright (c) 2014 AppLab, Grameen Foundation
 * Created by: David
 */
public class Preferences extends PreferenceActivity {

    private String PREFERENCES_FILE = "preferences.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);

        PreferenceManager.setDefaultValues(this, R.layout.preferences, true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
