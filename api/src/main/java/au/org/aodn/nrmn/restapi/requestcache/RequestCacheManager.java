package au.org.aodn.nrmn.restapi.requestcache;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestCacheManager {

    private final Map<InvocationTarget, Object> cache = new ConcurrentHashMap<>();

    Optional<Object> get(InvocationTarget invocationContext) {
        return Optional.ofNullable(cache.get(invocationContext));
    }

    void put(InvocationTarget methodInvocation, Object result) {
        cache.put(methodInvocation, result);
    }
}
