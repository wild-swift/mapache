package name.wildswift.mapache.graph;

import android.view.View;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public interface SubGraph<E extends Event, VR extends View, DC, S extends MState<E, ?, VR, DC> & Navigatable<E, DC, S>> {
    S getInitialState();
    VR extractRoot(View currentRoot, ViewSet currentViewSet);
}
