package name.wildswift.mapache.viewcontent;

public class ViewContentMeta {
    private final Class<? extends ViewContent> clazz;
    private final String name;

    public ViewContentMeta(Class<? extends ViewContent> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class<? extends ViewContent> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public ViewContentMeta clazz(Class<? extends ViewContent> clazz) {
        return new ViewContentMeta(clazz, name);
    }

    public ViewContentMeta name(String name) {
        return new ViewContentMeta(clazz, name);
    }

    @Override
    public String toString() {
        return "ViewContentMeta(" +
                "clazz=" + clazz +
                ", name='" + name + '\'' +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewContentMeta that = (ViewContentMeta) o;

        if (!clazz.equals(that.clazz)) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = clazz.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
