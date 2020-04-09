package name.wildswift.mapache.states;

import android.widget.FrameLayout;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.osintegration.SystemCalls;
import name.wildswift.mapache.viewsets.ViewSet;

public interface MState<E extends Event, VS extends ViewSet, DC> {
    /**
     * Use for set application screen and initial fill data (no binding, listeners, etc.)
     * @param rootView
     * @return
     */
    VS setup(FrameLayout rootView, NavigationContext<E, DC> context);
    Runnable dataBind(NavigationContext<E, DC> context, VS views);
    Runnable start(NavigationContext<E, DC> context, SystemCalls caller);
}
