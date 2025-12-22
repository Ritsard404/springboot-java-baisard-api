/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.json.JSONObject
 */
package com.ritsard.baisard.utils.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class AddressConverter {
    private static String KAKAO_GEOCODE_URL = "http://dapi.kakao.com/v2/local/search/address.json?query=";
    private static String KAKAO_GEOCODE_USER_INFO = "ac91ff5fba5326680dd65aea6add6342";

    public static Object strAddressToCoordinates(String address) {
        try {
            String inputLine;
            String strAddress = URLEncoder.encode(address, "UTF-8");
            String authToken = "KakaoAK " + KAKAO_GEOCODE_USER_INFO;
            URL url = new URL(KAKAO_GEOCODE_URL + strAddress);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", authToken);
            httpURLConnection.setRequestProperty("content-type", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDefaultUseCaches(false);
            Charset charset = Charset.forName("UTF-8");
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), charset));
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String jsonResponse = response.toString();
            Object addressGeoData = AddressConverter.parseCoordinatesFromJson(jsonResponse);
            return addressGeoData;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object parseCoordinatesFromJson(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            JsonNode documents = jsonNode.get("documents");
            if (documents.isArray() && documents.size() > 0) {
                JsonNode firstDocument = documents.get(0);
                Float longitude = Float.valueOf(Float.parseFloat(firstDocument.get("x").asText()));
                Float latitude = Float.valueOf(Float.parseFloat(firstDocument.get("y").asText()));
                JSONObject data = new JSONObject();
                data.put("latitude", (Object)latitude);
                data.put("longitude", (Object)longitude);
                return data;
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

