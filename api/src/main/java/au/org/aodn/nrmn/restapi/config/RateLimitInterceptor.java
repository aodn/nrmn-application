package au.org.aodn.nrmn.restapi.config;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket newBucket(String apiKey) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(10))))
                .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        var details = (WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        var ipAddress = ((WebAuthenticationDetails) details).getRemoteAddress();
        var bucket = cache.computeIfAbsent(ipAddress, this::newBucket);
        var probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed())
            return true;
        var seconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
        response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(seconds));
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), String.valueOf(seconds));
        return false;
    }
}