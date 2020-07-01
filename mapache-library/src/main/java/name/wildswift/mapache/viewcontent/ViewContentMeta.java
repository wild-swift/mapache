package name.wildswift.mapache.viewcontent;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViewContentMeta {
    private final Class<View> viewClass;
    private final Class<ViewContent> clazz;
    private final String name;
    private final boolean isDefault;


    public ViewContentMeta(@NonNull Class<View> viewClass, @NonNull Class<ViewContent> clazz, @Nullable String name, boolean isDefault) {
        this.viewClass = viewClass;
        this.clazz = clazz;
        this.name = name;
        this.isDefault = isDefault;
    }

    public Class<View> getViewClass() {
        return viewClass;
    }

    public Class<ViewContent> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public ViewContentMeta viewClass(Class<View> viewClass) {
        return new ViewContentMeta(viewClass, clazz, name, isDefault);
    }

    public ViewContentMeta clazz(Class<ViewContent> clazz) {
        return new ViewContentMeta(viewClass, clazz, name, isDefault);
    }

    public ViewContentMeta name(String name) {
        return new ViewContentMeta(viewClass, clazz, name, isDefault);
    }

    public ViewContentMeta isDefault(boolean isDefault) {
        return new ViewContentMeta(viewClass, clazz, name, isDefault);
    }

    @Override
    public String toString() {
        return "ViewContentMeta(" +
                "viewClass=" + viewClass +
                ", clazz=" + clazz +
                ", name='" + name + '\'' +
                ", isDefault=" + isDefault +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewContentMeta that = (ViewContentMeta) o;

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
