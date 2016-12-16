package com.gae.scaffolder.plugin;
import android.content.Context;
import android.content.SharedPreferences;
/**
 * @author @diyfr
 */

public class PreferencesHelper {

    public static final String PREFERENCE_NAME = "sharedPref";

    private final SharedPreferences preferences;

    private static SharedPreferences.Editor editorTransaction;

    public PreferencesHelper(Context context)
    {

        preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
    }

    public void beginTransaction()
    {
        editorTransaction = preferences.edit();
    }

    public void endTransaction()
    {
        if (editorTransaction != null)
        {
            editorTransaction.commit();
        }
        editorTransaction = null;
    }

    public void remove(String key)
    {
        if (editorTransaction == null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(key);
            editor.commit();
        } else
        {
            editorTransaction.remove(key);
        }
    }

    public Boolean getBoolean(String key, boolean defValue)
    {
        return preferences.getBoolean(key, defValue);
    }

    public void setBoolean(String key, boolean value)
    {
        if (editorTransaction == null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } else
        {
            editorTransaction.putBoolean(key, value);
        }
    }

    public String getString(String key, String defValue)
    {
        return preferences.getString(key, defValue);
    }

    public void setString(String key, String value)
    {
        if (editorTransaction == null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.commit();
        } else
        {
            editorTransaction.putString(key, value);
        }
    }

    public int getInt(String key, int defValue)
    {
        return preferences.getInt(key, defValue);
    }

    public void setInt(String key, int value)
    {
        if (editorTransaction == null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            editor.commit();
        } else
        {
            editorTransaction.putInt(key, value);
        }
    }

    public float getFloat(String key, float defValue)
    {
        return preferences.getFloat(key, defValue);
    }

    public void setFloat(String key, float value)
    {
        if (editorTransaction == null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(key, value);
            editor.commit();
        } else
        {
            editorTransaction.putFloat(key, value);
        }
    }

    public long getLong(String key, long defValue)
    {
        return preferences.getLong(key, defValue);
    }

    public void setLong(String key, long value)
    {
        if (editorTransaction == null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            editor.commit();
        } else
        {
            editorTransaction.putLong(key, value);
        }
    }
}
