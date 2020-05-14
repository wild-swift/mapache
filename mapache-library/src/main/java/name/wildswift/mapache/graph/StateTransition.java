package name.wildswift.mapache.graph;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public abstract class StateTransition<E extends Event, VS_IN extends ViewSet, VS_OUT extends ViewSet, DC> {
    protected final MState<E, ?, ?> from;
    protected final MState<E, ?, ?> to;

    public StateTransition(MState<E, ?, ?> from, MState<E, ?, ?> to) {
        this.from = from;
        this.to = to;
    }

    public abstract void execute(@NonNull NavigationContext<E, DC> context, @Nullable FrameLayout rootView, @Nullable VS_IN inViews, @NonNull TransitionCallback<VS_OUT> callback);
}