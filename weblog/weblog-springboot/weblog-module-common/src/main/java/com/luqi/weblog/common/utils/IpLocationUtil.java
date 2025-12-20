package com.luqi.weblog.common.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * IP Location Utility
 * Uses ip2region library to get geographic location from IP address
 */
@Slf4j
public class IpLocationUtil {

    private static Searcher searcher;

    static {
        try {
            // Load ip2region.xdb from classpath resources
            ClassPathResource resource = new ClassPathResource("ip2region/ip2region.xdb");
            InputStream inputStream = resource.getInputStream();
            byte[] dbBytes = FileCopyUtils.copyToByteArray(inputStream);
            searcher = Searcher.newWithBuffer(dbBytes);
            log.info("ip2region database loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load ip2region database", e);
        }
    }

    /**
     * Location information holder
     */
    @Data
    public static class Location {
        private String country = "";
        private String region = "";
        private String province = "";
        private String city = "";
        private String isp = "";

        public Location() {}

        public Location(String country, String region, String province, String city, String isp) {
            this.country = country != null ? country : "";
            this.region = region != null ? region : "";
            this.province = province != null ? province : "";
            this.city = city != null ? city : "";
            this.isp = isp != null ? isp : "";
        }
    }

    /**
     * Get location information from IP address
     * @param ip IP address
     * @return Location object containing country, province, city, etc.
     */
    public static Location getLocation(String ip) {
        Location location = new Location();

        if (searcher == null) {
            log.warn("ip2region searcher not initialized");
            return location;
        }

        if (ip == null || ip.isEmpty() || isLocalIp(ip)) {
            location.setCountry("Local");
            location.setProvince("Local");
            location.setCity("Local");
            return location;
        }

        try {
            // ip2region returns format: "国家|区域|省份|城市|ISP"
            // Example: "中国|0|北京|北京市|电信"
            String result = searcher.search(ip);
            if (result != null && !result.isEmpty()) {
                String[] parts = result.split("\\|");
                if (parts.length >= 5) {
                    location.setCountry(cleanValue(parts[0]));
                    location.setRegion(cleanValue(parts[1]));
                    location.setProvince(cleanValue(parts[2]));
                    location.setCity(cleanValue(parts[3]));
                    location.setIsp(cleanValue(parts[4]));
                }
            }
        } catch (Exception e) {
            log.error("Failed to get location for IP: {}", ip, e);
        }

        return location;
    }

    /**
     * Clean value - replace "0" with empty string
     */
    private static String cleanValue(String value) {
        if (value == null || "0".equals(value)) {
            return "";
        }
        return value.trim();
    }

    /**
     * Check if IP is a local/private IP address
     */
    public static boolean isLocalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return true;
        }
        return ip.startsWith("127.")
            || ip.startsWith("192.168.")
            || ip.startsWith("10.")
            || ip.startsWith("172.16.")
            || ip.startsWith("172.17.")
            || ip.startsWith("172.18.")
            || ip.startsWith("172.19.")
            || ip.startsWith("172.20.")
            || ip.startsWith("172.21.")
            || ip.startsWith("172.22.")
            || ip.startsWith("172.23.")
            || ip.startsWith("172.24.")
            || ip.startsWith("172.25.")
            || ip.startsWith("172.26.")
            || ip.startsWith("172.27.")
            || ip.startsWith("172.28.")
            || ip.startsWith("172.29.")
            || ip.startsWith("172.30.")
            || ip.startsWith("172.31.")
            || "0:0:0:0:0:0:0:1".equals(ip)
            || "::1".equals(ip)
            || "localhost".equalsIgnoreCase(ip);
    }

    /**
     * Get real IP address from HTTP request
     * Handles proxies and load balancers (including AWS ELB/ALB)
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;

        // Try to get IP from various headers (in order of preference)
        // AWS ALB/ELB headers are included
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP",
            "CF-Connecting-IP",     // Cloudflare
            "True-Client-IP",       // Cloudflare Enterprise
            "X-Client-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Cluster-Client-IP"
        };

        for (String header : headers) {
            ip = request.getHeader(header);
            log.debug("Header {} = {}", header, ip);
            if (isValidIp(ip)) {
                // X-Forwarded-For may contain multiple IPs, take the first one (original client)
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                log.info("Got IP from header {}: {}", header, ip);
                break;
            }
        }

        // Fall back to remote address
        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
            log.info("Using remote address: {}", ip);
        }

        return ip;
    }

    /**
     * Check if IP string is valid (not null, not empty, not "unknown")
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}
