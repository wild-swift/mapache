package name.wildswift.mapache.viewsets;

import android.view.View;

@SuppressWarnings("WeakerAccess")
public class ViewVoid extends ViewSet {
    public ViewVoid() {
    }

    public <V1 extends View> ViewSingle<V1> union(V1 v1) {
        return new ViewSingle<>(v1);
    }

}
