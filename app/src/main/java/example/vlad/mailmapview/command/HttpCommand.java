package example.vlad.mailmapview.command;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public abstract class HttpCommand<T, V> extends BaseCommand<T, V> {

    private static final int CONNECT_TIMEOUT = 15000;

    public HttpCommand(T params) {
        super(params);
    }

    @Override
    public Result<V> execute() {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            URL url = new URL(prepareUrl());
            connection = (HttpURLConnection) url.openConnection();
            prepareConnection(connection);
            connection.connect();

            if (connection.getResponseCode() == HttpStatus.SC_OK) {
                is = connection.getInputStream();
                return processResponse(is);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(is);
            if (connection != null){
                connection.disconnect();
            }
        }
        return new Result.NetworkError();
    }

    protected abstract Result<V> processResponse(InputStream is) throws IOException;

    protected abstract String prepareUrl();

    protected void prepareConnection(HttpURLConnection connection) {
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(CONNECT_TIMEOUT);
        connection.setDoInput(true);
    }
}
