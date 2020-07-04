package name.wildswift.mapache.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class AndroidHandlerExecutor extends AbstractExecutorService implements ScheduledExecutorService {
    private volatile Handler handler;
    private volatile HandlerThread androidHandlerExecutor = null;
    private boolean shutdown = false;
    private final Object terminationMutex = new Object();
    private List<FutureTask<Void>> registeredTasks = new ArrayList<>();

    public AndroidHandlerExecutor(Handler handler) {
        this.handler = handler;
    }

    public AndroidHandlerExecutor() {
        androidHandlerExecutor = new HandlerThread("AndroidHandlerExecutor");
        androidHandlerExecutor.start();
        while (androidHandlerExecutor.getLooper() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
        }
        handler = new Handler(androidHandlerExecutor.getLooper());
    }

    @Override
    public void execute(Runnable command) {
        FutureTask<Void> task = new FutureTask<>(command, null);
        addRegisteredTask(task);
        handler.post(task);

    }

    @Override
    public void shutdown() {
        HandlerThread androidHandlerExecutor = this.androidHandlerExecutor;
        shutdown = true;
        if (androidHandlerExecutor != null) {
            androidHandlerExecutor.quitSafely();
        }
        synchronized (terminationMutex) {
            this.androidHandlerExecutor = null;
            terminationMutex.notifyAll();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        HandlerThread androidHandlerExecutor = this.androidHandlerExecutor;
        shutdown = true;
        if (androidHandlerExecutor != null) {
            androidHandlerExecutor.quit();
        }
        synchronized (terminationMutex) {
            this.androidHandlerExecutor = null;
            terminationMutex.notifyAll();
        }

        ArrayList<Runnable> result = new ArrayList<>();
        synchronized (this) {
            for (FutureTask<Void> futureTask : registeredTasks) {
                if (!futureTask.isCancelled() && !futureTask.isDone()) {
                    result.add(futureTask);
                }
            }
        }
        return result;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return androidHandlerExecutor == null;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (androidHandlerExecutor != null) {
            synchronized (terminationMutex) {
                if (androidHandlerExecutor != null) {
                    terminationMutex.wait(unit.toMillis(timeout));
                }
            }
        }
        return androidHandlerExecutor == null;
    }

    private synchronized void addRegisteredTask(FutureTask<Void> task) {
        registeredTasks.add(task);
        ArrayList<FutureTask<Void>> toDelete = new ArrayList<>();
        for (FutureTask<Void> registeredTask : registeredTasks) {
            if (registeredTask.isDone() || registeredTask.isCancelled()) toDelete.add(registeredTask);
        }
        for (FutureTask<Void> taskToDelete : toDelete) {
            registeredTasks.remove(taskToDelete);
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        if (command == null || unit == null) throw new NullPointerException();
        ScheduledFutureTask<Void> t = new ScheduledFutureTask<>(command, null, SystemClock.uptimeMillis() + unit.toMillis(delay));
        handler.postAtTime(t, t.time);
        return t;
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        if (callable == null || unit == null) throw new NullPointerException();
        ScheduledFutureTask<V> t = new ScheduledFutureTask<>(callable, SystemClock.uptimeMillis() + unit.toMillis(delay));
        handler.postAtTime(t, t.time);
        return t;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        if (command == null || unit == null) throw new NullPointerException();
        if (period <= 0L) throw new IllegalArgumentException();
        ScheduledFutureTask<Void> sft = new ScheduledFutureTask<>(command, null, SystemClock.uptimeMillis() + unit.toMillis(initialDelay), unit.toMillis(period));
        handler.postAtTime(sft, sft.time);
        return sft;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        if (command == null || unit == null) throw new NullPointerException();
        if (delay <= 0L) throw new IllegalArgumentException();

        ScheduledFutureTask<Void> sft = new ScheduledFutureTask<>(command, null, SystemClock.uptimeMillis() + unit.toMillis(initialDelay), -unit.toMillis(delay));
        handler.postAtTime(sft, sft.time);
        return sft;
    }

    private class ScheduledFutureTask<V>
            extends FutureTask<V> implements RunnableScheduledFuture<V> {

        private volatile long time;
        private final long period;
        ScheduledFutureTask(Runnable r, V result, long triggerTime) {
            super(r, result);
            this.time = triggerTime;
            this.period = 0;
        }

        /**
         * Creates a periodic action with given nanoTime-based initial
         * trigger time and period.
         */
        ScheduledFutureTask(Runnable r, V result, long triggerTime,
                            long period) {
            super(r, result);
            this.time = triggerTime;
            this.period = period;
        }

        /**
         * Creates a one-shot action with given nanoTime-based trigger time.
         */
        ScheduledFutureTask(Callable<V> callable, long triggerTime) {
            super(callable);
            this.time = triggerTime;
            this.period = 0;
        }

        public long getDelay(TimeUnit unit) {
            return unit.convert(time - System.nanoTime(), NANOSECONDS);
        }

        public int compareTo(Delayed other) {
            if (other == this) // compare zero if same object
                return 0;
            if (other instanceof ScheduledFutureTask) {
                ScheduledFutureTask<?> x = (ScheduledFutureTask<?>)other;
                long diff = time - x.time;
                if (diff < 0)
                    return -1;
                else if (diff > 0)
                    return 1;
                else
                    return 1;
            }
            long diff = getDelay(NANOSECONDS) - other.getDelay(NANOSECONDS);
            return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
        }

        /**
         * Returns {@code true} if this is a periodic (not a one-shot) action.
         *
         * @return {@code true} if periodic
         */
        public boolean isPeriodic() {
            return period != 0;
        }

        /**
         * Sets the next time to run for a periodic task.
         */
        private void setNextRunTime() {
            long p = period;
            if (p > 0)
                time += p;
            else
                time = SystemClock.uptimeMillis() - p;
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancelled = super.cancel(mayInterruptIfRunning);
            handler.removeCallbacks(this);
            return cancelled;
        }

        /**
         * Overrides FutureTask version so as to reset/requeue if periodic.
         */
        public void run() {
            boolean periodic = isPeriodic();
            if (!isShutdown())
                cancel(false);
            else if (!periodic)
                super.run();
            else if (super.runAndReset()) {
                setNextRunTime();
                handler.postAtTime(this, time);
            }
        }
    }

}
