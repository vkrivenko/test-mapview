package example.vlad.mailmapview.command;


import example.vlad.mailmapview.R;

public abstract class Result<T> {

    private T mData;

    public Result() {
        this(null);
    }

    public Result(T data) {
        mData = data;
    }

    public T getData() {
        return mData;
    }


    public static class Ok<T> extends Result<T> {

        public Ok(T data) {
            super(data);
        }
    }

    public static class Error<T> extends Result<T> {

        private int mErrorRes;

        public Error(int errorRes) {
            mErrorRes = errorRes;
        }

        public int getErrorRes() {
            return mErrorRes;
        }
    }

    public static class NetworkError<T> extends Error<T> {

        public NetworkError() {
            super(R.string.network_error);
        }
    }

    public static class FileError<T> extends Error<T> {

        public FileError() {
            super(R.string.file_error);
        }
    }
}
