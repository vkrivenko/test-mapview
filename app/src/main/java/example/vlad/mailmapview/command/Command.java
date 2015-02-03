package example.vlad.mailmapview.command;


public interface Command<T, V> {

    public V execute();

    public T getParams();
}
