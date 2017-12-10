package de.hss.sae.sue.chat.client.localstorage;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsEditor {
    public static final String ID_IP = "IP";
    public static final String ID_Port = "Port";
    public static final String ID_Nickname = "Name";
    public static final String ID_Key1 = "Var0";
    public static final String ID_Key2 = "Var1";

    private static final String ID_PREFERENCES = "Settings";

    private SharedPreferences preferences;      // the actual object connected to this file
    private SharedPreferences.Editor editor;    // the editor which is used to save data

    // Constructor to create a connection to one SettingsEditor file
    public SettingsEditor(Context context) {
        this.preferences = context.getSharedPreferences(ID_PREFERENCES, 0);
        this.editor = preferences.edit();
    }

    // Setter methods, 'id' is to identify the variable.
    // I'm using apply() and not commit() so that it'll also work for background threads.
    public void setString(String id, String value) {
        editor.putString(id, value);
        editor.apply();
    }

    public void setInt(String id, int value) {
        editor.putInt(id, value);
        editor.apply();
    }

    // Getter methods, second parameter is the default value to return if variable was not found
    public String getString(String id) {
        return this.preferences.getString(id, null);
    }
    public int getInt(String id) {
        return this.preferences.getInt(id, 0);
    }
}
