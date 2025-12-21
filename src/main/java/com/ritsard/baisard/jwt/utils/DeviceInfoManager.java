package com.ritsard.baisard.jwt.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface DeviceInfoManager {
    Map<String, Object> collectDeviceInfo(HttpServletRequest request);

    String createDeviceHash(Map<String, Object> deviceInfo);
}
