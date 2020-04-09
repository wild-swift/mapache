package name.wildswift.mapache.viewsets;

import android.view.View;

@SuppressWarnings("WeakerAccess")
public class ViewCouple<V1 extends View, V2 extends View> extends ViewSet {
    private final V1 view1;
    private final V2 view2;

    public ViewCouple(V1 view1, V2 view2) {
        this.view1 = view1;
        this.view2 = view2;
    }

    public V1 getView1() {
        return view1;
    }

    public V2 getView2() {
        return view2;
    }

    // Kotlin compatibility
    public V1 component1() {
        return view1;
    }

    public V2 component2() {
        return view2;
    }

    public <V3 extends View> ViewTriple<V1, V2, V3> union(V3 v3) {
        return new ViewTriple<>(view1, view2, v3);
    }
}
