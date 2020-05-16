package name.wildswift.mapache;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.widget.FrameLayout;

import java.util.Map;

import name.wildswift.mapache.contextintegration.CallToActivityBridge;
import name.wildswift.mapache.debouncers.Cancelable;
import name.wildswift.mapache.debouncers.CancelableDebouncer;
import name.wildswift.mapache.debouncers.DebounceCallback;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.events.SystemEventFactory;
import name.wildswift.mapache.graph.NavigationGraph;
import name.wildswift.mapache.contextintegration.ActivityCaller;
import name.wildswift.mapache.contextintegration.ActivityEventsCallback;
import name.wildswift.mapache.graph.StateTransition;
import name.wildswift.mapache.graph.TransitionCallback;
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

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private StateWrapper<E, ViewSet, DC, MState<E, ViewSet, DC>> currentState;
    private FrameLayout currentRoot;

    private boolean isPaused = false;

    private CancelableDebouncer<Boolean> debouncer = new CancelableDebouncer<>(500);


    public NavigationStateMachine(S initialState, NavigationGraph<E, DC, S> graph, SystemEventFactory<E> systemEvents, ViewContentMetaSource metaSource, DC diContext) {
        this.initialState = initialState;
        this.graph = graph;
        this.systemEvents = systemEvents;
        this.metaSource = metaSource;

        this.callToActivityBridge = new CallToActivityBridge();
        this.eventerInternal = new EventerInternal();
        this.navigationContext = new NavigationContext<>(diContext, eventerInternal, null, callToActivityBridge.getSystemCaller());

        currentState = new StateWrapper<>((MState<E, ViewSet, DC>) initialState, navigationContext, null);
        currentState.start();

        debouncer.addCallback(new ChangePauseStateListener());
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
        if (isPaused) {
            debouncer.onNewValue(false);
        } else {
            debouncer.cancel();
        }
    }

    public void pause() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("attachToActivity should be run on main thread");
        if (!isPaused) {
            debouncer.onNewValue(true);
        } else {
            debouncer.cancel();
        }
    }

    public void detachFromActivity() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("detachFromActivity should be run on main thread");
        currentState.setRoot(null);
        currentRoot = null;
        callToActivityBridge.detachFromActivity();
    }

    private boolean onNewEvent(E event) {
        if (currentState.onNewEvent(event)) return true;

        Pair<S, StateTransition<E, ViewSet, ViewSet, DC>> _tmpVar = graph.getNextState((S) currentState.getWrapped(), event);
        if (_tmpVar == null) return false;

        S nextState = _tmpVar.first;
        StateTransition<E, ViewSet, ViewSet, DC> transition = _tmpVar.second;

        currentState.stop();
        transition.execute(navigationContext, currentRoot, currentState.getCurrentViewSet(), new DefaultTransitionCallback(nextState));
        return true;
    }

    private void stopStateMachineExecution() {
        // TODO need implement this logic
    }

    private void resumeStateMachineExecution() {
        // TODO need implement this logic
    }

    private class EventerInternal implements Eventer<E> {
        @Override
        public boolean onNewEvent(E event) {
            return NavigationStateMachine.this.onNewEvent(event);
        }
    }

    private class ChangePauseStateListener implements DebounceCallback<Boolean> {
        @Override
        public void newValue(final Boolean value) {
            // TODO need set specific executor to debouncer, but there no way to do it now (write AndroidHandlerExecutor, that implements ScheduledExecutorService
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(isPaused == value) return;
                    isPaused = value;
                    if (isPaused) {
                        stopStateMachineExecution();
                    } else {
                        resumeStateMachineExecution();
                    }
                }
            });
        }
    }

    private class SetNewStateCommand implements Runnable {
        private final S nextState;
        private final ViewSet currentSet;

        private SetNewStateCommand(S nextState, ViewSet currentSet) {
            this.nextState = nextState;
            this.currentSet = currentSet;
        }

        @Override
        public void run() {
            currentState = new StateWrapper<>((MState<E, ViewSet, DC>) nextState, navigationContext, currentSet);
            currentState.start();
            if (currentRoot != null && currentSet == null) {
                currentState.setRoot(currentRoot);
            }
        }
    }

    private class DefaultTransitionCallback implements TransitionCallback<ViewSet> {
        private final S nextState;

        private DefaultTransitionCallback(S nextState) {
            this.nextState = nextState;
        }

        @Override
        public void onTransitionEnded(ViewSet currentSet) {
            mainThreadHandler.post(new SetNewStateCommand(nextState, currentSet));
        }
    }
}
