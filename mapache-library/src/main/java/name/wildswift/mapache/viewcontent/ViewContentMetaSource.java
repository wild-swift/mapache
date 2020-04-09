package name.wildswift.mapache.viewcontent;

import java.util.Set;

import name.wildswift.mapache.states.MState;

public interface ViewContentMetaSource<NS extends MState> {
    Set<ViewContentMeta> getObjectsForState(NS state);
}