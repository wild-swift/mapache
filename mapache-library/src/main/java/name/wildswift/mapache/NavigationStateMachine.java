package name.wildswift.mapache;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import name.wildswift.mapache.utils.CallToActivityBridge;
import name.wildswift.mapache.debouncers.CancelableDebouncer;
import name.wildswift.mapache.debouncers.DebounceCallback;
import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.graph.Navigatable;
import name.wildswift.mapache.contextintegration.ActivityCaller;
import name.wildswift.mapache.graph.TransitionFactory;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.utils.AndroidHandlerExecutor;
import name.wildswift.mapache.utils.LayerStateMachineProcessor;
import name.wildswift.mapache.utils.ViewContentHolderImpl;
import name.wildswift.mapache.viewcontent.ViewContentMetaSource;

public final class NavigationStateMachine<E extends Event, DC, S extends MState<E, ?, ?, DC> & Navigatable<E, DC, S>> {
    private final List<LayerHolder> layerProcessors;
    private final CallToActivityBridge callToActivityBridge;

    private boolean isPaused = false;

    private CancelableDebouncer<Boolean> startStopDebouncer = new CancelableDebouncer<>(new AndroidHandlerExecutor(new Handler(Looper.getMainLooper())), 500);


    @SuppressWarnings("unchecked")
    public NavigationStateMachine(List<LayerDefinition<E,DC,S>> layes, TransitionFactory<E, DC, ?> transFactory, ViewContentMetaSource<S> metaSource, DC diContext) {
        EventerInternal eventerInternal = new EventerInternal();
        this.callToActivityBridge = new CallToActivityBridge(eventerInternal);
        name.wildswift.mapache.utils.ViewContentHolderImpl<DC, S> viewsContents = new ViewContentHolderImpl<>(metaSource, diContext);
        NavigationContext<E, DC> navigationContext = new NavigationContext<>(diContext, eventerInternal, viewsContents, callToActivityBridge.getSystemCaller());

        this.layerProcessors = new ArrayList<>();

        for (LayerDefinition<E, DC, S> layerDefinition : layes) {
            layerProcessors.add(new LayerHolder(
                    new LayerStateMachineProcessor(layerDefinition.getState(), transFactory, navigationContext, viewsContents),
                    layerDefinition.getContentId()
            ));
        }

        startStopDebouncer.addCallback(new ChangePauseStateListener());
    }

    @SuppressWarnings("unchecked")
    public void attachToActivity(Activity activity, ActivityCaller caller) {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("attachToActivity should be run on main thread");
        callToActivityBridge.attachToActivity(caller);
        for (LayerHolder layerProcessor : layerProcessors) {
            layerProcessor.processor.attachToRoot(activity.findViewById(layerProcessor.contentId));
        }
    }

    public void resume() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("resume should be run on main thread");
        if (isPaused) {
            startStopDebouncer.onNewValue(false);
        } else {
            startStopDebouncer.cancel();
        }
    }

    public void pause() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("pause should be run on main thread");
        if (!isPaused) {
            startStopDebouncer.onNewValue(true);
        } else {
            startStopDebouncer.cancel();
        }
    }

    public void detachFromActivity() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("detachFromActivity should be run on main thread");
        for (LayerHolder layerProcessor : layerProcessors) {
            layerProcessor.processor.detachFromRoot();
        }
        callToActivityBridge.detachFromActivity();
    }

    private void stopStateMachineExecution() {
        // TODO need implement this logic
    }

    private void resumeStateMachineExecution() {
        // TODO need implement this logic
    }

    private class EventerInternal implements Eventer<E> {
        @Override
        public boolean onNewEvent(E event) {
            return NavigationStateMachine.this.onNewEvent(event);
        }

        @Override
        public boolean onBack() {
            return NavigationStateMachine.this.onBack();
        }
    }

    private boolean onBack() {
        for (int i = layerProcessors.size() - 1; i >= 0; i--) {
            LayerStateMachineProcessor layerProcessor = layerProcessors.get(i).processor;
            if (layerProcessor.onBack()) return true;
        }
        for (LayerHolder layerProcessor : layerProcessors) {
            layerProcessor.processor.reset();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean onNewEvent(E event) {
        for (int i = layerProcessors.size() - 1; i >= 0; i--) {
            LayerStateMachineProcessor layerProcessor = layerProcessors.get(i).processor;
            if (layerProcessor.onNewEvent(event)) return true;
        }
        return false;
    }

    private class ChangePauseStateListener implements DebounceCallback<Boolean> {
        @Override
        public void newValue(final Boolean value) {
            if(isPaused == value) return;
            isPaused = value;
            if (isPaused) {
                stopStateMachineExecution();
            } else {
                resumeStateMachineExecution();
            }
        }
    }

    private static class LayerHolder {
        private final LayerStateMachineProcessor processor;
        private final int contentId;

        public LayerHolder(LayerStateMachineProcessor processor, int contentId) {
            this.processor = processor;
            this.contentId = contentId;
        }
    }
}
