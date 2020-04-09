package name.wildswift.mapache.osintegration;

public interface SystemCalls {
    void requestPermissions(String[] permissions, PermissionsCallback callback);
}