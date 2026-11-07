package net.ravik_cms.ravik_backend.authorization;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.AccessDeniedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Intercepts methods (or classes) annotated with {@link RequirePermission} and runs the
 * equivalent {@link AuthorizationService#authorize(String)} check(s) before the method body runs.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
    private final AuthorizationService authorizationService;

    /**
     * Around advice triggered for any method annotated with {@link RequirePermission}, or any
     * method in a class annotated with it.
     *
     * @param joinPoint the intercepted method call
     * @return the result of the intercepted method, once the permission check passes
     * @throws Throwable whatever the intercepted method itself throws, or
     *                    {@link AccessDeniedException} if the caller lacks the required permission(s)
     */
    @Around("@within(net.ravik_cms.ravik_backend.authorization.RequirePermission) || " +
            "@annotation(net.ravik_cms.ravik_backend.authorization.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        RequirePermission methodAnnotation = getMethodAnnotation(joinPoint);
        RequirePermission effective = methodAnnotation != null ? methodAnnotation : getClassAnnotation(joinPoint);

        if (effective != null) {
            validate(effective);
        }

        return joinPoint.proceed();
    }

    /**
     * Reads the {@link RequirePermission} annotation directly off the invoked method, if present.
     *
     * @param joinPoint the intercepted method call
     * @return the method-level annotation, or {@code null} if the method isn't annotated
     */
    private RequirePermission getMethodAnnotation(ProceedingJoinPoint joinPoint) {
        try {
            Method method = joinPoint.getTarget().getClass()
                    .getMethod(joinPoint.getSignature().getName(),
                            ((MethodSignature) joinPoint.getSignature()).getParameterTypes());
            return method.getAnnotation(RequirePermission.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Reads the {@link RequirePermission} annotation off the invoked method's declaring class, if present.
     *
     * @param joinPoint the intercepted method call
     * @return the class-level annotation, or {@code null} if the class isn't annotated
     */
    private RequirePermission getClassAnnotation(ProceedingJoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getAnnotation(RequirePermission.class);
    }

    /**
     * Runs the underlying {@link AuthorizationService#authorize(String)} check for every
     * permission named in the annotation, combined per its {@link Operation}.
     *
     * @param annotation the effective {@link RequirePermission} to enforce
     * @throws AccessDeniedException if the AND/OR condition over the listed permissions isn't met
     */
    private void validate(RequirePermission annotation) {
        String[] required = annotation.value();

        if (annotation.operation() == Operation.AND) {
            for (String permission : required) {
                authorizationService.authorize(permission);
            }
            return;
        }

        AccessDeniedException lastDenial = null;
        for (String permission : required) {
            try {
                authorizationService.authorize(permission);
                return;
            } catch (AccessDeniedException e) {
                lastDenial = e;
            }
        }
        if (lastDenial != null) {
            throw new AccessDeniedException(
                    "Access Denied, one of the following permissions is required: " + Arrays.toString(required));
        }
    }
}
