package name.wildswift.mapache.debouncers;

public interface DebounceCallback<T> {
    void newValue(T value);
}
