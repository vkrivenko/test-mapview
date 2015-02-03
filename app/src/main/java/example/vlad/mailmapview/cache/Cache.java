package example.vlad.mailmapview.cache;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


public class Cache {

    private static Cache mInstance;

    public static Cache getInstance(Application context, int cacheSize) {
        if (mInstance == null) {
            mInstance = new Cache(context, cacheSize);
        }
        return mInstance;
    }

    private Context mContext;

    private LruCache<String, Tile> mMemoryCache;

    private Cache(Context context, int cacheSize) {
        mContext = context;
        mMemoryCache = new LruCache<String, Tile>(cacheSize);
    }

    public void load(ImageView imageView, Tile tile) {
        Tile cachedTile = mMemoryCache.get(tile.getTag());
        if (cachedTile == null) {
            tile.setDrawable(null);
            mMemoryCache.put(tile.getTag(), tile);
            executeAsyncTask(new LoadingAsyncTask(mContext, imageView, tile, mMemoryCache));

        } else {
            Drawable image = cachedTile.getDrawable();
            if (image != null) {
                imageView.setImageDrawable(image);
            }
        }
    }

    static public void executeAsyncTask(LoadingAsyncTask task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(CacheUtil.EXECUTOR);
        } else {
            task.execute();
        }
    }
}
