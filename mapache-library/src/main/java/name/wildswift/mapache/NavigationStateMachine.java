package name.wildswift.mapache;

import android.app.Activity;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.events.SystemEventFactory;
import name.wildswift.mapache.graph.NavigationGraph;
import name.wildswift.mapache.interfaces.ActivityCaller;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewcontent.ViewContentMetaSource;
import name.wildswift.mapache.viewsets.ViewSet;

public final class NavigationStateMachine<E extends Event, S extends MState<E, ? extends ViewSet, ?>> implements Eventer<E> {
    private final S initialState;
    private final NavigationGraph<E, S> graph;
    private final SystemEventFactory<E> systemEvents;
    private final ViewContentMetaSource metaSource;

    public NavigationStateMachine(S initialState, NavigationGraph<E, S> graph, SystemEventFactory<E> systemEvents, ViewContentMetaSource metaSource) {
        this.initialState = initialState;
        this.graph = graph;
        this.systemEvents = systemEvents;
        this.metaSource = metaSource;
    }

    @Override
    public void onNewEvent(E event) {

    }

    public void detachFromScene() {

    }

    public void attachToScene(Activity activity, ActivityCaller caller) {

    }
}
