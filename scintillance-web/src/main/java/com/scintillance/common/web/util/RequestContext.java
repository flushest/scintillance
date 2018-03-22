package com.scintillance.common.web.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2018/3/12 0012.
 */
public class RequestContext {
    private static final String ATTRIBUTES_USERID = "userId";

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return servletRequestAttributes.getRequest();
    }

    public static String currentUserId() {
        return (String) getRequest().getSession().getAttribute(ATTRIBUTES_USERID);
    }

    public static void setCurrentUserId(String userId) {
        getRequest().getSession().setAttribute(ATTRIBUTES_USERID, userId);
    }
}
