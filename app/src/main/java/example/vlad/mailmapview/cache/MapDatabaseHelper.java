package example.vlad.mailmapview.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MapDatabaseHelper extends SQLiteOpenHelper {

    private static int VERSION = 1;
    private static final String NAME = "images_cache.db";

    public MapDatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    private static final String CREATE_IMAGE_TABLE = "CREATE TABLE " + Tile.TABLE_NAME + " (" +
            Tile.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Tile.COL_X + " INTEGER," +
            Tile.COL_Y + " INTEGER," +
            Tile.COL_DATA + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tile.TABLE_NAME);
        onCreate(db);
    }
}
