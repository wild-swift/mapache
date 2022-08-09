package name.wildswift.mapache.graph;


import org.jetbrains.annotations.NotNull;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;

public interface TransitionFactory<E extends Event, DC, S extends MState<E, ?, ?, DC>> {
    StateTransition<E, ?, ?, ?, DC> getTransition(@NotNull S from, @NotNull S to);
}
