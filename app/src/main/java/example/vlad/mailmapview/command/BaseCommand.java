package example.vlad.mailmapview.command;

import java.io.Closeable;
import java.io.IOException;


public abstract class BaseCommand<T, V> implements Command<T, Result<V>> {

    private T mParams;

    public BaseCommand(T params) {
        mParams = params;
    }

    @Override
    public T getParams() {
        return mParams;
    }

    protected void close(Closeable closeable) {
        if (closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
