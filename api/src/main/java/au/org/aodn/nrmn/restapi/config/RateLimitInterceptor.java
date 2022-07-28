package au.org.aodn.nrmn.restapi.config;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket newBucket(String apiKey) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(10))))
                .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // should be the value from X-Forwarded-For provided by ESB
        var ipAddress = request.getRemoteAddr(); 

        // no header provided so do not rate limit since we are not load balanced.
        if(ipAddress == null || 
        ipAddress.contentEquals("0:0:0:0:0:0:0:1") || 
        ipAddress.startsWith("172.") || 
        ipAddress.startsWith("192.") || 
        ipAddress.startsWith("10.")) {
            logger.info("local auth request " + ipAddress);
            return true;
        }

        logger.info("auth request from ip " + ipAddress);
        var bucket = cache.computeIfAbsent(ipAddress, this::newBucket);
        var probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed())
            return true;
        logger.info("rejecting rate limited ip " + ipAddress);
        var seconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
        response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(seconds));
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), String.valueOf(seconds));
        return false;
    }
}
