package example.vlad.mailmapview.cache;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import example.vlad.mailmapview.command.GetTileCommand;


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

    static class LoadingAsyncTask extends AsyncTask<Tile, Void, Tile> {

        private Context mContext;
        private LruCache<String, Tile> mCache;
        private WeakReference<ImageView> mImageRef;
        private Tile mTile;

        LoadingAsyncTask(Context context, ImageView imageView, Tile tile, LruCache<String, Tile> cache) {
            mContext = context;
            mImageRef = new WeakReference<ImageView>(imageView);
            mTile = tile;
            mCache = cache;
            Log.d("qwerty", "LoadingAsyncTask");
        }

        @Override
        protected void onPreExecute() {
            ImageView imageView = mImageRef.get();
            if (imageView != null) {
                imageView.setTag(mTile.getTag());
            }
        }

        @Override
        protected Tile doInBackground(Tile... params) {
            return new GetTileCommand(mContext, mTile).execute();
        }

        @Override
        protected void onPostExecute(Tile result) {
            if (result == null) {
                mCache.remove(mTile.getTag());
            } else {
                Log.d("qwerty", "LoadingAsyncTask onPostExecute");
                mCache.put(result.getTag(), result);

                ImageView imageView = mImageRef.get();
                if (imageView != null && result.getTag().equals(imageView.getTag())) {
                    Log.d("qwerty", "LoadingAsyncTask onPostExecute setImageDrawable");
                    imageView.setImageDrawable(result.getDrawable());
                }
            }
        }
    }


}
