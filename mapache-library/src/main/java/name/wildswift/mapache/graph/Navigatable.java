package name.wildswift.mapache.graph;

import android.util.Pair;
import android.view.View;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public interface Navigatable<E extends Event, DC, NS extends MState<E, ? extends ViewSet, ? extends View, DC> & Navigatable<E, DC, NS>> {
    // TODO think about specify view sets
    @Nullable NS getNextState(@NotNull E e);
    @Nullable BackStackEntry<? extends NS> getBackStackEntry();
    boolean singleInBackStack();
}