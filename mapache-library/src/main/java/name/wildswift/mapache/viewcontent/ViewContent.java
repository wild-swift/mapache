package name.wildswift.mapache.viewcontent;

import android.view.View;

public interface ViewContent<T extends View> {
    void fillCurrentData(T view);
    Runnable subscribeForUpdates(T view);
}
