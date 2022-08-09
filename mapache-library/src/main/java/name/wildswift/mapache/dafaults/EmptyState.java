package name.wildswift.mapache.dafaults;

import android.view.View;


import org.jetbrains.annotations.NotNull;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;
import name.wildswift.mapache.viewsets.ViewVoid;

class EmptyState<E extends Event, RV extends View, DC> implements MState<E, ViewVoid,RV,DC> {

    private EmptyRunnable emptyRunnable = new EmptyRunnable();

    @NotNull
    @Override
    public ViewVoid setup(@NotNull RV rootView, @NotNull NavigationContext<E, DC> context) {
        return ViewSet.from();
    }

    @NotNull
    @Override
    public Runnable dataBind(@NotNull NavigationContext<E, DC> context, @NotNull ViewVoid views) {
        return emptyRunnable;
    }

    @NotNull
    @Override
    public Runnable start(@NotNull NavigationContext<E, DC> context) {
        return emptyRunnable;
    }

    private static class EmptyRunnable implements Runnable {
        @Override
        public void run() {

        }
    }
}
