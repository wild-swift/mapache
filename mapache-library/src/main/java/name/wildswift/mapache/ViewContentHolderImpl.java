package name.wildswift.mapache;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import name.wildswift.mapache.states.MState;
import name.wildswift.mapache.viewcontent.Initializable;
import name.wildswift.mapache.viewcontent.ViewContent;
import name.wildswift.mapache.viewcontent.ViewContentHolder;
import name.wildswift.mapache.viewcontent.ViewContentMeta;
import name.wildswift.mapache.viewcontent.ViewContentMetaSource;

class ViewContentHolderImpl<D, S extends MState<?, ?, ?, D>> implements ViewContentHolder {

    private final ViewContentMetaSource<S> stateSource;
    private final D diContext;

    private final List<ViewContentHolder> currentContentHolders = new ArrayList<>();

    ViewContentHolderImpl(ViewContentMetaSource<S> stateSource, D diContext) {
        this.stateSource = stateSource;
        this.diContext = diContext;
    }

    @SuppressWarnings("unchecked")
    void onNewState(S state) {
        Set<ViewContentMeta> objectsForState = stateSource.getObjectsForState(state);

        List<ViewContentHolder> toDelete = new ArrayList<>();
        for (ViewContentHolder viewContentHolder : currentContentHolders) {
            if (!objectsForState.contains(new ViewContentMeta(viewContentHolder.viewClass, viewContentHolder.clazz, viewContentHolder.name, false))) {
                toDelete.add(viewContentHolder);
            }
        }

        for (ViewContentHolder holder : toDelete) {
            currentContentHolders.remove(holder);
            if (holder.impl instanceof Initializable) {
                ((Initializable) holder.impl).close();
            }
        }

        for (ViewContentMeta meta : objectsForState) {
            int currentIndex = currentContentHolders.indexOf(new ViewContentHolder(meta.getViewClass(), meta.getClazz(), meta.getName(), false, null));
            if (currentIndex >= 0) {
                ViewContentHolder currentValue = currentContentHolders.get(currentIndex);
                if (currentValue.isDefault != meta.isDefault()) {
                    currentContentHolders.set(currentIndex, new ViewContentHolder(currentValue.viewClass, currentValue.clazz, currentValue.name, meta.isDefault(), currentValue.impl));
                }
            } else {
                ViewContent viewContent;
                try {
                    viewContent = (ViewContent) meta.getClazz().newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new IllegalStateException(e);
                }
                if(viewContent instanceof Initializable) {
                    ((Initializable<D>) viewContent).init(diContext);
                }
                currentContentHolders.add(new ViewContentHolder(meta.getViewClass(), meta.getClazz(), meta.getName(), meta.isDefault(), viewContent));
            }
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <V extends View, VS extends ViewContent<V>> VS getByView(@NonNull Class<V> clazz) {
        List<ViewContentHolder> candidates = new ArrayList<>();
        for (ViewContentHolder viewContentHolder : currentContentHolders) {
            if (viewContentHolder.viewClass == clazz) {
                if (viewContentHolder.isDefault) {
                    return (VS) viewContentHolder.impl;
                } else {
                    candidates.add(viewContentHolder);
                }
            }
        }
        if (candidates.size() == 1) return (VS) candidates.get(0).impl;
        for (ViewContentHolder viewContentHolder : candidates){
            if (viewContentHolder.name == null) return (VS) viewContentHolder.impl;
        }
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <VS extends ViewContent<?>> VS getByClass(@NonNull Class<VS> clazz) {
        List<ViewContentHolder> candidates = new ArrayList<>();
        for (ViewContentHolder viewContentHolder : currentContentHolders) {
            if (viewContentHolder.clazz == ((Class) clazz)) {
                if (viewContentHolder.isDefault) {
                    return (VS) viewContentHolder.impl;
                } else {
                    candidates.add(viewContentHolder);
                }
            }
        }
        if (candidates.size() == 1) return (VS) candidates.get(0).impl;
        for (ViewContentHolder viewContentHolder : candidates){
            if (viewContentHolder.name == null) return (VS) viewContentHolder.impl;
        }
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <VS extends ViewContent<?>> VS getByName(@NonNull Class<VS> clazz, @NonNull String name) {
        if (name == null) throw new NullPointerException();
        for (ViewContentHolder viewContentHolder : currentContentHolders) {
            if (viewContentHolder.clazz == (Class) clazz && name.equals(viewContentHolder.name)) return (VS) viewContentHolder.impl;
        }
        return null;
    }

    private static class ViewContentHolder {
        private final Class<View> viewClass;
        private final Class<ViewContent> clazz;
        private final String name;
        private final boolean isDefault;
        private final ViewContent impl;

        public ViewContentHolder(Class<View> viewClass, Class<ViewContent> clazz, String name, boolean isDefault, ViewContent impl) {
            this.viewClass = viewClass;
            this.clazz = clazz;
            this.name = name;
            this.isDefault = isDefault;
            this.impl = impl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ViewContentHolder that = (ViewContentHolder) o;

            if (!viewClass.equals(that.viewClass)) return false;
            if (!clazz.equals(that.clazz)) return false;
            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            int result = viewClass.hashCode();
            result = 31 * result + clazz.hashCode();
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
