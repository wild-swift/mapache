package name.wildswift.mapache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewcontent.ViewContent;
import name.wildswift.mapache.viewcontent.ViewContentHolder;
import name.wildswift.mapache.viewcontent.ViewContentMetaSource;

class ViewContentHolderImpl<S extends MState> implements ViewContentHolder {

    private final ViewContentMetaSource<S> stateSource;

    public ViewContentHolderImpl(ViewContentMetaSource<S> stateSource) {
        this.stateSource = stateSource;
    }

    void onNewState(S state) {

    }

    @Nullable
    @Override
    public <VS extends ViewContent<?>> VS getByView(@NonNull Class<VS> clazz) {
        return null;
    }

    @Nullable
    @Override
    public <VS extends ViewContent<?>> VS getData(@NonNull Class<VS> clazz, String name) {
        return null;
    }
}
