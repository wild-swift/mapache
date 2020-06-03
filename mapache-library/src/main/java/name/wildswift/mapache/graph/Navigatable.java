package name.wildswift.mapache.graph;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public interface Navigatable<E extends Event, DC, NS extends MState<E, ? extends ViewSet, DC> & Navigatable<E, DC, NS>> {
    // TODO think about specify view sets
    @Nullable NS getNextState(@NonNull E e);
}