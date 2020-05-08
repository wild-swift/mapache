package name.wildswift.mapache.contextintegration;

import android.content.Intent;

public interface ActivityCaller {
    void registerEventsCallback(ActivityEventsCallback callback);
    void removeEventsCallback(ActivityEventsCallback callback);
    void finish();
    void requestPermissions(String[] permissions, int code);
    void startActivityForResult(Intent intent, int requestCode);

}