package name.wildswift.mapache.graph;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import name.wildswift.mapache.viewsets.ViewSet;

public interface TransitionCallback<VS extends ViewSet> {
    void onTransitionEnded(@NonNull VS currentSet);
}