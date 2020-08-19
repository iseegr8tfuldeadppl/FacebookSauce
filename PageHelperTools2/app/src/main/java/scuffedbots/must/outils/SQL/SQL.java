package scuffedbots.must.outils.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQL extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bigmaster.db";
    private static final String MESSAGES_TABLE = "MESSAGES_TABLE";
    private static final String LOG_TABLE = "LOG_TABLE";
    private static final String CLIENTS_TABLE = "CLIENTS_TABLE";
    private static final String COL_1 = "_ID";
    private static final String COL_2_MESSAGE = "MESSAGE";
    private static final String COL_2_CLIENT_ID = "CLIENT_ID";
    private static final String COL_3_MESSAGE_ID = "ESSAGE_ID";
    private static final String COL_3_CLIENTS_WHO_RECEIVED_THIS_MSG = "CLIENTS_WHO_RECEIVED_THIS_MSG";
    private static final String COL_3_CLIENT_NAME = "CLIENT_NAME";
    private static final String COL_4_AMOUNT = "AMOUNT";
    private static final String COL_4_CLIENT_CONVERSATION_ID = "CLIENT_CONVERSATION_ID";
    private static final String COL_5_PAGE_ID = "PAGE_ID";
    private static SQL mInstance = null;

    public SQL(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + MESSAGES_TABLE + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2_MESSAGE + " TEXT, " + COL_3_CLIENTS_WHO_RECEIVED_THIS_MSG + " TEXT, " + COL_4_AMOUNT + " TEXT, " + COL_5_PAGE_ID + " TEXT" + ");");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + LOG_TABLE + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2_CLIENT_ID + " TEXT, " + COL_3_MESSAGE_ID + " TEXT"  + ");");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + CLIENTS_TABLE + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2_CLIENT_ID + " TEXT, " + COL_3_CLIENT_NAME + " TEXT, " + COL_4_CLIENT_CONVERSATION_ID + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CLIENTS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public static SQL getInstance(Context ctx) {
        if (mInstance == null) mInstance = new SQL(ctx.getApplicationContext());
        return mInstance;
    }

    public boolean insertLog(String CLIENT_ID, String CLIENT_NAME){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_CLIENT_ID, CLIENT_ID);
        contentValues.put(COL_3_CLIENT_NAME, CLIENT_NAME);
        return sqLiteDatabase.insert(LOG_TABLE, null, contentValues) != -1;
    }

    public Cursor getLogs() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + LOG_TABLE + ";", null);
    }

    public boolean insertMessage(String MESSAGE, String CLIENTS_WHO_RECEIVED_THIS_MSG, String AMOUNT, String PAGE_ID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_MESSAGE, MESSAGE);
        contentValues.put(COL_3_CLIENTS_WHO_RECEIVED_THIS_MSG, CLIENTS_WHO_RECEIVED_THIS_MSG);
        contentValues.put(COL_4_AMOUNT, AMOUNT);
        contentValues.put(COL_5_PAGE_ID, PAGE_ID);
        return sqLiteDatabase.insert(MESSAGES_TABLE, null, contentValues) != -1;
    }

    public boolean updateMessage(String _ID, String MESSAGE, String CLIENTS_WHO_RECEIVED_THIS_MSG, String AMOUNT, String PAGE_ID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_MESSAGE, MESSAGE);
        contentValues.put(COL_3_CLIENTS_WHO_RECEIVED_THIS_MSG, CLIENTS_WHO_RECEIVED_THIS_MSG);
        contentValues.put(COL_4_AMOUNT, AMOUNT);
        contentValues.put(COL_5_PAGE_ID, PAGE_ID);
        sqLiteDatabase.update(MESSAGES_TABLE, contentValues, COL_1 + "=?", new String[] { _ID });
        return true;
    }

    public Cursor getMessages() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + MESSAGES_TABLE + ";", null);
    }

    public boolean insertClient(String CLIENT_ID, String CLIENT_NAME, String CLIENT_CONVERSATION_ID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_CLIENT_ID, CLIENT_ID);
        contentValues.put(COL_3_CLIENT_NAME, CLIENT_NAME);
        contentValues.put(COL_4_CLIENT_CONVERSATION_ID, CLIENT_CONVERSATION_ID);
        return sqLiteDatabase.insert(CLIENTS_TABLE, null, contentValues) != -1;
    }

    public boolean updateClient(String _ID, String CLIENT_ID, String CLIENT_NAME, String CLIENT_CONVERSATION_ID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_CLIENT_ID, CLIENT_ID);
        contentValues.put(COL_3_CLIENT_NAME, CLIENT_NAME);
        contentValues.put(COL_4_CLIENT_CONVERSATION_ID, CLIENT_CONVERSATION_ID);
        sqLiteDatabase.update(CLIENTS_TABLE, contentValues, COL_1 + "=?", new String[] { _ID });
        return true;
    }

    public Cursor getClients() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + CLIENTS_TABLE + ";", null);
    }


}
