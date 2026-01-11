package com.ritsard.baisard.utils.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ritsard.baisard.utils.enums.ErrorCode;
import com.ritsard.baisard.utils.enums.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * Standardized API Response Wrapper
 * * This class provides a consistent structure for all API responses,
 * including success data and error details.
 *
 * @param <T> The type of the data payload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "success", "status", "resultMsg", "divisionCode", "errorId", "data" }) // Add this line
public class ApiResponse<T> {

    // Indicates if the operation was successful (status < 400)
    private boolean success;

    // HTTP Status Code (e.g., 200, 400, 500)
    private int status;

    // Descriptive message about the result
    private String resultMsg;

    // Internal application division/error code (e.g., "A101", "S001")
    private String divisionCode;

    // The actual data being returned
    private T data;

    // Unique ID for tracking specific error instances in logs
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorId;

    /**
     * Internal constructor used by static factory methods.
     * Logic for 'success' boolean is determined by the HTTP status code.
     */
    public ApiResponse(int status, String resultMsg, String divisionCode, T data) {
        this.success = status < 400;
        this.status = status;
        this.resultMsg = resultMsg;
        this.divisionCode = divisionCode;
        this.data = data;
    }

    // =========================================================================
    // SUCCESS RESPONSES
    // =========================================================================

    /**
     * Standard 200 OK response with data.
     */
    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.status = 200;
        response.resultMsg = null;
        response.data = data;
        return response;
    }

    /**
     * Standard 200 OK response with no data payload.
     */
    public static <T> ApiResponse<T> ok() {
        return ok(null);
    }

    /**
     * 200 OK response with a custom success message.
     */
    public static <T> ApiResponse<T> ok(T data, String customMessage) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.status = 200;
        response.resultMsg = customMessage;
        response.data = data;
        return response;
    }

    /**
     * Specialized response for Paged data.
     * Converts a Spring Data 'Page' object into a 'PagedResponseDto' for clean JSON output.
     */
    public static <T> ApiResponse<PagedResponseDto<T>> okPage(java.util.List<T> content, Page<?> originalPage) {
        PageInfo pageInfo = new PageInfo(
                originalPage.getTotalElements(),
                originalPage.getTotalPages(),
                originalPage.getNumber(),
                originalPage.getSize(),
                originalPage.isFirst(),
                originalPage.isLast(),
                originalPage.hasNext(),
                originalPage.hasPrevious()
        );

        PagedResponseDto<T> pagedData = new PagedResponseDto<>(content, pageInfo);

        ApiResponse<PagedResponseDto<T>> response = new ApiResponse<>();
        response.success = true;
        response.status = 200;
        response.resultMsg = null;
        response.data = pagedData;
        return response;
    }

    // Aliases for 'ok' methods to support legacy 'success' naming convention
    public static <T> ApiResponse<T> success(T data) {
        return ok(data);
    }

    public static <T> ApiResponse<T> success() {
        return ok();
    }

    public static <T> ApiResponse<T> success(T data, String customMessage) {
        return ok(data, customMessage);
    }

    /**
     * Success response using a predefined SuccessCode enum.
     */
    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return new ApiResponse<>(successCode.getStatus(), successCode.getMessage(), successCode.getCode(), data);
    }

    /**
     * Success response using a predefined SuccessCode enum with no data.
     */
    public static <T> ApiResponse<T> success(SuccessCode successCode) {
        return new ApiResponse<>(successCode.getStatus(), successCode.getMessage(), successCode.getCode(), null);
    }

    // =========================================================================
    // ERROR RESPONSES
    // =========================================================================

    /**
     * Error response using a predefined ErrorCode.
     * Allows returning partial data alongside an error if necessary.
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), errorCode.getDivisionCode(), data);
    }

    /**
     * Error response with a custom message that overrides the default ErrorCode message.
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.status = errorCode.getStatus();
        response.resultMsg = customMessage;
        response.divisionCode = errorCode.getDivisionCode();
        return response;
    }

    /**
     * Error response with a custom message and a unique Error ID (for log tracing).
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage, String errorId) {
        ApiResponse<T> response = error(errorCode, customMessage);
        response.errorId = errorId;
        return response;
    }
}
