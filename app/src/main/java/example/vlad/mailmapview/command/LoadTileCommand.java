package example.vlad.mailmapview.command;

import android.content.Context;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import example.vlad.mailmapview.cache.Tile;


public class LoadTileCommand extends HttpCommand<Tile, Void> {

    private static final String URL = "http://b.tile.opencyclemap.org/cycle/16";

    private final Context mContext;

    public LoadTileCommand(Context context, Tile params) {
        super(params);
        mContext = context;
    }

    @Override
    protected Void processResponse(InputStream input) {
        Uri imageUri = mContext.getContentResolver().insert(Tile.CONTENT_URI, getParams().getContentValues(mContext));
        OutputStream output = null;
        try {
            output = mContext.getContentResolver().openOutputStream(imageUri);
            fromInputToOutput(input, output);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(output);
        }
        return null;
    }

    private void fromInputToOutput(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    protected String prepareUrl() {
        Uri uri = Uri.parse(URL);
        Uri.Builder builder = uri.buildUpon();
        builder.appendPath(String.valueOf(getParams().getX()))
                .appendPath(String.valueOf(getParams().getY()));

        return builder.build().toString();
    }
}
