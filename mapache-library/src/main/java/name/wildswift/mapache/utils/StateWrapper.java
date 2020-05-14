package name.wildswift.mapache.utils;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public final class StateWrapper<E extends Event, VS extends ViewSet, DC, S extends MState<E, VS, DC>> {
    private final S state;
    private final NavigationContext<E, DC> context;

    private final SingleTimeInitDeinitCaller startStopCaller;

    private SingleTimeInitDeinitCaller bindUnbindCaller;
    private VS viewSet;

    public StateWrapper(@NonNull S state, @NonNull NavigationContext<E, DC> context, @Nullable VS viewSet) {
        this.state = state;
        this.context = context;
        this.viewSet = viewSet;
        startStopCaller = new SingleTimeInitDeinitCaller(new StartStopCallable());
        if (viewSet != null) {
            bindUnbindCaller = new SingleTimeInitDeinitCaller(new BindUnbindCaller());
        }
    }

    public void setRoot(@Nullable ViewGroup root) {
        if (root != null) {
            if (viewSet == null) {
                viewSet = state.setup(root, context);
            }
            bindUnbindCaller = new SingleTimeInitDeinitCaller(new BindUnbindCaller());
            bindUnbindCaller.init();
        } else {
            if (bindUnbindCaller != null) bindUnbindCaller.deinit();
            viewSet = null;
            bindUnbindCaller = null;
        }

    }

    public void start() {
        startStopCaller.init();
        if (bindUnbindCaller != null) bindUnbindCaller.init();
    }

    public void stop() {
        startStopCaller.deinit();
        if (bindUnbindCaller != null) bindUnbindCaller.deinit();
    }

    public S getWrapped() {
        return state;
    }

    public VS getCurrentViewSet() {
        return viewSet;
    }

    private class StartStopCallable implements Callable<Runnable> {
        @Override
        public Runnable call() {
            return state.start(context);
        }
    }

    private class BindUnbindCaller implements Callable<Runnable> {
        @Override
        public Runnable call() {
            return state.dataBind(context, viewSet);
        }
    }
}
