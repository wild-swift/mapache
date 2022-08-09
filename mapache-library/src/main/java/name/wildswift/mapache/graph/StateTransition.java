package name.wildswift.mapache.graph;

import android.view.View;


import org.jetbrains.annotations.NotNull;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public abstract class StateTransition<E extends Event, VS_IN extends ViewSet, VS_OUT extends ViewSet, RV extends View, DC> {
    protected final MState<E, VS_IN, RV, DC> from;
    protected final MState<E, VS_OUT, RV, DC> to;

    // TODO why not use VS_IN + WS_OUT
    public StateTransition(MState<E, VS_IN, RV, DC> from, MState<E, VS_OUT, RV, DC> to) {
        this.from = from;
        this.to = to;
    }

    public abstract void execute(@NotNull NavigationContext<E, DC> context, @NotNull RV rootView, @NotNull VS_IN inViews, @NotNull TransitionCallback<VS_OUT> callback);
}