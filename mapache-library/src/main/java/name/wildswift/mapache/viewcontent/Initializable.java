package name.wildswift.mapache.viewcontent;

import androidx.annotation.NonNull;

public interface Initializable<T> {
    void init(@NonNull T context);
    void close();
}
