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
 * GitHub Organization Webhook Service
 * If required properties are missing, only this feature is disabled.
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
     * Called after the application context is fully initialized.
     * Ensures all beans are loaded before validating webhook configuration.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!initialized) {
            initialized = true;
            try {
                // Construct webhook URL if needed
                constructWebhookPayloadUrlIfNeeded();

                // Validate required settings and initialize webhook
                validateAndInitializeWebhook();
            } catch (Exception e) {
                // Errors here do not block application startup
                log.error("⚠️ Error initializing webhook service: {}", e.getMessage());
            }
        }
    }

    /**
     * Automatically constructs webhookPayloadUrl if deployServerDomain
     * and webhookReceiveEndpoint are provided.
     */
    private void constructWebhookPayloadUrlIfNeeded() {
        if (StringUtils.hasText(webhookPayloadUrl)) return;

        if (StringUtils.hasText(deployServerDomain) && StringUtils.hasText(webhookReceiveEndpoint)) {
            webhookPayloadUrl = "http://" + deployServerDomain + webhookReceiveEndpoint;
            log.info("📌 Constructed webhookPayloadUrl from deployServerDomain and webhookReceiveEndpoint: {}", webhookPayloadUrl);
        }
    }

    /**
     * Validates required properties and initializes the webhook if valid.
     */
    private void validateAndInitializeWebhook() {
        boolean isConfigValid = validateWebhookConfiguration();

        if (isConfigValid) {
            log.info("✅ GitHub webhook configuration is valid. Initializing webhook.");
            createOrUpdateOrganizationWebhook();
        } else {
            log.warn("⚠️ GitHub webhook configuration is invalid. Webhook feature is disabled, but other features will work.");
        }
    }

    /**
     * Checks if all required properties for webhook configuration are present.
     *
     * @return true if all required settings are valid
     */
    private boolean validateWebhookConfiguration() {
        boolean isValid = true;

        if (!StringUtils.hasText(githubToken)) {
            log.warn("⚠️ GitHub token is not set.");
            isValid = false;
        }

        if (!StringUtils.hasText(organization)) {
            log.warn("⚠️ GitHub organization is not set.");
            isValid = false;
        }

        if (!StringUtils.hasText(webhookPayloadUrl)) {
            log.warn("⚠️ Webhook URL is not set. Either set webhook.payload.url or both deploy.server.domain and webhook.receive.endpoint.");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Creates or updates the organization webhook.
     * Only called if the configuration is validated.
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

                        Integer hookId = null;
                        Object idObj = hook.get("id");
                        if (idObj != null) {
                            try {
                                hookId = Integer.valueOf(idObj.toString());
                            } catch (NumberFormatException e) {
                                log.error("❌ Webhook ID format error: {}", idObj);
                                continue;
                            }
                        }

                        if (hookId != null && config != null && webhookPayloadUrl.equals(config.get("url"))) {
                            log.info("✅ Existing organization webhook found. Updating: {}", webhookPayloadUrl);
                            updateOrganizationWebhook(hookId, webhookPayloadUrl);
                            return;
                        }
                    }
                }
            }

            log.info("⚡ Organization webhook does not exist. Creating a new webhook.");
            createOrganizationWebhook(webhookPayloadUrl);

        } catch (Exception e) {
            log.error("❌ Error fetching organization webhooks: {}", e.getMessage());
        }
    }

    private void updateOrganizationWebhook(Integer hookId, String webhookPayloadUrl) {
        String updateUrl = "https://api.github.com/orgs/" + organization + "/hooks/" + hookId;
        HttpHeaders headers = createHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(createWebhookBody(webhookPayloadUrl), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(updateUrl, HttpMethod.PATCH, requestEntity, String.class);
            log.info("✅ Organization webhook updated successfully: {}", response.getBody());
        } catch (Exception e) {
            log.error("❌ Error updating organization webhook: {}", e.getMessage());
        }
    }

    private void createOrganizationWebhook(String webhookPayloadUrl) {
        String createUrl = "https://api.github.com/orgs/" + organization + "/hooks";
        HttpHeaders headers = createHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(createWebhookBody(webhookPayloadUrl), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(createUrl, requestEntity, String.class);
            log.info("✅ Organization webhook created successfully: {}", response.getBody());
        } catch (Exception e) {
            log.error("❌ Error creating organization webhook: {}", e.getMessage());
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
