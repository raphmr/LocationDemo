package tech.gregori.locationdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CreateDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "contatos.db";
    public static final String TABLE = "contatoLocalizacao";
    public static final String NOME = "nome";
    public static final String EMAIL = "email";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    private static final int VERSION = 1;

    public CreateDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " ( "
                + "nome text primary key, " +
                "email text, " +
                "latitude real, " +
                "longitude real" +
        ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
