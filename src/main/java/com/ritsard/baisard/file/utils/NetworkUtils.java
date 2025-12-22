package com.ritsard.baisard.file.utils;

import java.net.InetAddress;

public class NetworkUtils {
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }

    public static int getServerPort() {
        return 8080;
    }
}

