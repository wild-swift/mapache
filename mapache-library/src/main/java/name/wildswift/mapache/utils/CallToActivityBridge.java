package name.wildswift.mapache.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import name.wildswift.mapache.contextintegration.ActivityCaller;
import name.wildswift.mapache.contextintegration.ActivityEventsCallback;
import name.wildswift.mapache.events.Eventer;
import name.wildswift.mapache.osintegration.PermissionsCallback;
import name.wildswift.mapache.osintegration.SystemCalls;

public class CallToActivityBridge {
    private final Eventer eventer;
    private final SystemCalls systemCaller = new SystemCallsInternal();
    private final ActivityEventsCallbackInternal activityEventsCallback = new ActivityEventsCallbackInternal();
    private final Object runTasksMutex = new Object();
    private final ArrayList<Runnable> tasksQueue = new ArrayList<>();
    private Map<Integer, PermissionsCallback> permissionsCallbacks = new ConcurrentHashMap<>();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private ActivityCaller activityCaller = null;
    private int requestActivityCounter = 1;

    public CallToActivityBridge(Eventer eventer) {
        this.eventer = eventer;
    }

    @NotNull
    public SystemCalls getSystemCaller() {
        return systemCaller;
    }

    public void attachToActivity(ActivityCaller caller) {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("attachToActivity should be run on main thread");
        if (activityCaller != null) {
            activityCaller.removeEventsCallback(activityEventsCallback);
        }
        this.activityCaller = caller;
        if (activityCaller != null) {
            activityCaller.registerEventsCallback(activityEventsCallback);
        }
        processQueue();
    }

    public void detachFromActivity() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("detachFromActivity should be run on main thread");
        if (activityCaller != null) {
            activityCaller.removeEventsCallback(activityEventsCallback);
        }
        activityCaller = null;
    }


    private void processQueue() {
        if (Looper.myLooper() != Looper.getMainLooper()) throw new IllegalArgumentException("attachToActivity should be run on main thread");
        while(tasksQueue.size() > 0 && activityCaller != null) {
            synchronized (runTasksMutex) {
                if (tasksQueue.size() > 0 || activityCaller != null) {
                    Runnable runnable = tasksQueue.get(0);
                    tasksQueue.remove(0);
                    runnable.run();
                }
            }
        }
    }

    private class ActivityEventsCallbackInternal implements ActivityEventsCallback {
        @Override
        public void onBackPressed() {
            if (!eventer.onBack()) {
                activityCaller.finish();
            }
        }

        @Override
        public void onPermissionsResult(int code, Map<String, Integer> result) {
            PermissionsCallback permissionsCallback = permissionsCallbacks.get(code);
            if (permissionsCallback != null) {
                permissionsCallback.onPermissionsResponse(result);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }
    }

    private class SystemCallsInternal implements SystemCalls {

        @Override
        public void requestPermissions(final String[] permissions, final PermissionsCallback callback) {
            synchronized (runTasksMutex) {
                tasksQueue.add(new RequestPermissionsTask(callback, permissions));
            }
            if (Looper.myLooper() != Looper.getMainLooper())
                processQueue();
            else
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        processQueue();
                    }
                });
        }

        @Override
        public void startActivity(Intent intent) {
            // TODO implement
        }

        @Override
        public void startService(Class<? extends Service> serviceClass) {
            // TODO implement
        }

        @Override
        public void startService(Intent serviceClass) {
            // TODO implement
        }
    }

    private class RequestPermissionsTask implements Runnable {
        private final PermissionsCallback callback;
        private final String[] permissions;

        public RequestPermissionsTask(PermissionsCallback callback, String[] permissions) {
            this.callback = callback;
            this.permissions = permissions;
        }

        @Override
        public void run() {
            int code = requestActivityCounter++;
            permissionsCallbacks.put(code, callback);
            activityCaller.requestPermissions(permissions, code);

        }
    }
}
