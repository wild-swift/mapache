package name.wildswift.mapache.osintegration;

import android.app.Service;
import android.content.Intent;

public interface SystemCalls {
    void requestPermissions(String[] permissions, PermissionsCallback callback);

    void startActivity(Intent intent);
    void startService(Class<? extends Service> serviceClass);
    void startService(Intent serviceClass);
}