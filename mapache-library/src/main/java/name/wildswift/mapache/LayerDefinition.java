package name.wildswift.mapache;

import android.view.View;

import name.wildswift.mapache.events.Event;
import name.wildswift.mapache.graph.Navigatable;
import name.wildswift.mapache.states.MState;

public class LayerDefinition<E extends Event, DC, S extends MState<E, ?, ?, DC> & Navigatable<E, DC, S>> {
    private final S state;
    private final int contentId;

    public LayerDefinition(S state, int contentId) {
        this.state = state;
        this.contentId = contentId;
    }

    public S getState() {
        return state;
    }

    public int getContentId() {
        return contentId;
    }

    public LayerDefinition<E,DC,S> setState(S state) {
        return new LayerDefinition<>(state, contentId);
    }

    public LayerDefinition<E,DC,S> setContentId(int contentId) {
        return new LayerDefinition<>(state, contentId);
    }

    public S component1() {
        return state;
    }

    public int component2() {
        return contentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayerDefinition<?, ?, ?> that = (LayerDefinition<?, ?, ?>) o;

        if (contentId != that.contentId) return false;
        return state != null ? state.equals(that.state) : that.state == null;
    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;
        result = 31 * result + contentId;
        return result;
    }

    @Override
    public String toString() {
        return "LayerDefinition{" +
                "state=" + state +
                ", contentId=" + contentId +
                '}';
    }
}
