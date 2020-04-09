package name.wildswift.mapache.viewsets;

import android.view.View;

@SuppressWarnings("WeakerAccess")
public class ViewSextuple<V1 extends View, V2 extends View, V3 extends View, V4 extends View, V5 extends View, V6 extends View> extends ViewSet {
    private final V1 view1;
    private final V2 view2;
    private final V3 view3;
    private final V4 view4;
    private final V5 view5;
    private final V6 view6;

    public ViewSextuple(V1 view1, V2 view2, V3 view3, V4 view4, V5 view5, V6 view6) {
        this.view1 = view1;
        this.view2 = view2;
        this.view3 = view3;
        this.view4 = view4;
        this.view5 = view5;
        this.view6 = view6;
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

    public V5 getView5() {
        return view5;
    }

    public V6 getView6() {
        return view6;
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

    public V5 component5() {
        return view5;
    }

    public V6 component6() {
        return view6;
    }

    public <V7 extends View> ViewSeptuple<V1, V2, V3, V4, V5, V6, V7> union(V7 v7) {
        return new ViewSeptuple<>(view1, view2, view3, view4, view5, view6, v7);
    }
}
