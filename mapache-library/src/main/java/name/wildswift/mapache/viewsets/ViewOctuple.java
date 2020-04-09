package name.wildswift.mapache.viewsets;

import android.view.View;

@SuppressWarnings("WeakerAccess")
public class ViewOctuple<V1 extends View, V2 extends View, V3 extends View, V4 extends View, V5 extends View, V6 extends View, V7 extends View, V8 extends View> extends ViewSet {
    private final V1 view1;
    private final V2 view2;
    private final V3 view3;
    private final V4 view4;
    private final V5 view5;
    private final V6 view6;
    private final V7 view7;
    private final V8 view8;

    public ViewOctuple(V1 view1, V2 view2, V3 view3, V4 view4, V5 view5, V6 view6, V7 view7, V8 view8) {
        this.view1 = view1;
        this.view2 = view2;
        this.view3 = view3;
        this.view4 = view4;
        this.view5 = view5;
        this.view6 = view6;
        this.view7 = view7;
        this.view8 = view8;
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

    public V7 getView7() {
        return view7;
    }

    public V8 getView8() {
        return view8;
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

    public V7 component7() {
        return view7;
    }

    public V8 component8() {
        return view8;
    }
}
