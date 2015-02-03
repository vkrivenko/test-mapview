package example.vlad.mailmapview;

import example.vlad.mailmapview.cache.Tile;


public class TileManager {

    public static final int TILES_COUNT = 50;

    public static final int TILE_SIZE = 256;

    private static final int START_X = 33150;
    private static final int START_Y = 22500;

    private static final int LEFT_BOUND = (START_X - TILES_COUNT) * TILE_SIZE;
    private static final int TOP_BOUND = (START_Y - TILES_COUNT) * TILE_SIZE;
    private static final int RIGHT_BOUND = (START_X + TILES_COUNT) * TILE_SIZE;
    private static final int BOTTOM_BOUND = (START_Y + TILES_COUNT) * TILE_SIZE;

    private int mGlobalX;
    private int mGlobalY;

    private Tile mStartTile;

    private int mTileOffsetX;
    private int mTileOffsetY;

    public TileManager() {
        setStartInfo(new Tile(START_X, START_Y), 0, 0);
    }

    public Tile getStartTile() {
        return mStartTile;
    }

    public int getTileOffsetX() {
        return mTileOffsetX;
    }

    public int getTileOffsetY() {
        return mTileOffsetY;
    }

    public void setStartInfo(Tile tile, int offsetX, int offsetY) {
        mStartTile = tile;
        mTileOffsetX = offsetX;
        mTileOffsetY = offsetY;

        mGlobalX = mStartTile.getX() * TILE_SIZE + offsetX;
        mGlobalY = mStartTile.getY() * TILE_SIZE + offsetY;
    }

    public void translate(int dX, int dY) {
        mGlobalX += dX;
        mGlobalY += dY;
        calcBounds();

        mTileOffsetX = mGlobalX % TILE_SIZE;
        mTileOffsetY = mGlobalY % TILE_SIZE;

        mStartTile.setX(mGlobalX / TILE_SIZE);
        mStartTile.setY(mGlobalY / TILE_SIZE);
    }

    private void calcBounds() {
        if (mGlobalX < LEFT_BOUND) {
            mGlobalX = LEFT_BOUND;
        }
        if (mGlobalX > RIGHT_BOUND) {
            mGlobalX = RIGHT_BOUND;
        }
        if (mGlobalY < TOP_BOUND) {
            mGlobalY = TOP_BOUND;
        }
        if (mGlobalY > BOTTOM_BOUND) {
            mGlobalY = BOTTOM_BOUND;
        }
    }
}
