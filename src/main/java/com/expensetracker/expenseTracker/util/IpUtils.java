package com.expensetracker.expenseTracker.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    private IpUtils() {}

    // Handles proxies, load balancers, and direct connections
    public static String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (isValidIp(ip)) {
            // X-Forwarded-For can contain multiple IPs — take the first one
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) return ip;

        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) return ip;

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) return ip;

        // Fall back to direct remote address
        return request.getRemoteAddr();
    }

    private static boolean isValidIp(String ip) {
        return ip != null
                && !ip.isBlank()
                && !"unknown".equalsIgnoreCase(ip);
    }
}