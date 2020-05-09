package name.wildswift.mapache;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.widget.FrameLayout;

import java.util.Map;

import name.wildswift.mapache.contextintegration.CallToActivityBridge;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.events.SystemEventFactory;
import name.wildswift.mapache.graph.NavigationGraph;
import name.wildswift.mapache.contextintegration.ActivityCaller;
import name.wildswift.mapache.contextintegration.ActivityEventsCallback;
import name.wildswift.mapache.osintegration.PermissionsCallback;
import name.wildswift.mapache.osintegration.SystemCalls;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.utils.StateWrapper;
import name.wildswift.mapache.viewcontent.ViewContentMetaSource;
import name.wildswift.mapache.viewsets.ViewSet;

public final class NavigationStateMachine<E extends Event, DC, S extends MState<E, ? extends ViewSet, DC>> {
    private final S initialState;
    private final NavigationGraph<E, DC, S> graph;
    private final SystemEventFactory<E> systemEvents;
    private final ViewContentMetaSource metaSource;

    private final EventerInternal eventerInternal;
    private final CallToActivityBridge callToActivityBridge;
    private final NavigationContext<E, DC> navigationContext;

    private StateWrapper<E, ViewSet, DC, MState<E, ViewSet, DC>> currentState;
    private FrameLayout currentRoot;


    public NavigationStateMachine(S initialState, NavigationGraph<E, DC, S> graph, SystemEventFactory<E> systemEvents, ViewContentMetaSource metaSource, DC diContext) {
        this.initialState = initialState;
        this.graph = graph;
        this.systemEvents = systemEvents;
        this.metaSource = metaSource;

        this.callToActivityBridge = new CallToActivityBridge();
        this.eventerInternal = new EventerInternal();
        this.navigationContext = new NavigationContext<>(diContext, eventerInternal, null, callToActivityBridge.getSystemCaller());

        currentState = new StateWrapper<>((MState<E, ViewSet, DC>) initialState, navigationContext, null);
    }

    public void attachToActivity(Activity activity, ActivityCaller caller) {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("attachToActivity should be run on main thread");
        callToActivityBridge.attachToActivity(caller);
        currentRoot = activity.findViewById(android.R.id.content);

        if (currentState != null) {
            currentState.setRoot(currentRoot);
        }
    }

    public void resume() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("attachToActivity should be run on main thread");
        if (currentState != null) {
            currentState.start();
        }
    }

    public void pause() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("attachToActivity should be run on main thread");
        if (currentState != null) {
            currentState.stop();
        }
    }

    public void detachFromActivity() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("detachFromActivity should be run on main thread");
        currentState.setRoot(null);
        currentRoot = null;
        callToActivityBridge.detachFromActivity();
    }

    private void onNewEvent(E event) {
    }

    private class EventerInternal implements Eventer<E> {
        @Override
        public void onNewEvent(E event) {
            NavigationStateMachine.this.onNewEvent(event);
        }
    }
}
