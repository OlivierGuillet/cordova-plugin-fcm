package com.gae.scaffolder.plugin;

import android.content.Context;

/**
 * @author Stéphane LE GALL SNCF Voyages SI - 2016.
 */

public class NotificationsPreferencesStorage {

    public final static String NOTIFICATIONS_COUNT = "com.gae.scaffolder.plugin.NOTIFICATIONS_COUNT";


    public static int getNotificationCount(Context context) {
        return new PreferencesHelper(context).getInt(NOTIFICATIONS_COUNT, 0);
    }

    public static void addNotification(Context context, String value) {
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        int current = preferencesHelper.getInt(NOTIFICATIONS_COUNT, 0);
        current++;
        preferencesHelper.setInt(NOTIFICATIONS_COUNT, current);
        preferencesHelper.setString(String.valueOf(current), value);
    }

    public static String getNotification(Context context, int id) {
        return new PreferencesHelper(context).getString(String.valueOf(id), null);
    }

    public static void setNotification(Context context, int id, String value) {
        new PreferencesHelper(context).setString(String.valueOf(id), value);
    }

    public static void removeNotification(Context context, int id) {
        new PreferencesHelper(context).remove(String.valueOf(id));
    }

}
