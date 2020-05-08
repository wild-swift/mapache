package name.wildswift.mapache.contextintegration;

import android.content.Intent;

import java.util.Map;

public interface ActivityEventsCallback {
    void onBackPressed();
    void onPermissionsResult(int code, Map<String, Integer> result);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}