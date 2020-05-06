package name.wildswift.mapache;

import android.app.Activity;
import android.widget.FrameLayout;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.events.SystemEventFactory;
import name.wildswift.mapache.graph.NavigationGraph;
import name.wildswift.mapache.interfaces.ActivityCaller;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewcontent.ViewContentMetaSource;
import name.wildswift.mapache.viewsets.ViewSet;

public final class NavigationStateMachine<E extends Event, DC, VS extends ViewSet, S extends MState<E, VS, DC>> implements Eventer<E> {
    private final S initialState;
    private final NavigationGraph<E, DC, S> graph;
    private final SystemEventFactory<E> systemEvents;
    private final ViewContentMetaSource metaSource;
    private final NavigationContext<E, DC> navigationContext;

    private S currentState;
    private VS currentViews;
    private ActivityCaller activityCaller;


    public NavigationStateMachine(S initialState, NavigationGraph<E, DC, S> graph, SystemEventFactory<E> systemEvents, ViewContentMetaSource metaSource, DC diContext) {
        this.initialState = initialState;
        this.graph = graph;
        this.systemEvents = systemEvents;
        this.metaSource = metaSource;
        this.navigationContext = new NavigationContext<>(diContext, this, null);

        currentState = initialState;
    }

    @Override
    public void onNewEvent(E event) {
    }

    public void detachFromScene() {
        activityCaller = null;
    }

    public void attachToScene(Activity activity, ActivityCaller caller) {
        activityCaller = caller;
        if (currentState != null) {
            currentViews = currentState.setup(activity.<FrameLayout>findViewById(android.R.id.content), navigationContext);
            currentState.dataBind(navigationContext, currentViews);
        }
    }
}
