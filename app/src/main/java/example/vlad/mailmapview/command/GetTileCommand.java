package example.vlad.mailmapview.command;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import example.vlad.mailmapview.cache.Tile;


public class GetTileCommand extends Command<Tile, Tile> {

    private final Context mContext;

    public GetTileCommand(Context context, Tile tile) {
        super(tile);
        mContext = context;
    }

    @Override
    public Tile execute() {
        Tile tile = getParams();
        Uri uri = tile.getContentUriCoords();
        checkAndLoadTile(uri, tile);

        InputStream is = null;
        try {
            is = mContext.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            tile.setDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
            return tile;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            close(is);
        }
        return null;
    }

    private void checkAndLoadTile(Uri uri, Tile tile) {
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (!cursor.moveToFirst()) {
                new LoadTileCommand(mContext, tile).execute();
            }
        } finally {
            close(cursor);
        }

    }
}
