package name.wildswift.mapache.viewcontent;

import androidx.annotation.NonNull;

import java.util.Set;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewsets.ViewSet;

public interface ViewContentMetaSource<NS extends MState> {
    @NonNull
    Set<ViewContentMeta> getObjectsForState(@NonNull Class<NS> stateClass);
}