package name.wildswift.mapache.viewsets;

import android.view.View;

@SuppressWarnings("WeakerAccess")
public class ViewSingle<V1 extends View> extends ViewSet {
    private final V1 view1;

    public ViewSingle(V1 view1) {
        this.view1 = view1;
    }

    public V1 getView1() {
        return view1;
    }

    // Kotlin compatibility
    public V1 component1() {
        return view1;
    }

    public <V2 extends View> ViewCouple<V1, V2> union(V2 v2) {
        return new ViewCouple<>(view1, v2);
    }

}
