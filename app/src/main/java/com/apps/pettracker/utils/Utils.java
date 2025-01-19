package com.apps.pettracker.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;

import java.util.Locale;

public class Utils {
    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
