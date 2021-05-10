package net.vanderkast.fs4r.domain.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Declares domain-level operations constructed for concurrent execution.</p>
 * <p>
 * Directly called overridden operations executes in parallel.
 * That means no guarantees are not provided about synchronization, atomicity, consistency, etc.
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ConcurrentDomain {
}
