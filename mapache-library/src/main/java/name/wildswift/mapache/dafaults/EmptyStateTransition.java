package name.wildswift.mapache.dafaults;

import android.widget.FrameLayout;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.graph.StateTransition;
import name.wildswift.mapache.graph.TransitionCallback;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public class EmptyStateTransition<E extends Event, VS extends ViewSet> extends StateTransition<E, VS, VS> {

    public EmptyStateTransition(NavigationContext<E, ?> context, FrameLayout rootView, MState<E, ?, ?> from, MState<E, ?, ?> to, VS viewSet) {
        super(context, rootView, from, to, viewSet);
    }

    @Override
    public void execute(TransitionCallback callback) {
        callback.onTransitionEnded(viewSet);
    }
}