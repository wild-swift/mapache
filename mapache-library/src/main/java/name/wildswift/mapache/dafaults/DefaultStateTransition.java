package name.wildswift.mapache.dafaults;

import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.graph.StateTransition;
import name.wildswift.mapache.graph.TransitionCallback;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public class DefaultStateTransition<E extends Event, VS_IN extends ViewSet, VS_OUT extends ViewSet, RV extends View, DC> extends StateTransition<E, VS_IN, VS_OUT, RV, DC> {

    public DefaultStateTransition(MState<E, VS_IN, RV, DC> from, MState<E, VS_OUT, RV, DC> to) {
        super(from, to);
    }

    @Override
    public void execute(@NotNull NavigationContext<E, DC> context, @NotNull RV rootView, @NotNull VS_IN inViews, @NotNull TransitionCallback<VS_OUT> callback) {
        if (rootView instanceof ViewGroup) ((ViewGroup) rootView).removeAllViews();
        VS_OUT setup = to.setup(rootView, context);
        callback.onTransitionEnded(setup);
    }

}