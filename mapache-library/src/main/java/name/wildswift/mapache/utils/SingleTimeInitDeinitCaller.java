package name.wildswift.mapache.utils;

import java.util.concurrent.Callable;

public class SingleTimeInitDeinitCaller {
    private final Object callMutex = new Object();
    private final Callable<Runnable> initMethod;
    private Runnable deinitCall;
    private boolean deinitCallSuccess = false;

    public SingleTimeInitDeinitCaller(Callable<Runnable> initMethod) {
        this.initMethod = initMethod;
    }

    public void init() {
        if (deinitCallSuccess || deinitCall != null) return;
        synchronized (callMutex) {
            if (deinitCallSuccess || deinitCall != null) return;
            try {
                deinitCall = initMethod.call();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public void deinit() {
        if (deinitCallSuccess) return;
        synchronized (callMutex) {
            if (deinitCallSuccess) return;
            if (deinitCall != null) {
                deinitCall.run();
            }
            deinitCallSuccess = true;
        }
    }

}
