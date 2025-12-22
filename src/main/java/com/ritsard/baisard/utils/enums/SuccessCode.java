package com.ritsard.baisard.utils.enums;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * CRUD-based Success Code Definitions
 *
 * <p>Messages support multi-language via messages.properties and messages_ko.properties:</p>
 * <ul>
 * <li>English (Default): success.select=Select Success</li>
 * <li>Korean: success.select=선택 성공</li>
 * </ul>
 *
 * <p>Success codes automatically mapped by HTTP methods:</p>
 * <ul>
 * <li>GET → SELECT_SUCCESS (200, "Select Success")</li>
 * <li>POST → INSERT_SUCCESS (201, "Insert Success")</li>
 * <li>PUT/PATCH → UPDATE_SUCCESS (200, "Update Success")</li>
 * <li>DELETE → DELETE_SUCCESS (204, "Delete Success")</li>
 * </ul>
 */
@Getter
public enum SuccessCode {
    SELECT_SUCCESS(200, "S001", "Select Success", "success.select"),
    INSERT_SUCCESS(201, "S002", "Insert Success", "success.insert"),
    UPDATE_SUCCESS(200, "S003", "Update Success", "success.update"),
    DELETE_SUCCESS(204, "S004", "Delete Success", "success.delete");

    private final int status;
    private final String code;
    private final String defaultMessage;    // Default message (English)
    private final String messageKey;        // Message key (for properties files)
    private static MessageSource messageSource;

    SuccessCode(int status, String code, String defaultMessage, String messageKey) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.messageKey = messageKey;
    }

    /**
     * Configure MessageSource (Required for multi-language support)
     *
     * <p>This is automatically called when the MessageSource Bean is created in the ApplicationContext.</p>
     *
     * @param source MessageSource Bean
     */
    public static void setMessageSource(MessageSource source) {
        messageSource = source;
    }

    /**
     * Returns the message corresponding to the current locale
     *
     * <p>Processing order:</p>
     * <ol>
     * <li>If MessageSource is configured, look up message in properties files</li>
     * <li>If the message for the current locale doesn't exist, return the default message (English)</li>
     * <li>If MessageSource is not configured, return the default message</li>
     * </ol>
     *
     * <p><strong>Example:</strong></p>
     * <ul>
     * <li>Korean Locale: "선택 성공"</li>
     * <li>English Locale: "Select Success"</li>
     * <li>Unsupported Locale: "Select Success" (Default)</li>
     * </ul>
     *
     * @return Success message matching the locale
     */
    public String getMessage() {
        if (messageSource != null) {
            try {
                return messageSource.getMessage(this.messageKey, null, LocaleContextHolder.getLocale());
            } catch (Exception e) {
                // Return default message if key is missing or an error occurs
                return this.defaultMessage;
            }
        }
        // Return default message if MessageSource is not set
        return this.defaultMessage;
    }

    /**
     * Returns the message for a specific locale
     *
     * @param locale The desired locale
     * @return Success message for the specified locale
     */
    public String getMessage(java.util.Locale locale) {
        if (messageSource != null) {
            try {
                return messageSource.getMessage(this.messageKey, null, locale);
            } catch (Exception e) {
                return this.defaultMessage;
            }
        }
        return this.defaultMessage;
    }

    /**
     * Returns the message key (for debugging purposes)
     *
     * @return Message key from properties file
     */
    public String getMessageKey() {
        return this.messageKey;
    }

    /**
     * Returns the default message (without multi-language processing)
     *
     * @return English default message
     */
    public String getDefaultMessage() {
        return this.defaultMessage;
    }
}