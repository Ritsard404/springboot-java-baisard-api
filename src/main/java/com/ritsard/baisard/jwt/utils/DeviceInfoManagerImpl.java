package com.ritsard.baisard.jwt.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class DeviceInfoManagerImpl implements DeviceInfoManager{
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DeviceInfoManagerImpl.class);

    public Map<String, Object> collectDeviceInfo(HttpServletRequest request) {
        Map<String, Object> deviceInfo = new HashMap();
        String deviceId = request.getHeader("X-Device-ID");
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = request.getSession().getId();
        }

        deviceInfo.put("deviceId", deviceId);
        deviceInfo.put("userAgent", request.getHeader("User-Agent"));
        deviceInfo.put("acceptLanguage", request.getHeader("Accept-Language"));
        deviceInfo.put("accept", request.getHeader("Accept"));
        return deviceInfo;
    }

    public String createDeviceHash(Map<String, Object> deviceInfo) {
        try {
            StringBuilder sb = new StringBuilder();
            deviceInfo.forEach((key, value) -> {
                if (value != null) {
                    sb.append(key).append("=").append(value).append(";");
                }

            });
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("디바이스 해시 생성 중 오류 발생 / Error occurred while creating device hash", e);
            String combined = (String)deviceInfo.values().stream().filter((v) -> v != null).map(Object::toString).reduce("", (a, b) -> a + b);
            return Integer.toHexString(combined.hashCode());
        }
    }
}
