package org.akquinet.audit;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation to say that the annotated element will maybe need user-interaction
 * @author immanuel
 *
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Interactive
{
}
