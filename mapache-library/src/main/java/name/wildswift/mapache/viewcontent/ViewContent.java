package name.wildswift.mapache.viewcontent;

import android.view.View;

import androidx.annotation.NonNull;

public interface ViewContent<T extends View> {
    void fillCurrentData(@NonNull T view);
    @NonNull
    Runnable subscribeForUpdates(@NonNull T view);
}
