package name.wildswift.mapache.graph;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import name.wildswift.mapache.states.MState;

public class BackStackEntry<NS extends MState<?, ?, ?, ?>> {
    private final Class<NS> stateWrapperClass;
    private final Object[] parameters;

    public BackStackEntry(Class<NS> stateWrapperClass, Serializable[] parameters) {
        this.stateWrapperClass = stateWrapperClass;
        this.parameters = parameters;
    }

    @SuppressWarnings("unchecked")
    public NS createInstance() {
        try {
            Class[] parameterClasses = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parameterClasses[i] = parameters[i].getClass();
            }
            Method method = stateWrapperClass.getMethod("newInstance", parameterClasses);
            return (NS) method.invoke(null, parameters);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
            throw new IllegalStateException(e);
        }
    }
}
