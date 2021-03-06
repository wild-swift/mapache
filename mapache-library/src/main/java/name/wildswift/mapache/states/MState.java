package name.wildswift.mapache.states;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.osintegration.SystemCalls;
import name.wildswift.mapache.viewsets.ViewSet;

public interface MState<E extends Event, VS extends ViewSet, RV extends View, DC> {
    /**
     * Use for set application screen and initial fill data (no binding, listeners, etc.)
     * @param rootView
     * @return
     */
    @NonNull VS setup(@NonNull RV rootView, @NonNull NavigationContext<E, DC> context);
    @NonNull Runnable dataBind(@NonNull NavigationContext<E, DC> context, @NonNull VS views);
    @NonNull Runnable start(@NonNull NavigationContext<E, DC> context);
}
