package name.wildswift.mapache.viewcontent;

import android.view.View;

import org.jetbrains.annotations.NotNull;


public interface ViewContent<T extends View> {
    void fillCurrentData(@NotNull T view);
    @NotNull
    Runnable subscribeForUpdates(@NotNull T view);
}
