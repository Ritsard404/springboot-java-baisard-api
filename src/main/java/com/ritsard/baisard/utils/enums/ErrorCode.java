package com.ritsard.baisard.utils.enums;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Global Application Error Code Definitions
 *
 * Each error code contains the following information:
 * 1. HTTP Status Code - The HTTP status to be returned to the client
 * 2. Error Code - Internal application error code (for documentation and reference)
 * 3. Message Key - The key used to look up messages in multi-language message resources
 *
 * Error Code Prefix Rules:
 * - E: General Errors
 * - A: Authentication/Authorization Errors
 * - V: Validation Errors
 * - D: Data Errors
 * - S: System/Service Errors
 * - G: GPT/External Service Errors
 * - C: Encryption/Security Errors
 */
@Getter
public enum ErrorCode {

    //===================================
    // General Errors (E000-E099)
    //===================================
    BAD_REQUEST_ERROR(400, "E001", "error.badRequest"),
    NOT_FOUND_ERROR(404, "E002", "error.notFound"),
    INTERNAL_SERVER_ERROR(500, "E003", "error.internalServer"),
    TIMEOUT_ERROR(408, "E004", "error.timeout"),
    CONFLICT_ERROR(409, "E005", "error.conflict"),
    IO_ERROR(500, "E006", "error.io"),

    //===================================
    // Authentication/Authorization Errors (A100-A199)
    //===================================
    LOGIN_ERROR(401, "A100", "error.login"),
    UNAUTHORIZED_ERROR(401, "A101", "error.unauthorized"),
    ACCESS_DENIED(403, "A102", "error.accessDenied"),
    PERMISSION_DENIED(403, "A103", "error.permissionDenied"),
    INVALID_TOKEN_ERROR(401, "A104", "error.invalidToken"),
    EXPIRED_TOKEN_ERROR(401, "A105", "error.expiredToken"),
    JWT_TOKEN_NOT_VALID_ERROR(401, "A106", "error.invalidToken"),
    SESSION_EXPIRED_ERROR(401, "A107", "error.sessionExpired"),
    INVALID_CREDENTIALS_ERROR(401, "A108", "error.invalidCredentials"),
    ACCOUNT_LOCKED_ERROR(403, "A109", "error.accountLocked"),
    ACCOUNT_DISABLED_ERROR(403, "A110", "error.accountDisabled"),
    INVALID_PASSWORD_ERROR(401, "A111", "error.invalidPassword"),
    AUTHENTICATION_ERROR(401, "A112", "error.authenticationFailed"),
    EMAIL_NOT_VERIFIED_ERROR(400, "A113", "error.emailNotVerified"),
    PHONE_NOT_VERIFIED_ERROR(400, "A114", "error.phoneNotVerified"),
    FORBIDDEN_ERROR(403, "A115", "error.forbidden"),
    REQUEST_PASS_AUTH_ERROR(500, "A116", "error.requestPassAuth"),
    PASS_AUTH_CALLBACK_ERROR(500, "A117", "error.passAuthCallback"),

    //===================================
    // Data Related Errors (D200-D299)
    //===================================
    DUPLICATE_ERROR(409, "D200", "error.duplicate"),
    NO_SUCH_ELEMENT(404, "D201", "error.noSuchElement"),
    NO_SUCH_USER_ERROR(404, "D202", "error.noSuchUser"),
    SELECT_ERROR(500, "D203", "error.select"),
    INSERT_ERROR(500, "D204", "error.insert"),
    UPDATE_ERROR(500, "D205", "error.update"),
    DELETE_ERROR(500, "D206", "error.delete"),
    IDENTIFIER_DUPLICATED_ERROR(409, "D207", "error.identifierDuplicated"),
    RESOURCE_LOCKED_ERROR(423, "D208", "error.resourceLocked"),
    OUT_OF_STOCK_ERROR(409, "D209", "error.outOfStock"),
    CART_EMPTY_ERROR(400, "D210", "error.cartEmpty"),
    // Database related errors
    DATABASE_ERROR(500, "D211", "error.database"),
    SQL_ERROR(500, "D212", "error.sql"),
    DATA_TRUNCATION_ERROR(400, "D213", "error.dataTruncation"),
    FOREIGN_KEY_VIOLATION_ERROR(400, "D214", "error.foreignKeyViolation"),
    UNIQUE_CONSTRAINT_ERROR(409, "D215", "error.uniqueConstraint"),
    CONNECTION_ERROR(500, "D216", "error.databaseConnection"),
    TRANSACTION_ERROR(500, "D217", "error.transaction"),

