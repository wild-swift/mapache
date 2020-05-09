package name.wildswift.mapache.debouncers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SkipWhileRunningDebouncer<T> implements Debouncer<T> {
    private ExecutorService internalExecutor;
    private long timeInMills;
    private Future<?> future;
    private List<DebounceCallback<T>> callbacks = new ArrayList<>();

    public SkipWhileRunningDebouncer(long timeInMills) {
        internalExecutor = Executors.newSingleThreadScheduledExecutor();
        this.timeInMills = timeInMills;
    }

    public SkipWhileRunningDebouncer(ExecutorService executor, long debounceTime) {
        this.internalExecutor = executor;
        this.timeInMills = debounceTime;
    }

    @Override
    public void onNewValue(final T value) {
        onNewValue(new JustCallable(value));
    }

    @Override
    public void onNewValue(Callable<T> value) {
        if (!future.isDone()) return;
        future = internalExecutor.submit(new DeliverValue(value));
    }

    @Override
    public void addCallback(DebounceCallback<T> callback) {
        callbacks.add(callback);
    }

    private class DeliverValue implements Runnable {
        private final Callable<T> value;

        private DeliverValue(Callable<T> value) {
            this.value = value;
        }

        @Override
        public void run() {
            T call = null;
            try {
                call = value.call();
            } catch (Exception e) {
                // log error
            }
            ArrayList<DebounceCallback<T>> debounceCallbacks = new ArrayList<>(callbacks);
            for (DebounceCallback<T> callback : debounceCallbacks) {
                callback.newValue(call);
            }

        }
    }

    private class JustCallable implements Callable<T> {
        private final T value;

        private JustCallable(T value) {
            this.value = value;
        }

        @Override
        public T call() throws Exception {
            return value;
        }
    }
}
