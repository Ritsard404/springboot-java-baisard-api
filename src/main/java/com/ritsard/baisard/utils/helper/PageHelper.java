/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 */
package com.ritsard.baisard.utils.helper;

import com.ritsard.baisard.utils.dto.PageInfo;
import com.ritsard.baisard.utils.dto.PagedResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for pagination and DTO conversion
 *
 * <p>Converts Entity to DTO while creating responses that include pagination information,
 * or performs general List conversions.</p>
 *
 * <p>Supports two conversion methods:</p>
 * <ul>
 *   <li>Function passing: Directly pass a conversion function in the form of {@code MessageDto::from}</li>
 *   <li>Reflection: Pass a DTO class in the form of {@code MessageDto.class} to automatically discover the {@code from()} method</li>
 * </ul>
 *
 * @author lodong-utils-module
 * @version 1.0
 * @since 1.0
 */
public class PageHelper {

    /**
     * Creates a paged response (function passing method)
     *
     * <p>Takes a Page&lt;Entity&gt;, converts each Entity to a DTO,
     * and returns a response object in Map form with pagination information.</p>
     *
     * <p><strong>Usage example:</strong></p>
     * <pre>
     * {@code
     * Page<Message> messagePage = messageRepository.findAll(pageable);
     * Object pageData = PageHelper.toPageResponse(messagePage, MessageDto::from);
     * return ApiResponse.ok(pageData);
     * }
     * </pre>
     *
     * @param <E>        Entity type
     * @param <D>        DTO type
     * @param entityPage Page&lt;Entity&gt; object to convert
     * @param converter  Function to convert Entity to DTO (e.g., {@code MessageDto::from})
     * @return Map object containing pagination information
     * <ul>
     *   <li>{@code content}: Converted DTO List</li>
     *   <li>{@code totalElements}: Total number of elements</li>
     *   <li>{@code totalPages}: Total number of pages</li>
     *   <li>{@code currentPage}: Current page (starts from 0)</li>
     *   <li>{@code pageSize}: Page size</li>
     *   <li>{@code first}: Whether it's the first page</li>
     *   <li>{@code last}: Whether it's the last page</li>
     *   <li>{@code hasNext}: Whether next page exists</li>
     *   <li>{@code hasPrevious}: Whether previous page exists</li>
     * </ul>
     * @throws RuntimeException if an error occurs during conversion
     */
    public static <E, D> Object toPageResponse(Page<E> entityPage, Function<E, D> converter) {
        List<D> dtoList = entityPage.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());

