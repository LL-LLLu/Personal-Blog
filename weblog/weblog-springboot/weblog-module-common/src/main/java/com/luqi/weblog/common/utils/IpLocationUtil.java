package com.luqi.weblog.common.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * IP Location Utility
 * Uses MaxMind GeoIP2 for comprehensive global IP geolocation
 */
@Slf4j
public class IpLocationUtil {

    private static DatabaseReader geoIpReader;

    static {
        try {
            // Load GeoLite2-City.mmdb from classpath resources
            ClassPathResource resource = new ClassPathResource("geoip2/GeoLite2-City.mmdb");
            InputStream inputStream = resource.getInputStream();
            geoIpReader = new DatabaseReader.Builder(inputStream).build();
            log.info("MaxMind GeoIP2 database loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load MaxMind GeoIP2 database", e);
        }
    }

    /**
     * Location information holder
     */
    @Data
    public static class Location {
        private String country = "";
        private String countryCode = "";
        private String region = "";
        private String province = "";
        private String city = "";
        private String isp = "";
        private Double latitude;
        private Double longitude;

        public Location() {}

        public Location(String country, String countryCode, String province, String city) {
            this.country = country != null ? country : "";
            this.countryCode = countryCode != null ? countryCode : "";
            this.province = province != null ? province : "";
            this.city = city != null ? city : "";
        }
    }

    /**
     * Get location information from IP address using MaxMind GeoIP2
     * @param ip IP address
     * @return Location object containing country, province, city, etc.
     */
    public static Location getLocation(String ip) {
        Location location = new Location();

        if (ip == null || ip.isEmpty() || isLocalIp(ip)) {
            location.setCountry("Local");
            location.setProvince("Local");
            location.setCity("Local");
            return location;
        }

        // Try MaxMind GeoIP2 first
        if (geoIpReader != null) {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                CityResponse response = geoIpReader.city(ipAddress);

                // Get country info
                Country country = response.getCountry();
                if (country != null) {
                    // Prefer English name, fall back to Chinese name
                    String countryName = country.getName();
                    if (countryName == null || countryName.isEmpty()) {
                        countryName = country.getNames().get("zh-CN");
                    }
                    location.setCountry(countryName != null ? countryName : "");
                    location.setCountryCode(country.getIsoCode() != null ? country.getIsoCode() : "");
                }

                // Get province/state info
                if (!response.getSubdivisions().isEmpty()) {
                    Subdivision subdivision = response.getMostSpecificSubdivision();
                    String provinceName = subdivision.getName();
                    if (provinceName == null || provinceName.isEmpty()) {
                        provinceName = subdivision.getNames().get("zh-CN");
                    }
                    location.setProvince(provinceName != null ? provinceName : "");
                }

                // Get city info
                City city = response.getCity();
                if (city != null) {
                    String cityName = city.getName();
                    if (cityName == null || cityName.isEmpty()) {
                        cityName = city.getNames().get("zh-CN");
                    }
                    location.setCity(cityName != null ? cityName : "");
                }

                // Get coordinates
                if (response.getLocation() != null) {
                    location.setLatitude(response.getLocation().getLatitude());
                    location.setLongitude(response.getLocation().getLongitude());
                }

                log.debug("GeoIP2 lookup for {}: country={}, province={}, city={}",
                        ip, location.getCountry(), location.getProvince(), location.getCity());

            } catch (Exception e) {
                log.warn("GeoIP2 lookup failed for IP: {}, error: {}", ip, e.getMessage());
            }
        }

        return location;
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
