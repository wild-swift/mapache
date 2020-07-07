package name.wildswift.mapache.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

final class StateWrapper<E extends Event, VS extends ViewSet, DC, VR extends View, S extends MState<E, VS, VR, DC>> implements Eventer<E> {
    private final S state;
    private final NavigationContext<E, DC> context;

    private final SingleTimeInitDeinitCaller startStopCaller;

    private SingleTimeInitDeinitCaller bindUnbindCaller;
    private VS viewSet;

    StateWrapper(@NonNull S state, @NonNull NavigationContext<E, DC> context, @Nullable VS viewSet) {
        this.state = state;
        this.context = context;
        this.viewSet = viewSet;
        startStopCaller = new SingleTimeInitDeinitCaller(new StartStopCallable());
        if (viewSet != null) {
            bindUnbindCaller = new SingleTimeInitDeinitCaller(new BindUnbindCaller());
        }
    }

    void setRoot(@Nullable VR root) {
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

    void start() {
        startStopCaller.init();
        if (bindUnbindCaller != null) bindUnbindCaller.init();
    }

    void stop() {
        startStopCaller.deinit();
        if (bindUnbindCaller != null) bindUnbindCaller.deinit();
    }

    S getWrapped() {
        return state;
    }

    VS getCurrentViewSet() {
        return viewSet;
    }

    @Override
    public boolean onNewEvent(E event) {
        return false;
    }

    @Override
    public boolean onBack() {
        return false;
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
