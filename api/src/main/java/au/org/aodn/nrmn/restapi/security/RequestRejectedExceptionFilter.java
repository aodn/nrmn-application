package au.org.aodn.nrmn.restapi.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestRejectedExceptionFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestRejectedExceptionFilter.class);

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        try {
            filterChain.doFilter(request, response);
        } catch (RequestRejectedException e) {

            logger.warn("RequestRejectedException: ip={}, request_url={}, message={}",
                    request.getRemoteHost(), request.getRequestURL(), e.getMessage());

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }

}
