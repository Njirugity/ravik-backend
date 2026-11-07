package net.ravik_cms.ravik_backend.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declarative replacement for an imperative {@code authorizationService.authorize(...)} call.
 * <p>
 * Checks are run by {@link PermissionAspect} before the annotated method executes, using the
 * same project/user context {@link AuthorizationService} resolves.
 * <p>
 * Usage:
 * <pre>
 * {@code @RequirePermission("READ_BUDGET")}
 * public ResponseEntity&lt;X&gt; getFoo(...) { ... }
 *
 * {@code @RequirePermission({"READ_BUDGET", "READ_COST_SUMMARY"})} // AND by default
 * {@code @RequirePermission(value = {"READ_BUDGET", "READ_COST_SUMMARY"}, operation = Operation.OR)}
 * </pre>
 * Can be placed on a class to apply to every method in it; a method-level annotation overrides
 * the class-level one rather than combining with it.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /** Permission names to check, as registered in {@code PermissionsSeeder}. */
    String[] value();

    /** How multiple permissions are combined. Defaults to requiring all of them. */
    Operation operation() default Operation.AND;
}
