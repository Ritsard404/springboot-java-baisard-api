/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ContextRefreshedEvent
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.stereotype.Component
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 *  org.springframework.web.client.RestTemplate
 */
package com.ritsard.baisard.utils.service.cicd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GitHub Organization Webhook 서비스
 * 필수 속성이 없으면 해당 기능만 비활성화됨
 */
@Slf4j
@Component
public class GitHubOrgWebhookService implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${github.token:}")
    private String githubToken;

    @Value("${github.organization:}")
    private String organization;

    @Value("${webhook.payload.url:}")
    private String webhookPayloadUrl;

    @Value("${webhook.receive.endpoint:}")
    private String webhookReceiveEndpoint;

    @Value("${deploy.server.domain:}")
    private String deployServerDomain;

    private final RestTemplate restTemplate = new RestTemplate();

    private boolean initialized = false;

    /**
     * 애플리케이션 컨텍스트가 완전히 초기화된 후 실행되어
     * 모든 빈이 로드된 상태에서 웹훅 설정을 검증합니다.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!initialized) {
            initialized = true;
            try {
                // deploy.server.domain과 webhook.receive.endpoint가 모두 있으면 webhook.payload.url 구성
                constructWebhookPayloadUrlIfNeeded();

                // 필수 설정이 있는지 확인하고, 있으면 웹훅 초기화
                validateAndInitializeWebhook();
            } catch (Exception e) {
                // 어떤 예외가 발생하더라도 애플리케이션 시작에 영향을 주지 않음
                log.error("⚠️ 웹훅 서비스 초기화 중 오류가 발생했습니다: {}", e.getMessage());
            }
        }
    }

    /**
     * deploy.server.domain과 webhook.receive.endpoint가 모두 있으면
     * webhook.payload.url을 자동으로 구성합니다.
     * 이 시점에서 webhookPayloadUrl이 이미 있다면 아무 것도 하지 않습니다.
     */
    private void constructWebhookPayloadUrlIfNeeded() {
        // webhook.payload.url이 이미 설정되어 있으면 아무것도 하지 않음
        if (StringUtils.hasText(webhookPayloadUrl)) {
            return;
        }

        // deploy.server.domain과 webhook.receive.endpoint가 모두 설정되어 있으면 webhook.payload.url 생성
        if (StringUtils.hasText(deployServerDomain) && StringUtils.hasText(webhookReceiveEndpoint)) {
            webhookPayloadUrl = "http://" + deployServerDomain + webhookReceiveEndpoint;
            log.info("📌 deploy.server.domain과 webhook.receive.endpoint에서 webhook.payload.url을 생성했습니다: {}", webhookPayloadUrl);
        }
    }

    /**
     * 웹훅 설정에 필요한 모든 속성을 검증하고 웹훅을 초기화합니다.
     */
    private void validateAndInitializeWebhook() {
        // 각 필수 속성들을 검증
        boolean isConfigValid = validateWebhookConfiguration();

        if (isConfigValid) {
            log.info("✅ GitHub 웹훅 설정이 유효합니다. 웹훅 초기화를 시작합니다.");
            createOrUpdateOrganizationWebhook();
        } else {
            log.warn("⚠️ GitHub 웹훅 설정이 유효하지 않아 웹훅 기능은 비활성화됩니다. 다른 기능은 정상적으로 작동합니다.");
        }
    }

    /**
     * 웹훅 설정에 필요한 모든 속성들을 검증합니다.
     *
     * @return 모든 필수 설정이 유효한 경우 true
     */
    private boolean validateWebhookConfiguration() {
        boolean isValid = true;

        // GitHub 토큰 검증
        if (!StringUtils.hasText(githubToken)) {
            log.warn("⚠️ GitHub 토큰이 설정되지 않았습니다.");
            isValid = false;
        }

        // GitHub 조직 이름 검증
        if (!StringUtils.hasText(organization)) {
            log.warn("⚠️ GitHub 조직 이름이 설정되지 않았습니다.");
            isValid = false;
        }

        // Webhook URL 검증 - constructWebhookPayloadUrlIfNeeded() 에서 이미 구성 시도했음
        if (!StringUtils.hasText(webhookPayloadUrl)) {
            log.warn("⚠️ Webhook URL이 설정되지 않았습니다. webhook.payload.url을 직접 설정하거나 deploy.server.domain과 webhook.receive.endpoint를 모두 설정해야 합니다.");
            isValid = false;
        }

        return isValid;
    }

    /**
     * 웹훅을 생성하거나 업데이트합니다.
     * 이 메서드는 validateWebhookConfiguration()에서 모든 설정이 유효하다고 검증된 후에만 호출됩니다.
     */
    public void createOrUpdateOrganizationWebhook() {
        String url = "https://api.github.com/orgs/" + organization + "/hooks";
        HttpHeaders headers = createHeaders();

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> hooks = response.getBody();
                if (hooks != null) {
                    for (Map<String, Object> hook : hooks) {
                        Map<String, Object> config = (Map<String, Object>) hook.get("config");

                        // 안전한 hookId 파싱
                        Integer hookId = null;
                        Object idObj = hook.get("id");
                        if (idObj != null) {
                            try {
                                hookId = Integer.valueOf(idObj.toString());
                            } catch (NumberFormatException e) {
                                log.error("❌ Webhook ID 형식 오류: {}", idObj);
                                continue;
                            }
                        }

                        if (hookId != null && config != null && webhookPayloadUrl.equals(config.get("url"))) {
                            log.info("✅ 기존 조직 웹훅이 존재함. 업데이트 진행: {}", webhookPayloadUrl);
                            updateOrganizationWebhook(hookId, webhookPayloadUrl);
                            return;
                        }
                    }
                }
            }

            log.info("⚡ 조직 웹훅이 존재하지 않음. 새로 생성합니다.");
            createOrganizationWebhook(webhookPayloadUrl);

        } catch (Exception e) {
            log.error("❌ 조직 웹훅 조회 중 오류 발생: {}", e.getMessage());
        }
    }

    private void updateOrganizationWebhook(Integer hookId, String webhookPayloadUrl) {
        String updateUrl = "https://api.github.com/orgs/" + organization + "/hooks/" + hookId;
        HttpHeaders headers = createHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(createWebhookBody(webhookPayloadUrl), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(updateUrl, HttpMethod.PATCH, requestEntity, String.class);
            log.info("✅ 조직 웹훅 업데이트 완료: {}", response.getBody());
        } catch (Exception e) {
            log.error("❌ 조직 웹훅 업데이트 중 오류 발생: {}", e.getMessage());
        }
    }

    private void createOrganizationWebhook(String webhookPayloadUrl) {
        String createUrl = "https://api.github.com/orgs/" + organization + "/hooks";
        HttpHeaders headers = createHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(createWebhookBody(webhookPayloadUrl), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(createUrl, requestEntity, String.class);
            log.info("✅ 조직 웹훅 생성 완료: {}", response.getBody());
        } catch (Exception e) {
            log.error("❌ 조직 웹훅 생성 중 오류 발생: {}", e.getMessage());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }

    private Map<String, Object> createWebhookBody(String webhookPayloadUrl) {
        Map<String, Object> config = new HashMap<>();
        config.put("url", webhookPayloadUrl);
        config.put("content_type", "json");
        config.put("insecure_ssl", "0");

        Map<String, Object> body = new HashMap<>();
        body.put("name", "web");
        body.put("active", true);
        body.put("events", List.of("push", "pull_request"));
        body.put("config", config);

        return body;
    }
}