    //===================================
    // Validation Errors (V300-V399)
    //===================================
    VALIDATION_ERROR(422, "V300", "error.validation"),
    METHOD_ARGUMENT_NOT_VALID_ERROR(422, "V301", "error.methodArgumentNotValid"),
    NOT_VALID_HEADER_ERROR(400, "V302", "error.notValidHeader"),
    MISSING_REQUEST_PARAMETER_ERROR(400, "V303", "error.missingRequestParameter"),
    REQUEST_BODY_MISSING_ERROR(400, "V304", "error.requestBodyMissing"),
    PARAMETERS_ARE_NOT_EXIST(404, "V305", "error.parametersAreNotExist"),
    JSON_PARSE_ERROR(400, "V306", "error.jsonParse"),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED_ERROR(415, "V307", "error.httpMediaTypeNotSupported"),
    IMAGE_FILE_TOO_BIG_ERROR(413, "V308", "error.imageFileTooBig"),
    CAPTCHA_INVALID_ERROR(400, "V309", "error.captchaInvalid"),
    FieldNotFoundException(400, "V310", "error.fieldNotFoundException"),
    INVALID_FILE_TYPE(415, "V311", "error.invalidFileType"),
    METHOD_ARGUMENT_TYPE_MISMATCH_ERROR(400, "V312", "error.methodArgumentTypeMismatch"),

    //===================================
    // System/Service Errors (S400-S499)
    //===================================
    NULL_POINT_ERROR(500, "S400", "error.nullPointer"),
    UPLOAD_ERROR(500, "S401", "error.upload"),
    DOWNLOAD_ERROR(500, "S402", "error.download"),
    SEND_EMAIL_ERROR(500, "S403", "error.sendEmail"),
    SIGNUP_ERROR(400, "S404", "error.signup"),
    CHANGE_PASSWORD_ERROR(400, "S405", "error.changePassword"),
    RESET_PASSWORD_ERROR(400, "S406", "error.resetPassword"),
    ACTIVATE_ACCOUNT_ERROR(400, "S407", "error.activateAccount"),
    DEACTIVATE_ACCOUNT_ERROR(400, "S408", "error.deactivateAccount"),
    OPERATION_FAILED_ERROR(500, "S409", "error.operationFailed"),
    ORDER_FAILED_ERROR(500, "S410", "error.orderFailed"),
    PAYMENT_ERROR(402, "S411", "error.payment"),
    INSUFFICIENT_FUNDS_ERROR(402, "S412", "error.insufficientFunds"),
    LIMIT_EXCEEDED_ERROR(429, "S413", "error.limitExceeded"),
    PROCESSOR_ERROR(500, "S414", "error.processorError"),

    //===================================
    // GPT/External Service Errors (G500-G599)
    //===================================
    GPT_UNKNOWN_ERROR(500, "G500", "error.gpt.unknown"),
    INVALID_API_KEY(401, "G501", "error.gpt.invalidApiKey"),
    RATE_LIMIT_EXCEEDED(429, "G502", "error.gpt.rateLimitExceeded"),
    TOKEN_LIMIT_EXCEEDED(400, "G503", "error.gpt.tokenLimitExceeded"),
    CONTENT_FILTERED(400, "G504", "error.gpt.contentFiltered"),
    API_TIMEOUT(408, "G505", "error.gpt.timeout"),
    BAD_GATEWAY(502, "G506", "error.gpt.badGateway"),
    SERVICE_UNAVAILABLE(503, "G507", "error.gpt.serviceUnavailable"),
    API_SERVER_ERROR(500, "G508", "error.gpt.serverError"),
    API_UNAUTHORIZED(401, "G509", "error.gpt.unauthorized"),
    API_FORBIDDEN(403, "G510", "error.gpt.forbidden"),
    RESOURCE_NOT_FOUND(404, "G511", "error.gpt.resourceNotFound"),
    API_BAD_REQUEST(400, "G512", "error.gpt.badRequest"),
    NETWORK_ERROR(500, "G513", "error.gpt.networkError"),
    API_JSON_PARSING_ERROR(500, "G514", "error.gpt.jsonParsingError"),

