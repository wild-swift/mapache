package name.wildswift.mapache.graph;

import name.wildswift.mapache.viewsets.ViewSet;

public interface TransitionCallback<VS extends ViewSet> {
    void onTransitionEnded(VS currentSet);
}