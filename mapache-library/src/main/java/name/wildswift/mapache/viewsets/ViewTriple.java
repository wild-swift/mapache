package name.wildswift.mapache.viewsets;

import android.view.View;

@SuppressWarnings("WeakerAccess")
public class ViewTriple<V1 extends View, V2 extends View, V3 extends View> extends ViewSet {
    private final V1 view1;
    private final V2 view2;
    private final V3 view3;

    public ViewTriple(V1 view1, V2 view2, V3 view3) {
        this.view1 = view1;
        this.view2 = view2;
        this.view3 = view3;
    }

    public V1 getView1() {
        return view1;
    }

    public V2 getView2() {
        return view2;
    }

    public V3 getView3() {
        return view3;
    }

    // Kotlin compatibility
    public V1 component1() {
        return view1;
    }

    public V2 component2() {
        return view2;
    }

    public V3 component3() {
        return view3;
    }

    public <V4 extends View> ViewQuadruple<V1, V2, V3, V4> union(V4 v4) {
        return new ViewQuadruple<>(view1, view2, view3, v4);
    }

}
