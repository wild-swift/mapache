package name.wildswift.mapache;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.viewcontent.ViewContentHolder;

public class NavigationContext<E extends Event, DC> {
    private final DC diContext;
    private final Eventer<E> eventer;
    private final ViewContentHolder viewsContents;

    NavigationContext(DC diContext, Eventer<E> eventer, ViewContentHolder viewsContents) {
        this.diContext = diContext;
        this.eventer = eventer;
        this.viewsContents = viewsContents;
    }

    public DC getDiContext() {
        return diContext;
    }

    public Eventer<E> getEventer() {
        return eventer;
    }

    public ViewContentHolder getViewsContents() {
        return viewsContents;
    }
}
