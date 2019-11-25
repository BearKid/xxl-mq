package com.xxl.mq.admin.extension.controller;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class RequestUtils {

    /**
     * 获取该请求来源IP
     */
    public String getIp(HttpServletRequest request) {
        final String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            return request.getRemoteAddr();
        } else {
            return ip;
        }
    }
}
