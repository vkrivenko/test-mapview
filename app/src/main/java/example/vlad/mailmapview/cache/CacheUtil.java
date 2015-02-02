package example.vlad.mailmapview.cache;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CacheUtil {

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static class FIFOBlockingDeque<T> extends LinkedBlockingDeque<T> {

        @Override
        public boolean offer(T t) {
            return super.offerFirst(t);
        }

        @Override
        public T remove() {
            return super.removeFirst();
        }
    }

    static final Executor EXECUTOR = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
            new FIFOBlockingDeque<Runnable>(), sThreadFactory);


    public static String getCachePath(Context context) {
        String cachePath = isExternalCacheAvailable(context) ? getExternalCachePath(context) : getInternalCachePath(context);
        return cachePath;
    }

    private static String getInternalCachePath(Context context) {
        return context.getCacheDir().getPath();
    }

    private static String getExternalCachePath(Context context) {
        return getExternalCacheDir(context).getPath();
    }

    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    private static boolean isExternalCacheAvailable(Context context) {
        return (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
                && !Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED)
                && getExternalCacheDir(context) != null;
    }
}
