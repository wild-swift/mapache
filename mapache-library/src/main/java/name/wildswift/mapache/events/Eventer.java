package name.wildswift.mapache.events;

import name.wildswift.mapache.events.Event;

public interface Eventer<E extends Event> {
    boolean onNewEvent(E event);
    boolean onBack();
}