package name.wildswift.mapache.viewcontent;

import android.view.View;

public interface ViewContentHolder {
    <T extends View, VS extends ViewContent<T>> VS getData(Class<T> clazz);
    <T extends View, VS extends ViewContent<T>> VS getData(Class<T> clazz, String name);
}
