package example.vlad.mailmapview.command;


import java.io.Closeable;
import java.io.IOException;

public abstract class Command<T, V> {

    private T mParams;

    public Command(T params) {
        mParams = params;
    }

    public T getParams() {
        return mParams;
    }

    public abstract V execute();

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
