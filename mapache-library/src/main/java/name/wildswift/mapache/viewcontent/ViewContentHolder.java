package name.wildswift.mapache.viewcontent;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ViewContentHolder {
    @Nullable
    <V extends View, VS extends ViewContent<V>> VS getByView(@NonNull Class<V> clazz);

    @Nullable
    <VS extends ViewContent<?>> VS getByClass(@NonNull Class<VS> clazz);

    @Nullable
    <VS extends ViewContent<?>> VS getByName(@NonNull Class<VS> clazz, String name);
}
