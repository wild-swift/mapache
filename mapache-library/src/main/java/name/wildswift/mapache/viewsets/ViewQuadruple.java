package name.wildswift.mapache.viewsets;

import android.view.View;

@SuppressWarnings("WeakerAccess")
public class ViewQuadruple<V1 extends View, V2 extends View, V3 extends View, V4 extends View> extends ViewSet {
    private final V1 view1;
    private final V2 view2;
    private final V3 view3;
    private final V4 view4;

    public ViewQuadruple(V1 view1, V2 view2, V3 view3, V4 view4) {
        this.view1 = view1;
        this.view2 = view2;
        this.view3 = view3;
        this.view4 = view4;
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

    public V4 getView4() {
        return view4;
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

    public V4 component4() {
        return view4;
    }

    public <V5 extends View> ViewQuintuple<V1, V2, V3, V4, V5> union(V5 v5) {
        return new ViewQuintuple<>(view1, view2, view3, view4, v5);
    }

}
