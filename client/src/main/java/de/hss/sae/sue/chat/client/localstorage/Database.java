package de.hss.sae.sue.chat.client.localstorage;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import de.hss.sae.sue.chat.common.communication.Message;

public class Database extends SQLiteOpenHelper {
    /*
        References:
        * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
        * http://lomza.totem-soft.com/tutorial-add-sqlcipher-to-your-android-app/
     */

    // Init constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HSSChatClientDB";
    private static final String TABLE_MESSAGES = "messages";

    // Table Column names
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "timestamp";

    // SettingsEditor
    private SettingsEditor settings;
    private boolean init = false;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if (!init) {
            settings = new SettingsEditor(context);
            init = true;
        }
    }

    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE " + TABLE_MESSAGES
                + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_SENDER + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_TIMESTAMP + " TEXT" + ")";
        db.execSQL(SQL);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        // Create tables again
        onCreate(db);
    }

    /*
        CRUD Operations - Create / Read / Update / Delete
    */

    public void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase(loadCypherKey());

        ContentValues values = new ContentValues();
        values.put(KEY_SENDER, message.getSender());
        values.put(KEY_MESSAGE, message.getMessage());
        values.put(KEY_TIMESTAMP, message.getTimestamp());

        // INSERT VALUE AND CLOSE CONNECTION
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public Message getMessage(int id) {
        Message message = null;
        SQLiteDatabase db = this.getReadableDatabase(loadCypherKey());

        Cursor cursor = db.query(TABLE_MESSAGES, new String[]{KEY_ID,
                        KEY_SENDER, KEY_MESSAGE, KEY_TIMESTAMP}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            message = getMessage(cursor);
        }

        db.close();
        cursor.close();
        return message;
    }

    public Message getLastMessage() {
        Message message = null;
        SQLiteDatabase db = this.getWritableDatabase(loadCypherKey());
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES +
                " ORDER BY " + KEY_ID + " DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            message = getMessage(cursor);
        }

        return message;
    }

    public void deleteAllMessages() {
        SQLiteDatabase db = this.getWritableDatabase(loadCypherKey());
        db.delete(TABLE_MESSAGES, null, null);
        db.close();
    }

    public List<de.hss.sae.sue.chat.common.communication.Message> getAllMessages() {   // return all Message Objects
        List<Message> MessageList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase(loadCypherKey());
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES, null);

        // get all Message Objects
        if (cursor.moveToFirst()) {
            do {
                MessageList.add(getMessage(cursor));
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return MessageList;
    }

    private Message getMessage(Cursor cursor){
        return new Message(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
        );
    }

    /*
        generating, reading and interpreting the CYPHER KEY
     */

    private String loadCypherKey() {   // try to load the key from the SettingsEditor, creates the key on the very first call
        String key;
        String pattern;
        String resolved;
        int currentInt;

        // check if there is data in the SharedPreference, if not generate and load it
        if (settings.getString(SettingsEditor.ID_Key1) == null) {
            createCypherKey();
        }

        pattern = settings.getString(SettingsEditor.ID_Key1);
        key = settings.getString(SettingsEditor.ID_Key2);

        // read the key according to the pattern; passphrase length is set to 16
        resolved = "";

        for (int i = 0; i < 16; i++) {
            // get the char on position 'i' and convert hex to int
            currentInt = Integer.parseInt(Character.toString(pattern.charAt(i * 3)), 16);
            resolved = resolved + Character.toString(key.charAt(currentInt * 2));
        }

        return resolved;
    }

    private void createCypherKey() {   // generate one random Hex and one random normal base64 String
        settings.setString(SettingsEditor.ID_Key1, generateRandomHexString());
        settings.setString(SettingsEditor.ID_Key2, generateRandomString());
    }

    // the code below is for my random String/HexString Generators

    private final Random random = new Random();
    private final char[] string = new char[64]; // all my generated Strings are 32chars long

    // initialize my charArrays containing all possible characters
    private static final char[] base64;
    private static final char[] hex;

    // fill the charArrays accordingly
    static {
        StringBuilder tmp = new StringBuilder();

        // setup the base64 table
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'A'; ch <= 'Z'; ++ch)
            tmp.append(ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        tmp.append('/');
        tmp.append('+');
        base64 = tmp.toString().toCharArray();

        tmp.setLength(0); // reset

        // setup the hex table - excluding zero
        for (char ch = '1'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'A'; ch <= 'F'; ++ch)
            tmp.append(ch);
        hex = tmp.toString().toCharArray();
    }

    // generate Strings based on the characters in my charArrays
    private String generateRandomString() {
        for (int i = 0; i < string.length; ++i)
            string[i] = base64[random.nextInt(base64.length)];
        return new String(string);
    }

    private String generateRandomHexString() {
        for (int i = 0; i < string.length; ++i)
            string[i] = hex[random.nextInt(hex.length)];
        return new String(string);
    }
}
