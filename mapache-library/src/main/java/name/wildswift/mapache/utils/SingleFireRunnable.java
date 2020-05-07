package name.wildswift.mapache.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public final class SingleFireRunnable implements Runnable {
    private final Runnable wrapped;
    private final AtomicBoolean fired = new AtomicBoolean(false);

    public SingleFireRunnable(Runnable wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void run() {
        if (!fired.compareAndSet(false, true)) return;
        wrapped.run();
    }
}
