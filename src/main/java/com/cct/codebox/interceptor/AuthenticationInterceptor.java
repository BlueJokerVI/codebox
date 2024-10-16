package com.cct.codebox.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求认证拦截
 * @author cct
 */
@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Value("${codebox.auth.header}")
    private String authRequestHeader;

    @Value("${codebox.auth.secret}")
    private String authRequestSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String headerValue = request.getHeader(authRequestHeader);
        if (!StringUtils.equals(headerValue, authRequestSecret)) {
            log.warn("Unauthorized request: invalid header value");
            // 返回 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
