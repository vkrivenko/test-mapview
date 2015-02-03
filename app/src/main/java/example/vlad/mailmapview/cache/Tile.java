package example.vlad.mailmapview.cache;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;


public class Tile {

    public static final String TABLE_NAME = "tile_table";

    public static final String CONTENT_PATH = "tile";
    public static final Uri CONTENT_URI = Uri.parse("content://" + MapContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + MapContentProvider.AUTHORITY + "."
            + TABLE_NAME;
    public static final String CONTENT_COORDS_TYPE = "vnd.android.cursor.coords/vnd." + MapContentProvider.AUTHORITY + "."
            + TABLE_NAME;

    public static final String IMAGE_EXTENSION = ".png";

    public static final String COL_ID = "_id";
    public static final String COL_X = "x";
    public static final String COL_Y = "y";
    public static final String COL_DATA = "_data";

    private int mId;
    private int mX;
    private int mY;
    private Drawable mDrawable;

    public Tile(int x, int y) {
        mX = x;
        mY = y;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public int getX() {
        return mX;
    }

    public void setX(int mX) {
        this.mX = mX;
    }

    public int getY() {
        return mY;
    }

    public void setY(int mY) {
        this.mY = mY;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
    }

    public String getTag() {
        return "x" + mX + "y" + mY;
    }

    public ContentValues getContentValues(Context context) {
        ContentValues values = new ContentValues();
        values.put(Tile.COL_X, mX);
        values.put(Tile.COL_Y, mY);
        values.put(Tile.COL_DATA, CacheUtil.getCachePath(context) + "/" + getTag() + IMAGE_EXTENSION);
        return values;
    }

    public Uri getContentUriCoords() {
        return getContentUriCoords(mX, mY);
    }

    public static Uri getContentUriCoords(int x, int y) {
        Uri.Builder builder = Tile.CONTENT_URI.buildUpon();
        builder.appendPath(String.valueOf(x))
                .appendPath(String.valueOf(y));
        return builder.build();
    }
}