        return buildPageResponse(dtoList, entityPage);
    }

    /**
     * Creates a paged response (reflection method)
     *
     * <p>Takes a Page&lt;Entity&gt;, automatically discovers the {@code from()} method of the DTO class
     * to convert each Entity to a DTO, and returns a response object in Map form with pagination information.</p>
     *
     * <p><strong>DTO class requirements:</strong></p>
     * <ul>
     *   <li>Must have a {@code public static DTO from(Entity entity)} method</li>
     *   <li>Method must have exactly 1 parameter</li>
     * </ul>
     *
     * <p><strong>Usage example:</strong></p>
     * <pre>
     * {@code
     * Page<Message> messagePage = messageRepository.findAll(pageable);
     * Object pageData = PageHelper.toPageResponse(messagePage, MessageDto.class);
     * return ApiResponse.ok(pageData);
     * }
     * </pre>
     *
     * @param <E>        Entity type
     * @param <D>        DTO type
     * @param entityPage Page&lt;Entity&gt; object to convert
     * @param dtoClass   DTO class (e.g., {@code MessageDto.class})
     * @return Map object containing pagination information (structure is the same as the function passing method)
     * @throws RuntimeException if the DTO class doesn't have a {@code from()} method or conversion fails
     * @see #toPageResponse(Page, Function)
     */
    @SuppressWarnings("unchecked")
    public static <E, D> Object toPageResponse(Page<E> entityPage, Class<D> dtoClass) {
        try {
            Method fromMethod = findFromMethod(dtoClass);

            List<D> dtoList = entityPage.getContent().stream()
                    .map(entity -> {
                        try {
                            return (D) fromMethod.invoke(null, entity);
                        } catch (Exception e) {
                            throw new RuntimeException("DTO Conversion failed: " + entity.getClass().getSimpleName() +
                                    " -> " + dtoClass.getSimpleName(), e);
                        }
                    })
                    .collect(Collectors.toList());

            return buildPageResponse(dtoList, entityPage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create page response: " + e.getMessage(), e);
        }
    }

    /**
     * Converts Entity List to DTO List (function passing method)
     *
     * <p>Converts a general List&lt;Entity&gt; (not paged) to List&lt;DTO&gt;.</p>
     *
     * <p><strong>Usage example:</strong></p>
     * <pre>
     * {@code
     * List<Message> messages = messageRepository.findTop10ByOrderByCreatedAtDesc();
     * List<MessageDto> dtoList = PageHelper.toList(messages, MessageDto::from);
     * return ApiResponse.ok(dtoList);
     * }
     * </pre>
     *
     * @param <E>        Entity type
     * @param <D>        DTO type
     * @param entityList Entity List to convert
     * @param converter  Function to convert Entity to DTO (e.g., {@code MessageDto::from})
     * @return Converted DTO List
     * @throws RuntimeException if an error occurs during conversion
     */
    public static <E, D> List<D> toList(List<E> entityList, Function<E, D> converter) {
        return entityList.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    /**
     * Converts Entity List to DTO List (reflection method)
     *
     * <p>Converts a general List&lt;Entity&gt; (not paged) to List&lt;DTO&gt;
     * by automatically discovering the {@code from()} method of the DTO class.</p>
     *
     * <p><strong>DTO class requirements:</strong></p>
     * <ul>
     *   <li>Must have a {@code public static DTO from(Entity entity)} method</li>
     *   <li>Method must have exactly 1 parameter</li>
     * </ul>
     *
     * <p><strong>Usage example:</strong></p>
     * <pre>
     * {@code
     * List<Message> messages = messageRepository.findTop10ByOrderByCreatedAtDesc();
     * List<MessageDto> dtoList = PageHelper.toList(messages, MessageDto.class);
     * return ApiResponse.ok(dtoList);
     * }
     * </pre>
     *
     * @param <E>        Entity type
     * @param <D>        DTO type
     * @param entityList Entity List to convert
     * @param dtoClass   DTO class (e.g., {@code MessageDto.class})
     * @return Converted DTO List
     * @throws RuntimeException if the DTO class doesn't have a {@code from()} method or conversion fails
     * @see #toList(List, Function)
     */
    @SuppressWarnings("unchecked")
    public static <E, D> List<D> toList(List<E> entityList, Class<D> dtoClass) {
        try {
            Method fromMethod = findFromMethod(dtoClass);

            return entityList.stream()
                    .map(entity -> {
                        try {
                            return (D) fromMethod.invoke(null, entity);
                        } catch (Exception e) {
                            throw new RuntimeException("DTO conversion failed", e);
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("List conversion failed: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a paged response structure
     *
     * <p>Takes the converted DTO List and the original Page object to create a Map containing pagination information.</p>
     *
     * @param <D>     DTO type
     * @param content Converted DTO List
     * @param page    Original Page object (for extracting pagination metadata)
     * @return Map object containing pagination information
     */
    private static <D> PagedResponseDto<D> buildPageResponse(List<D> content, Page<?> page) {
        PageInfo pageInfo = new PageInfo(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );

        return new PagedResponseDto<>(content, pageInfo);
    }


    /**
     * Finds the {@code from()} method in the DTO class
     *
     * <p>Uses reflection to search for a method that satisfies the following conditions:</p>
     * <ul>
     *   <li>Method name is "from"</li>
     *   <li>Is a static method</li>
     *   <li>Has exactly 1 parameter</li>
     * </ul>
     *
     * @param dtoClass DTO class
     * @return The found {@code from()} method
     * @throws RuntimeException if a {@code from()} method satisfying the conditions cannot be found
     */
    private static Method findFromMethod(Class<?> dtoClass) {
        Method[] methods = dtoClass.getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals("from") &&
                    java.lang.reflect.Modifier.isStatic(method.getModifiers()) &&
                    method.getParameterCount() == 1) {

                return method;
            }
        }

        throw new RuntimeException(dtoClass.getSimpleName() + " does not have a static from() method.");
    }
}