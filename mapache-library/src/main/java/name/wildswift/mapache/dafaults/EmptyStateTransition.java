package name.wildswift.mapache.dafaults;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.graph.StateTransition;
import name.wildswift.mapache.graph.TransitionCallback;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public class EmptyStateTransition<E extends Event, VS extends ViewSet, DC> extends StateTransition<E, VS, VS, DC> {

    public EmptyStateTransition(MState<E, ?, ?> from, MState<E, ?, ?> to) {
        super(from, to);
    }

    @Override
    public void execute(@NonNull NavigationContext context, @Nullable FrameLayout rootView, @Nullable ViewSet inViews, @NonNull TransitionCallback callback) {
        callback.onTransitionEnded(inViews);
    }

}