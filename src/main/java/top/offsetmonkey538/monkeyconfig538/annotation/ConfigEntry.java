package top.offsetmonkey538.monkeyconfig538.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a config value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ConfigEntry {

    /**
     * The comment for the value.
     * @return the comment for the value.
     */
    String value() default "";
}
