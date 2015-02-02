package example.vlad.mailmapview.cache;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.util.List;


public class MapContentProvider extends ContentProvider {

    public static final String AUTHORITY = "example.vlad.mailmapview.cache";

    private static final int ALL = 1;
    private static final int COORDINATES = 2;

    private static final UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(0);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
            //	Starting from API level JELLY_BEAN_MR2, this method will accept leading slash in the path.
            mUriMatcher.addURI(AUTHORITY, ("/" + Tile.CONTENT_PATH + "/*/*"), COORDINATES);
            mUriMatcher.addURI(AUTHORITY, ("/" + Tile.CONTENT_PATH), ALL);
        } else {
            mUriMatcher.addURI(AUTHORITY, (Tile.CONTENT_PATH + "/*/*"), COORDINATES);
            mUriMatcher.addURI(AUTHORITY, (Tile.CONTENT_PATH), ALL);
        }
    }

    private MapDatabaseHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new MapDatabaseHelper(getContext());
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (mUriMatcher.match(uri) != COORDINATES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return openFileHelper(uri, mode);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Tile.TABLE_NAME);

        switch (mUriMatcher.match(uri)) {
            case ALL:
                break;
            case COORDINATES:
                qb.appendWhere(getCoordinatesQuery(uri));
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case ALL:
                return Tile.CONTENT_TYPE;
            case COORDINATES:
                return Tile.CONTENT_COORDS_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mUriMatcher.match(uri) != ALL) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long rowId = db.insert(Tile.TABLE_NAME, null, values);

        Uri result = null;
        if (rowId > 0) {
            result = Tile.getContentUriCoords(values.getAsInteger(Tile.COL_X), values.getAsInteger(Tile.COL_Y));
            getContext().getContentResolver().notifyChange(result, null);
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;
        switch (mUriMatcher.match(uri)) {
            case ALL:
                count = db.delete(Tile.TABLE_NAME, selection, selectionArgs);
                break;
            case COORDINATES:
                String where = getCoordinatesQuery(uri) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                count = db.delete(Tile.TABLE_NAME, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;

        switch (mUriMatcher.match(uri)) {
            case ALL:
                count = db.update(Tile.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COORDINATES:
                String where = getCoordinatesQuery(uri) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                count = db.update(Tile.TABLE_NAME, values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private String getCoordinatesQuery(Uri uri) {
        return Tile.COL_X + "=" + getX(uri) + " AND " + Tile.COL_Y + "=" + getY(uri);
    }

    private String getX(Uri uri) {
        return getPathSegment(uri, 1);
    }

    private String getY(Uri uri) {
        return getPathSegment(uri, 2);
    }

    private String getPathSegment(Uri uri, int index) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() < index + 1) {
            throw new IllegalArgumentException("Invalid uri" + uri);
        }
        return pathSegments.get(index);
    }
}
