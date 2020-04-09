package name.wildswift.mapache.graph;

import android.util.Pair;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public interface NavigationGraph<E extends Event, NS extends MState<E, ?, ?>> {
    Pair<NS, StateTransition<E, ViewSet, ViewSet>>  getNextState(NS currentState, E e);
}