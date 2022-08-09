package name.wildswift.mapache.viewcontent;

import android.view.View;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface ViewContentHolder {
    @Nullable
    <V extends View, VS extends ViewContent<V>> VS getByView(@NotNull Class<V> clazz);

    @Nullable
    <VS extends ViewContent<?>> VS getByClass(@NotNull Class<VS> clazz);

    @Nullable
    <VS extends ViewContent<?>> VS getByName(@NotNull Class<VS> clazz, String name);
}
