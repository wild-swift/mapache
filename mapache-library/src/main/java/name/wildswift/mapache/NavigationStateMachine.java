package name.wildswift.mapache;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import name.wildswift.mapache.contextintegration.CallToActivityBridge;
import name.wildswift.mapache.debouncers.CancelableDebouncer;
import name.wildswift.mapache.debouncers.DebounceCallback;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.graph.Navigatable;
import name.wildswift.mapache.contextintegration.ActivityCaller;
import name.wildswift.mapache.graph.StateTransition;
import name.wildswift.mapache.graph.TransitionCallback;
import name.wildswift.mapache.graph.TransitionFactory;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.utils.StateWrapper;
import name.wildswift.mapache.viewcontent.ViewContentMetaSource;
import name.wildswift.mapache.viewsets.ViewSet;

public final class NavigationStateMachine<E extends Event, VR extends View, DC, S extends MState<E, ?, VR, DC> & Navigatable<E, DC, S>> {
    private final S initialState;
    private final TransitionFactory<E, DC, S> transFactory;
    private final ViewContentMetaSource metaSource;

    private final EventerInternal eventerInternal;
    private final CallToActivityBridge callToActivityBridge;
    private final NavigationContext<E, DC> navigationContext;

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final ViewContentHolderImpl<S> viewsContents;

    private StateWrapper<E, ?, DC, VR, S> currentState;
    private VR currentRoot;

    private boolean isPaused = false;

    private CancelableDebouncer<Boolean> debouncer = new CancelableDebouncer<>(500);


    public NavigationStateMachine(S initialState, TransitionFactory<E, DC, ?> transFactory, ViewContentMetaSource<S> metaSource, DC diContext) {
        this.initialState = initialState;
        this.transFactory = (TransitionFactory<E, DC, S>) transFactory;
        this.metaSource = metaSource;

        this.callToActivityBridge = new CallToActivityBridge();
        this.eventerInternal = new EventerInternal();
        viewsContents = new ViewContentHolderImpl<>(metaSource);
        this.navigationContext = new NavigationContext<>(diContext, eventerInternal, viewsContents, callToActivityBridge.getSystemCaller());

        currentState = new StateWrapper(initialState, navigationContext, null);
        viewsContents.onNewState(initialState);
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
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("resume should be run on main thread");
        if (isPaused) {
            debouncer.onNewValue(false);
        } else {
            debouncer.cancel();
        }
    }

    public void pause() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("pause should be run on main thread");
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
        // TODO Add thread managment
        if (currentState.onNewEvent(event)) return true;

        S nextState = currentState.getWrapped().getNextState(event);
        if (nextState == null) return false;

        StateTransition<E, ViewSet, ViewSet, VR, DC> transition = (StateTransition<E, ViewSet, ViewSet, VR, DC>) transFactory.getTransition(currentState.getWrapped(), nextState);

        currentState.stop();
        if (currentRoot == null) {
            mainThreadHandler.post(new SetNewStateCommand(nextState, null));
        } else {
            mainThreadHandler.post(new HandleStartTransition(transition, nextState));
        }
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
            viewsContents.onNewState(nextState);
            currentState = new StateWrapper(nextState, navigationContext, currentSet);
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

    private class HandleStartTransition implements Runnable {
        private final StateTransition<E, ViewSet, ViewSet, VR, DC> transition;
        private final S nextState;

        public HandleStartTransition(StateTransition<E, ViewSet, ViewSet, VR, DC> transition, S nextState) {
            this.transition = transition;
            this.nextState = nextState;
        }

        @Override
        public void run() {
            transition.execute(navigationContext, currentRoot, currentState.getCurrentViewSet(), new DefaultTransitionCallback(nextState));
        }
    }
}
