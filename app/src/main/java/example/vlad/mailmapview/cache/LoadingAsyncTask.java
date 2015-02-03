package example.vlad.mailmapview.cache;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import example.vlad.mailmapview.command.GetTileCommand;
import example.vlad.mailmapview.command.Result;


public class LoadingAsyncTask extends AsyncTask<Void, Void, Result<Tile>> {

    private Context mContext;
    private LruCache<String, Tile> mCache;
    private WeakReference<ImageView> mImageRef;
    private Tile mTile;

    LoadingAsyncTask(Context context, ImageView imageView, Tile tile, LruCache<String, Tile> cache) {
        mContext = context;
        mImageRef = new WeakReference<ImageView>(imageView);
        mTile = tile;
        mCache = cache;
    }

    @Override
    protected void onPreExecute() {
        ImageView imageView = mImageRef.get();
        if (imageView != null) {
            imageView.setTag(mTile.getTag());
        }
    }

    @Override
    protected Result<Tile> doInBackground(Void... params) {
        return new GetTileCommand(mContext, mTile).execute();
    }

    @Override
    protected void onPostExecute(Result<Tile> result) {
        if (result != null) {
            if (result instanceof Result.Ok<?>) {
                handleOkResult(result.getData());
                return;
            } else {
                Toast.makeText(mContext, ((Result.Error<?>) result).getErrorRes(), Toast.LENGTH_SHORT).show();
            }
        }
        mCache.remove(mTile.getTag());
    }

    private void handleOkResult(Tile result) {
        mCache.put(result.getTag(), result);

        ImageView imageView = mImageRef.get();
        if (imageView != null && result.getTag().equals(imageView.getTag())) {
            imageView.setImageDrawable(result.getDrawable());
        }
    }
}
