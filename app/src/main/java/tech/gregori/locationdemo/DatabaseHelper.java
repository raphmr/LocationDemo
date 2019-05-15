package tech.gregori.locationdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper {
    private static final String TAG = "DAL";

    private SQLiteDatabase db;
    private CreateDatabase database;

    public DatabaseHelper(Context context) {
        database = new CreateDatabase(context);
    }

    public boolean insert(String nome, String email, double latitude, double longitude) {
        ContentValues values;
        long result;

        db = database.getWritableDatabase();
        values = new ContentValues();
        values.put(CreateDatabase.NOME, nome);
        values.put(CreateDatabase.EMAIL, email);
        values.put(CreateDatabase.LATITUDE, latitude);
        values.put(CreateDatabase.LONGITUDE, longitude);

        result = db.insert(CreateDatabase.TABLE, null, values);
        db.close();


        if (result == -1) {
            Log.e(TAG, "insert: Erro inserindo registro");
            return false;
        }

        return true;
    }

    public boolean update(String nome, String email, double latitude, double longitude) {
        ContentValues values;
        long result;

        db = database.getWritableDatabase();
        values = new ContentValues();
        values.put(CreateDatabase.EMAIL, email);
        values.put(CreateDatabase.LATITUDE, latitude);
        values.put(CreateDatabase.LONGITUDE, longitude);
        String[] whereArgs = {nome};

        result = db.update(CreateDatabase.TABLE, values, "nome = ?", whereArgs);
        db.close();


        if (result == -1) {
            Log.e(TAG, "insert: Erro atualizando registro");
            return false;
        }

        return true;
    }

    public boolean delete() {
        long result;

        db = database.getWritableDatabase();
        String[] whereArgs = {};

        result = db.delete(CreateDatabase.TABLE, "", whereArgs);
        db.close();


        if (result == -1) {
            Log.e(TAG, "insert: Erro inserindo registro");
            return false;
        }

        return true;
    }

    public Cursor loadAll() {
        Cursor cursor;
        String[] fields = {CreateDatabase.NOME, CreateDatabase.EMAIL, CreateDatabase.LONGITUDE, CreateDatabase.LATITUDE};
        db = database.getReadableDatabase();

        // SELECT _id, title FROM book
        // String sql = "SELECT _id, title FROM book";
        //cursor = db.rawQuery(sql, null);
        cursor = db.query(CreateDatabase.TABLE, fields, null,
                null, null, null,
                null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }

    public Cursor findByNome(String nome) {
        Cursor cursor;
        final String whereClause = "nome = ?";
        String[] whereArgs = {nome};
        db = database.getReadableDatabase();

        cursor = db.query(CreateDatabase.TABLE, null, whereClause,
                whereArgs, null, null,
                null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }
}
