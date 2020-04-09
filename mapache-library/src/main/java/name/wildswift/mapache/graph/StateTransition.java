package name.wildswift.mapache.graph;

import android.widget.FrameLayout;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public abstract class StateTransition<E extends Event, VS_IN extends ViewSet, VS_OUT extends ViewSet> {
    protected final NavigationContext<E, ?> context;
    protected final FrameLayout rootView;
    protected final MState<E, ?, ?> from;
    protected final MState<E, ?, ?> to;
    protected final VS_IN viewSet;

    public StateTransition(NavigationContext<E, ?> context, FrameLayout rootView, MState<E, ?, ?> from, MState<E, ?, ?> to, VS_IN viewSet) {
        this.context = context;
        this.rootView = rootView;
        this.from = from;
        this.to = to;
        this.viewSet = viewSet;
    }

    public abstract void execute(TransitionCallback<VS_OUT> callback);
}