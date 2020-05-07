package name.wildswift.mapache;

import android.app.Activity;
import android.widget.FrameLayout;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.events.SystemEventFactory;
import name.wildswift.mapache.graph.NavigationGraph;
import name.wildswift.mapache.interfaces.ActivityCaller;
import name.wildswift.mapache.osintegration.PermissionsCallback;
import name.wildswift.mapache.osintegration.SystemCalls;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.utils.SingleTimeInitDeinitCaller;
import name.wildswift.mapache.utils.StateWrapper;
import name.wildswift.mapache.viewcontent.ViewContentMetaSource;
import name.wildswift.mapache.viewsets.ViewSet;

public final class NavigationStateMachine<E extends Event, DC, S extends MState<E, ? extends ViewSet, DC>> implements Eventer<E>, SystemCalls {
    private final S initialState;
    private final NavigationGraph<E, DC, S> graph;
    private final SystemEventFactory<E> systemEvents;
    private final ViewContentMetaSource metaSource;
    private final NavigationContext<E, DC> navigationContext;

    private StateWrapper<E, ViewSet, DC, MState<E, ViewSet, DC>> currentState;

    private SingleTimeInitDeinitCaller startCaller;
    private ActivityCaller activityCaller;



    public NavigationStateMachine(S initialState, NavigationGraph<E, DC, S> graph, SystemEventFactory<E> systemEvents, ViewContentMetaSource metaSource, DC diContext) {
        this.initialState = initialState;
        this.graph = graph;
        this.systemEvents = systemEvents;
        this.metaSource = metaSource;
        this.navigationContext = new NavigationContext<>(diContext, this, null, this);

        currentState = new StateWrapper<>((MState<E, ViewSet, DC>) initialState, navigationContext, null);
    }

    @Override
    public void onNewEvent(E event) {
    }

    public void detachFromActivity() {
        activityCaller = null;
    }

    public void attachToActivity(Activity activity, ActivityCaller caller) {
        activityCaller = caller;
        if (currentState != null) {
            currentState.setRoot(activity.<FrameLayout>findViewById(android.R.id.content));
        }
    }

    @Override
    public void requestPermissions(String[] permissions, PermissionsCallback callback) {

    }
}
