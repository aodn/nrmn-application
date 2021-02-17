package au.org.aodn.nrmn.restapi.requestcache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
class RequestScopeAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestScopeAspect.class);

    private final RequestCacheManager requestCacheManager;

    @Autowired
    RequestScopeAspect(RequestCacheManager requestCacheManager) {
        this.requestCacheManager = requestCacheManager;
    }

    @Around("@annotation(au.org.aodn.nrmn.restapi.requestcache.RequestCache)")
    Object processRequestCache(ProceedingJoinPoint pjp) throws Throwable {
        InvocationTarget invocationTarget = new InvocationTarget(
                pjp.getSignature().getDeclaringType(),
                pjp.getSignature().getName(),
                pjp.getArgs()
        );
        Optional<Object> cachedResult = requestCacheManager.get(invocationTarget);
        if (cachedResult.isPresent()) {
            Object result = cachedResult.get();
            LOGGER.info("Using cached value {}, for invocation: {}", result, invocationTarget);
            return result;
        } else {
            Object methodResult = pjp.proceed();
            LOGGER.info("Caching result: {}, for invocation: {}", methodResult, invocationTarget);
            requestCacheManager.put(invocationTarget, methodResult);
            return methodResult;
        }
    }
}
