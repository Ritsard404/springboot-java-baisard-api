package com.ritsard.baisard.jwt.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Generated;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FingerprintGeneratorImpl implements FingerprintGenerator {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(FingerprintGeneratorImpl.class);
//    private final ObjectMapper objectMapper;

    public Map<String, Object> generate(HttpServletRequest request) {
        Map<String, Object> fingerprint = new HashMap();
        fingerprint.put("userAgent", Optional.ofNullable(request.getHeader("User-Agent")).orElse(""));
        fingerprint.put("acceptLanguage", Optional.ofNullable(request.getHeader("Accept-Language")).orElse(""));
        fingerprint.put("acceptEncoding", Optional.ofNullable(request.getHeader("Accept-Encoding")).orElse(""));
        fingerprint.put("acceptCharset", Optional.ofNullable(request.getHeader("Accept-Charset")).orElse(""));
        log.debug(" Device fingerprint information generated");
        return fingerprint;
    }

    public String generateHash(HttpServletRequest request) {
//        try {
//            Map<String, Object> fingerprint = this.generate(request);
//            String json = this.objectMapper.writeValueAsString(fingerprint);
//            String hash = DigestUtils.sha256Hex(json);
//            log.debug("Device fingerprint hash generated: {}", hash.substring(0, 8) + "...");
//            return hash;
//        } catch (JsonProcessingException e) {
//            log.error("Fingerprint serialization failed", e);
//            throw new RuntimeException("Fingerprint serialization failed", e);
//        }
        return "";
    }
//
//    @Generated
//    public FingerprintGeneratorImpl(final ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }

}
