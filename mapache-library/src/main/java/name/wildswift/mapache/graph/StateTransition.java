package name.wildswift.mapache.graph;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public abstract class StateTransition<E extends Event, VS_IN extends ViewSet, VS_OUT extends ViewSet, RV extends View, DC> {
    protected final MState<E, ?, RV, ?> from;
    protected final MState<E, ?, RV, ?> to;

    // TODO why not use VS_IN + WS_OUT
    public StateTransition(MState<E, ?, RV, ?> from, MState<E, ?, RV, ?> to) {
        this.from = from;
        this.to = to;
    }

    public abstract void execute(@NonNull NavigationContext<E, DC> context, @NonNull RV rootView, @NonNull VS_IN inViews, @NonNull TransitionCallback<VS_OUT> callback);
}