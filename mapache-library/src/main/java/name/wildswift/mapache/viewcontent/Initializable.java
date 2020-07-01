package name.wildswift.mapache.viewcontent;

public interface Initializable<T> {
    void init(T context);
    void close();
}
