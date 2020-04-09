package name.wildswift.mapache.events;

public interface SystemEventFactory<E extends Event> {
    E getBackEvent();
}