package name.wildswift.mapache.viewcontent;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ViewContentHolder {
    @Nullable
    <VS extends ViewContent<?>> VS getByView(@NonNull Class<VS> clazz);

    @Nullable
    <VS extends ViewContent<?>> VS getData(@NonNull Class<VS> clazz, String name);
}
