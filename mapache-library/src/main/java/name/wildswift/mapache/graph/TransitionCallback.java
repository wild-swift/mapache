package name.wildswift.mapache.graph;


import org.jetbrains.annotations.NotNull;

import name.wildswift.mapache.viewsets.ViewSet;

public interface TransitionCallback<VS extends ViewSet> {
    void onTransitionEnded(@NotNull VS currentSet);
}