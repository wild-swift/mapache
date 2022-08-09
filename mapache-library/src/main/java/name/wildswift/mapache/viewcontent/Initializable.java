package name.wildswift.mapache.viewcontent;


import org.jetbrains.annotations.NotNull;

public interface Initializable<T> {
    void init(@NotNull T context);
    void close();
}
