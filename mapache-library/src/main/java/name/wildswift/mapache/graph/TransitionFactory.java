package name.wildswift.mapache.graph;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;

public interface TransitionFactory<E extends Event, DC, S extends MState<E, ?, DC>> {
    StateTransition<E, ?, ?, DC> getTransition(S from, S to);
}
