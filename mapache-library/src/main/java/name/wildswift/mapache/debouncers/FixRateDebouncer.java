package name.wildswift.mapache.debouncers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FixRateDebouncer<T> implements Debouncer<T> {
    private ScheduledExecutorService internalExecutor;
    private long timeInMills;
    private List<DebounceCallback<T>> callbacks = new ArrayList<>();
    private volatile ScheduledFuture<?> future;
    private volatile T currentValue;

    public FixRateDebouncer(long timeInMills) {
        internalExecutor = Executors.newSingleThreadScheduledExecutor();
        this.timeInMills = timeInMills;
    }

    public FixRateDebouncer(ScheduledExecutorService executor, long debounceTime) {
        this.internalExecutor = executor;
        this.timeInMills = debounceTime;
    }

    @Override
    public void onNewValue(T value) {
        currentValue = value;
        if (future == null) {
            future = internalExecutor.schedule(new DeliverValue(), timeInMills, TimeUnit.MILLISECONDS);
        }
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
        @Override
        public void run() {
            ArrayList<DebounceCallback<T>> debounceCallbacks = new ArrayList<>(callbacks);
            future = null;
            for (DebounceCallback<T> callback : debounceCallbacks) {
                callback.newValue(currentValue);
            }

        }
    }
}
