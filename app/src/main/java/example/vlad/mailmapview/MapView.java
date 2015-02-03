package example.vlad.mailmapview;

import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import example.vlad.mailmapview.cache.Cache;
import example.vlad.mailmapview.cache.Tile;


public class MapView extends ViewGroup {

    private static final int CACHE_SIZE = 100;

    private final GestureDetector mGestureDetector;

    private List<ImageView> mDetachedViews;

    private Cache mCache;

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mTileManager.translate((int) distanceX, (int) distanceY);
            invalidate();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    private TileManager mTileManager;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addView(new ImageView(context));
        mTileManager = new TileManager();
        mCache = Cache.getInstance((Application) context.getApplicationContext(), CACHE_SIZE);
        mGestureDetector = new GestureDetector(context, mGestureListener);
        mDetachedViews = new ArrayList<ImageView>();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState state = new SavedState(superState);
        state.mCurrentPosition = new Point(mTileManager.getStartTile().getX(), mTileManager.getStartTile().getY());
        state.mCurrentOffset = new Point(mTileManager.getTileOffsetX(), mTileManager.getTileOffsetY());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        Tile tile = new Tile(myState.mCurrentPosition.x, myState.mCurrentPosition.y);
        mTileManager.setStartInfo(tile, myState.mCurrentOffset.x, myState.mCurrentOffset.y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int index = 0;
        int viewY = -mTileManager.getTileOffsetY();
        for (int y = mTileManager.getStartTile().getY() ; viewY < getHeight(); y++) {
            int viewX = -mTileManager.getTileOffsetX();

            for (int x = mTileManager.getStartTile().getX(); viewX < getWidth(); x++) {

                ImageView imageView = obtainImageView(index);
                imageView.setImageBitmap(null);
                imageView.layout(viewX, viewY, viewX + TileManager.TILE_SIZE, viewY + TileManager.TILE_SIZE);

                Tile tile = new Tile(x, y);
                mCache.load(imageView, tile);

                index++;
                viewX += TileManager.TILE_SIZE;
            }
            viewY += TileManager.TILE_SIZE;
        }
        detachAndRemoveViews(index);
        super.dispatchDraw(canvas);
    }

    private ImageView obtainImageView(int index) {
        ImageView imageView;
        if (index < getChildCount()) {
            imageView = (ImageView) getChildAt(index);
        } else if (!mDetachedViews.isEmpty()) {
            imageView = mDetachedViews.remove(mDetachedViews.size() - 1);
            addView(imageView);
        } else {
            imageView = new ImageView(getContext());
            addView(imageView);
        }
        return imageView;
    }

    private void detachAndRemoveViews(int index) {
        for (int i = index; i < getChildCount(); i++) {
            mDetachedViews.add((ImageView) getChildAt(i));
            removeViewAt(i);
        }
    }


    private static class SavedState extends BaseSavedState {

        private Point mCurrentPosition;
        private Point mCurrentOffset;

        public SavedState(Parcel source) {
            super(source);
            mCurrentPosition = source.readParcelable(Point.class.getClassLoader());
            mCurrentOffset = source.readParcelable(Point.class.getClassLoader());
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(mCurrentPosition, flags);
            dest.writeParcelable(mCurrentOffset, flags);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
