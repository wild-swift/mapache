package name.wildswift.mapache.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.graph.BackStackEntry;
import name.wildswift.mapache.graph.Navigatable;
import name.wildswift.mapache.graph.StateTransition;
import name.wildswift.mapache.graph.SubGraph;
import name.wildswift.mapache.graph.TransitionCallback;
import name.wildswift.mapache.graph.TransitionFactory;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public class LayerStateMachineProcessor<E extends Event, VR extends View, DC, S extends MState<E, ?, VR, DC> & Navigatable<E, DC, S>> {
    private final S initialState;
    private final TransitionFactory<E, DC, S> transFactory;

    private final NavigationContext<E, DC> navigationContext;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private final ViewContentHolderImpl<DC, S> viewsContents;

    private StateWrapper<E, ?, DC, VR, S> currentState;
    private VR currentRoot;
    private List<BackStackEntry<S>> backStack = new ArrayList<>();

    private LayerStateMachineProcessor subMachine = null;

    public LayerStateMachineProcessor(S initialState, TransitionFactory<E, DC, S> transFactory, NavigationContext<E, DC> navigationContext, ViewContentHolderImpl<DC, S> viewsContents) {
        this.initialState = initialState;
        this.transFactory = transFactory;
        this.navigationContext = navigationContext;
        this.viewsContents = viewsContents;
    }


    @SuppressWarnings("unchecked")
    public void attachToRoot(VR currentRoot) {
        this.currentRoot = currentRoot;

        if (currentState == null) {
            currentState = new StateWrapper(initialState, navigationContext, null);
            viewsContents.enterState(initialState);
            currentState.start();
            if (initialState instanceof SubGraph) {
                subMachine = new LayerStateMachineProcessor(((SubGraph) initialState).getInitialState(), transFactory, navigationContext, viewsContents);
            }
        }
        currentState.setRoot(currentRoot);
        S wrapped = currentState.getWrapped();
        if (wrapped instanceof SubGraph) {
            subMachine.attachToRoot(((SubGraph) wrapped).extractRoot(currentRoot, currentState.getCurrentViewSet()));
        }
    }

    public void detachFromRoot() {
        if (subMachine != null) subMachine.detachFromRoot();
        currentState.setRoot(null);
        currentRoot = null;
    }

    @SuppressWarnings("unchecked")
    public boolean onNewEvent(E event) {
        // TODO Add thread managment
        if (subMachine != null && subMachine.onNewEvent(event)) return true;

        S nextState = currentState.getWrapped().getNextState(event);
        if (nextState == null) return false;

        moveToState(nextState, true);
        return true;
    }

    public boolean onBack() {
        if (subMachine != null && subMachine.onBack()) return true;

        if (backStack.size() == 0) return false;

        S nextState = backStack.get(backStack.size() - 1).createInstance();
        backStack.remove(backStack.size() - 1);

        moveToState(nextState, false);

        return true;
    }

    @SuppressWarnings("unchecked")
    private void moveToState(S nextState, boolean addToBackStack) {
        StateTransition<E, ViewSet, ViewSet, VR, DC> transition = (StateTransition<E, ViewSet, ViewSet, VR, DC>) transFactory.getTransition(currentState.getWrapped(), nextState);

        if (subMachine != null) {
            subMachine.reset();
            subMachine = null;
        }
        currentState.stop();
        if (addToBackStack) {
            BackStackEntry<S> backStackEntry = (BackStackEntry<S>) currentState.getWrapped().getBackStackEntry();
            if (backStackEntry != null) {
                backStack.add(backStackEntry);
            }
        }

        viewsContents.enterState(nextState);

        if (currentRoot == null) {
            mainThreadHandler.post(new StartNewStateCommand(nextState, null));
        } else {
            mainThreadHandler.post(new HandleStartTransition(transition, nextState));
        }
    }

    public void reset() {
        currentState.stop();
        detachFromRoot();
        viewsContents.exitState(currentState.getWrapped());
        currentState = null;
    }

    private class StartNewStateCommand implements Runnable {
        private final S nextState;
        private final ViewSet currentSet;

        private StartNewStateCommand(S nextState, ViewSet currentSet) {
            this.nextState = nextState;
            this.currentSet = currentSet;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            viewsContents.exitState(currentState.getWrapped());
            currentState = new StateWrapper(nextState, navigationContext, currentSet);
            currentState.start();
            if (nextState instanceof SubGraph) {
                subMachine = new LayerStateMachineProcessor(((SubGraph) nextState).getInitialState(), transFactory, navigationContext, viewsContents);
            }

            if (currentRoot != null && currentSet == null) {
                currentState.setRoot(currentRoot);
            }

            if (nextState instanceof SubGraph && currentState.getCurrentViewSet() != null) {
                subMachine.attachToRoot(((SubGraph) nextState).extractRoot(currentRoot, currentState.getCurrentViewSet()));
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
            mainThreadHandler.post(new StartNewStateCommand(nextState, currentSet));
        }
    }

    private class HandleStartTransition implements Runnable {
        private final StateTransition<E, ViewSet, ViewSet, VR, DC> transition;
        private final S nextState;

        private HandleStartTransition(StateTransition<E, ViewSet, ViewSet, VR, DC> transition, S nextState) {
            this.transition = transition;
            this.nextState = nextState;
        }

        @Override
        public void run() {
            transition.execute(navigationContext, currentRoot, currentState.getCurrentViewSet(), new DefaultTransitionCallback(nextState));
        }
    }
}
