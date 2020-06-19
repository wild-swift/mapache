package name.wildswift.mapache.dafaults;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.graph.StateTransition;
import name.wildswift.mapache.graph.TransitionCallback;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public class EmptyStateTransition<E extends Event, VS extends ViewSet, RV extends View, DC> extends StateTransition<E, VS, VS, RV, DC> {

    public EmptyStateTransition(MState<E, ?, RV, ?> from, MState<E, ?, RV, ?> to) {
        super(from, to);
    }

    @Override
    public void execute(@NonNull NavigationContext<E, DC> context, @NonNull RV rootView, @NonNull VS inViews, @NonNull TransitionCallback<VS> callback) {
        callback.onTransitionEnded(inViews);
    }

}