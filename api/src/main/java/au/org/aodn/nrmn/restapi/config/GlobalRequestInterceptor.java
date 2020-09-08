package au.org.aodn.nrmn.restapi.config;

import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
public class GlobalRequestInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(GlobalRequestInterceptor.class);
    private final String preHandleTemplate = "ID: ${id} Username: ${username} Path: ${path} Query: ${query} Method: ${method}";
    private final String postHandleTemplate = "ID: ${id} Handler-Rendering-Time: ${handler-rendering-time} ms";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object object) throws Exception {

        HttpServletRequest cachedRequest = new ContentCachingRequestWrapper(request);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String fishtag = UUID.randomUUID().toString();

        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("id", fishtag);
        valuesMap.put("username", username);
        valuesMap.put("path", cachedRequest.getRequestURI());
        valuesMap.put("query", cachedRequest.getQueryString() != null ? cachedRequest.getQueryString() : "None");
        valuesMap.put("method", cachedRequest.getMethod());

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        logger.info(sub.replace(preHandleTemplate));

        ThreadContext.put("fishtag", fishtag);
        ThreadContext.put("username", username);
        request.setAttribute("start-time", System.currentTimeMillis());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) {

        long handlerDuration =  System.currentTimeMillis() - (Long)request.getAttribute("start-time");

        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("id", ThreadContext.get("requestId"));
        valuesMap.put("handler-rendering-time", String.valueOf(handlerDuration));

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        logger.info(sub.replace(postHandleTemplate));
    }
}
