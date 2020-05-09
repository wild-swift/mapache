package name.wildswift.mapache.debouncers;

import java.util.concurrent.Callable;

public interface Debouncer<T> {
    void onNewValue(T value);
    void onNewValue(Callable<T> value);
    void addCallback(DebounceCallback<T> callback);
}
