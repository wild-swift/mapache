package name.wildswift.mapache.states;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import org.jetbrains.annotations.NotNull;

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
    @NotNull VS setup(@NotNull RV rootView, @NotNull NavigationContext<E, DC> context);
    @NotNull Runnable dataBind(@NotNull NavigationContext<E, DC> context, @NotNull VS views);
    @NotNull Runnable start(@NotNull NavigationContext<E, DC> context);
}