    //===================================
    // Encryption/Security Errors (C600-C699)
    //===================================
    // Bidirectional Encryption
    ENCRYPTION_ERROR(500, "C600", "error.encryption"),
    DECRYPTION_ERROR(500, "C601", "error.decryption"),
    CRYPTO_KEY_ERROR(500, "C602", "error.cryptoKey"),

    // One-way Hashing
    PASSWORD_HASHING_ERROR(500, "C603", "error.passwordHashing"),
    PASSWORD_VERIFICATION_ERROR(400, "C604", "error.passwordVerification"),

    // Java Standard Crypto Exceptions
    INVALID_KEY_ERROR(400, "C605", "error.invalidKey"),
    INVALID_KEY_SPEC_ERROR(400, "C606", "error.invalidKeySpec"),
    UNSUPPORTED_ALGORITHM_ERROR(400, "C607", "error.unsupportedAlgorithm"),
    BAD_PADDING_ERROR(400, "C608", "error.badPadding"),
    ILLEGAL_BLOCK_SIZE_ERROR(400, "C609", "error.illegalBlockSize"),
    INVALID_ALGORITHM_PARAMETER_ERROR(400, "C610", "error.invalidAlgorithmParameter"),

    // Crypto-specific Situations
    CRYPTO_MEMORY_ERROR(503, "C611", "error.cryptoMemory"),
    CRYPTO_PARAMETER_ERROR(400, "C612", "error.cryptoParameter"),
    CRYPTO_VERSION_ERROR(500, "C613", "error.cryptoVersion"),
    WEAK_PASSWORD_ERROR(400, "C614", "error.weakPassword");


    private final int status;
    private final String divisionCode;
    private final String messageKey;
    private static MessageSource messageSource;

    ErrorCode(final int status, final String divisionCode, final String messageKey) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.messageKey = messageKey;
    }

    /**
     * Set the message source
     * Configured by MessageSourceConfig during application startup
     */
    public static void setMessageSource(MessageSource source) {
        messageSource = source;
    }

    /**
     * Returns the error message according to the current locale
     * @return Localized error message
     */
    public String getMessage() {
        return messageSource.getMessage(this.messageKey, null, LocaleContextHolder.getLocale());
    }

    /**
     * Returns a localized error message using specified arguments
     * @param args Arguments to be used in the message template
     * @return Localized error message containing the arguments
     */
    public String getMessage(Object... args) {
        return messageSource.getMessage(this.messageKey, args, LocaleContextHolder.getLocale());
    }

    /**
     * Check the error code category
     * @return Category string (e.g., "General", "Authentication", "Data", "Validation", "System", "GPT", "Security")
     */
    public String getCategory() {
        char firstChar = this.divisionCode.charAt(0);
        switch (firstChar) {
            case 'E': return "General";
            case 'A': return "Authentication";
            case 'D': return "Data";
            case 'V': return "Validation";
            case 'S': return "System";
            case 'G': return "GPT";
            case 'C': return "Security";
            default: return "Unknown";
        }
    }

    /**
     * Returns the severity level of the error code
     * Severity is determined based on the HTTP status code
     * @return Severity level string ("Critical", "Major", "Minor", "Info")
     */
    public String getSeverity() {
        if (status >= 500) return "Critical";
        if (status >= 400) return "Major";
        if (status >= 300) return "Minor";
        return "Info";
    }
}
