package example.vlad.mailmapview.command;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

import example.vlad.mailmapview.cache.Tile;


public class GetTileCommand extends BaseCommand<Tile, Tile> {

    private final Context mContext;

    public GetTileCommand(Context context, Tile tile) {
        super(tile);
        mContext = context;
    }

    @Override
    public Result<Tile> execute() {
        Tile tile = getParams();
        Uri uri = tile.getContentUriCoords();

        Result<Tile> result = checkAndLoadTile(uri, tile);
        if (result instanceof Result.Error<?>) {
            return result;
        }

        InputStream is = null;
        try {
            is = mContext.getContentResolver().openInputStream(uri);
            tile.setDrawable(getDrawable(is));
            return new Result.Ok<Tile>(tile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            close(is);
        }
        return new Result.FileError<Tile>();
    }

    private Drawable getDrawable(InputStream is) {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return new BitmapDrawable(mContext.getResources(), bitmap);
    }

    private Result<Tile> checkAndLoadTile(Uri uri, Tile tile) {
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (!cursor.moveToFirst()) {
                return new LoadTileCommand(mContext, tile).execute();
            }
        } finally {
            close(cursor);
        }
        return new Result.Ok<Tile>(null);
    }
}
