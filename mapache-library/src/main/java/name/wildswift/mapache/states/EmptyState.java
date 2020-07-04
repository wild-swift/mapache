package name.wildswift.mapache.states;

import android.view.View;

import androidx.annotation.NonNull;

import name.wildswift.mapache.NavigationContext;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.viewsets.ViewSet;
import name.wildswift.mapache.viewsets.ViewVoid;

class EmptyState<E extends Event, RV extends View, DC> implements MState<E, ViewVoid,RV,DC> {

    private EmptyRunnable emptyRunnable = new EmptyRunnable();

    @NonNull
    @Override
    public ViewVoid setup(@NonNull RV rootView, @NonNull NavigationContext<E, DC> context) {
        return ViewSet.from();
    }

    @NonNull
    @Override
    public Runnable dataBind(@NonNull NavigationContext<E, DC> context, @NonNull ViewVoid views) {
        return emptyRunnable;
    }

    @NonNull
    @Override
    public Runnable start(@NonNull NavigationContext<E, DC> context) {
        return emptyRunnable;
    }

    private static class EmptyRunnable implements Runnable {
        @Override
        public void run() {

        }
    }
}
