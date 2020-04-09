package name.wildswift.mapache.osintegration;

import java.util.Map;

public interface PermissionsCallback {
    void onPermissionsResponse(Map<String, Integer> result);
}
