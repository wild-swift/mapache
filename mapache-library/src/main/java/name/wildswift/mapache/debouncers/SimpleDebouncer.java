package name.wildswift.mapache.debouncers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SimpleDebouncer<T> implements Debouncer<T> {
    private ScheduledExecutorService internalExecutor;
    private long timeInMills;
    private ScheduledFuture<?> future;
    private List<DebounceCallback<T>> callbacks = new ArrayList<>();

    public SimpleDebouncer(long timeInMills) {
        internalExecutor = Executors.newSingleThreadScheduledExecutor();
        this.timeInMills = timeInMills;
    }

    public SimpleDebouncer(ScheduledExecutorService executor, long debounceTime) {
        this.internalExecutor = executor;
        this.timeInMills = debounceTime;
    }

    @Override
    public void onNewValue(T value) {
        if (future != null) {
            future.cancel(false);
        }
        future = internalExecutor.schedule(new DeliverValue(value), timeInMills, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onNewValue(Callable<T> value) {
        try {
            onNewValue(value.call());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void addCallback(DebounceCallback<T> callback) {
        callbacks.add(callback);
    }

    private class DeliverValue implements Runnable {
        private final T value;

        private DeliverValue(T value) {
            this.value = value;
        }

        @Override
        public void run() {
            ArrayList<DebounceCallback<T>> debounceCallbacks = new ArrayList<>(callbacks);
            for (DebounceCallback<T> callback : debounceCallbacks) {
                callback.newValue(value);
            }

        }
    }
}
