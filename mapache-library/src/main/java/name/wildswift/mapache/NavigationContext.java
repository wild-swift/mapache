package name.wildswift.mapache;


import org.jetbrains.annotations.NotNull;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.osintegration.SystemCalls;
import name.wildswift.mapache.viewcontent.ViewContentHolder;

public class NavigationContext<E extends Event, DC> {
    @NotNull
    private final DC diContext;
    @NotNull
    private final Eventer<E> eventer;
    @NotNull
    private final ViewContentHolder viewsContents;
    @NotNull
    private final SystemCalls systemCalls;

    NavigationContext(@NotNull DC diContext, @NotNull Eventer<E> eventer, @NotNull ViewContentHolder viewsContents, @NotNull SystemCalls systemCalls) {
        this.diContext = diContext;
        this.eventer = eventer;
        this.viewsContents = viewsContents;
        this.systemCalls = systemCalls;
    }

    @NotNull
    public DC getDiContext() {
        return diContext;
    }

    @NotNull
    public Eventer<E> getEventer() {
        return eventer;
    }

    @NotNull
    public ViewContentHolder getViewsContents() {
        return viewsContents;
    }

    @NotNull
    public SystemCalls getSystemCalls() {
        return systemCalls;
    }
}
