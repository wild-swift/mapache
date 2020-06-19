package name.wildswift.mapache.graph;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;

public interface TransitionFactory<E extends Event, DC, VR extends View, S extends MState<E, ?, VR, DC>> {
    StateTransition<E, ?, ?, VR, DC> getTransition(@NonNull S from, @NonNull S to);
}
