package name.wildswift.mapache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.osintegration.SystemCalls;
import name.wildswift.mapache.viewcontent.ViewContentHolder;

public class NavigationContext<E extends Event, DC> {
    @Nullable
    private final DC diContext;
    @NonNull
    private final Eventer<E> eventer;
    @NonNull
    private final ViewContentHolder viewsContents;
    @NonNull
    private final SystemCalls systemCalls;

    NavigationContext(@Nullable DC diContext, @NonNull Eventer<E> eventer, @NonNull ViewContentHolder viewsContents, @NonNull SystemCalls systemCalls) {
        this.diContext = diContext;
        this.eventer = eventer;
        this.viewsContents = viewsContents;
        this.systemCalls = systemCalls;
    }

    @Nullable
    public DC getDiContext() {
        return diContext;
    }

    @NonNull
    public Eventer<E> getEventer() {
        return eventer;
    }

    @NonNull
    public ViewContentHolder getViewsContents() {
        return viewsContents;
    }

    @NonNull
    public SystemCalls getSystemCalls() {
        return systemCalls;
    }
}
