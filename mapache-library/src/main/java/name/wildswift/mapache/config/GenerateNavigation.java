package name.wildswift.mapache.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface GenerateNavigation {
    /**
     * Name Prefix for Generated classes
     * @return namt prefix
     */
    String value();
    String configName() default "mapache";
    ConfigType type() default ConfigType.GROOVY;
}
