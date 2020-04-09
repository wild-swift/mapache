package name.wildswift.mapache.viewsets;

import android.view.View;

public abstract class ViewSet {

    public static <V1 extends View> ViewSingle<V1> from(V1 v1) {
        return new ViewSingle<>(v1);
    }

    public static <V1 extends View, V2 extends View> ViewCouple<V1, V2> from(V1 v1, V2 v2) {
        return new ViewCouple<>(v1, v2);
    }

    public static <V1 extends View, V2 extends View, V3 extends View> ViewTriple<V1, V2, V3> from(V1 v1, V2 v2, V3 v3) {
        return new ViewTriple<>(v1, v2, v3);
    }

    public static <V1 extends View, V2 extends View, V3 extends View, V4 extends View> ViewQuadruple<V1, V2, V3, V4> from(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new ViewQuadruple<>(v1, v2, v3, v4);
    }

    public static <V1 extends View, V2 extends View, V3 extends View, V4 extends View, V5 extends View> ViewQuintuple<V1, V2, V3, V4, V5> from(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        return new ViewQuintuple<>(v1, v2, v3, v4, v5);
    }

    public static <V1 extends View, V2 extends View, V3 extends View, V4 extends View, V5 extends View, V6 extends View> ViewSextuple<V1, V2, V3, V4, V5, V6> from(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
        return new ViewSextuple<>(v1, v2, v3, v4, v5, v6);
    }

    public static <V1 extends View, V2 extends View, V3 extends View, V4 extends View, V5 extends View, V6 extends View, V7 extends View> ViewSeptuple<V1, V2, V3, V4, V5, V6, V7> from(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7) {
        return new ViewSeptuple<>(v1, v2, v3, v4, v5, v6, v7);
    }

    public static <V1 extends View, V2 extends View, V3 extends View, V4 extends View, V5 extends View, V6 extends View, V7 extends View, V8 extends View> ViewOctuple<V1, V2, V3, V4, V5, V6, V7, V8> from(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8) {
        return new ViewOctuple<>(v1, v2, v3, v4, v5, v6, v7, v8);
    }
}
