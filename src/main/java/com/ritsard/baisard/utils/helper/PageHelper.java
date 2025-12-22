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
 * 페이징 및 DTO 변환을 지원하는 유틸리티 클래스
 *
 * <p>Entity에서 DTO로 변환하면서 페이징 정보를 포함한 응답을 생성하거나,
 * 일반 List 변환을 수행합니다.</p>
 *
 * <p>두 가지 변환 방식을 지원합니다:</p>
 * <ul>
 *   <li>함수 전달 방식: {@code MessageDto::from} 형태로 변환 함수를 직접 전달</li>
 *   <li>리플렉션 방식: {@code MessageDto.class} 형태로 DTO 클래스를 전달하여 {@code from()} 메서드를 자동 탐색</li>
 * </ul>
 *
 * @author lodong-utils-module
 * @version 1.0
 * @since 1.0
 */
public class PageHelper {

    /**
     * 페이징 응답을 생성합니다 (함수 전달 방식)
     *
     * <p>Page&lt;Entity&gt;를 받아서 각 Entity를 DTO로 변환하고,
     * 페이징 정보와 함께 Map 형태의 응답 객체를 반환합니다.</p>
     *
     * <p><strong>사용 예시:</strong></p>
     * <pre>
     * {@code
     * Page<Message> messagePage = messageRepository.findAll(pageable);
     * Object pageData = PageHelper.toPageResponse(messagePage, MessageDto::from);
     * return ApiResponse.ok(pageData);
     * }
     * </pre>
     *
     * @param <E>        Entity 타입
     * @param <D>        DTO 타입
     * @param entityPage 변환할 Page&lt;Entity&gt; 객체
     * @param converter  Entity를 DTO로 변환하는 함수 (예: {@code MessageDto::from})
     * @return 페이징 정보가 포함된 Map 객체
     * <ul>
     *   <li>{@code content}: 변환된 DTO List</li>
     *   <li>{@code totalElements}: 전체 요소 수</li>
     *   <li>{@code totalPages}: 전체 페이지 수</li>
     *   <li>{@code currentPage}: 현재 페이지 (0부터 시작)</li>
     *   <li>{@code pageSize}: 페이지 크기</li>
     *   <li>{@code first}: 첫 페이지 여부</li>
     *   <li>{@code last}: 마지막 페이지 여부</li>
     *   <li>{@code hasNext}: 다음 페이지 존재 여부</li>
     *   <li>{@code hasPrevious}: 이전 페이지 존재 여부</li>
     * </ul>
     * @throws RuntimeException 변환 과정에서 오류가 발생한 경우
     */
    public static <E, D> Object toPageResponse(Page<E> entityPage, Function<E, D> converter) {
        List<D> dtoList = entityPage.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());

        return buildPageResponse(dtoList, entityPage);
    }

    /**
     * 페이징 응답을 생성합니다 (리플렉션 방식)
     *
     * <p>Page&lt;Entity&gt;를 받아서 DTO 클래스의 {@code from()} 메서드를 자동으로 탐색하여
     * 각 Entity를 DTO로 변환하고, 페이징 정보와 함께 Map 형태의 응답 객체를 반환합니다.</p>
     *
     * <p><strong>DTO 클래스 요구사항:</strong></p>
     * <ul>
     *   <li>{@code public static DTO from(Entity entity)} 메서드가 존재해야 함</li>
     *   <li>메서드는 정확히 1개의 파라미터를 가져야 함</li>
     * </ul>
     *
     * <p><strong>사용 예시:</strong></p>
     * <pre>
     * {@code
     * Page<Message> messagePage = messageRepository.findAll(pageable);
     * Object pageData = PageHelper.toPageResponse(messagePage, MessageDto.class);
     * return ApiResponse.ok(pageData);
     * }
     * </pre>
     *
     * @param <E>        Entity 타입
     * @param <D>        DTO 타입
     * @param entityPage 변환할 Page&lt;Entity&gt; 객체
     * @param dtoClass   DTO 클래스 (예: {@code MessageDto.class})
     * @return 페이징 정보가 포함된 Map 객체 (구조는 함수 전달 방식과 동일)
     * @throws RuntimeException DTO 클래스에 {@code from()} 메서드가 없거나 변환 실패 시
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
                            throw new RuntimeException("DTO 변환 실패: " + entity.getClass().getSimpleName() +
                                    " -> " + dtoClass.getSimpleName(), e);
                        }
                    })
                    .collect(Collectors.toList());

            return buildPageResponse(dtoList, entityPage);

        } catch (Exception e) {
            throw new RuntimeException("페이지 응답 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Entity List를 DTO List로 변환합니다 (함수 전달 방식)
     *
     * <p>페이징이 아닌 일반 List&lt;Entity&gt;를 List&lt;DTO&gt;로 변환합니다.</p>
     *
     * <p><strong>사용 예시:</strong></p>
     * <pre>
     * {@code
     * List<Message> messages = messageRepository.findTop10ByOrderByCreatedAtDesc();
     * List<MessageDto> dtoList = PageHelper.toList(messages, MessageDto::from);
     * return ApiResponse.ok(dtoList);
     * }
     * </pre>
     *
     * @param <E>        Entity 타입
     * @param <D>        DTO 타입
     * @param entityList 변환할 Entity List
     * @param converter  Entity를 DTO로 변환하는 함수 (예: {@code MessageDto::from})
     * @return 변환된 DTO List
     * @throws RuntimeException 변환 과정에서 오류가 발생한 경우
     */
    public static <E, D> List<D> toList(List<E> entityList, Function<E, D> converter) {
        return entityList.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    /**
     * Entity List를 DTO List로 변환합니다 (리플렉션 방식)
     *
     * <p>페이징이 아닌 일반 List&lt;Entity&gt;를 DTO 클래스의 {@code from()} 메서드를
     * 자동으로 탐색하여 List&lt;DTO&gt;로 변환합니다.</p>
     *
     * <p><strong>DTO 클래스 요구사항:</strong></p>
     * <ul>
     *   <li>{@code public static DTO from(Entity entity)} 메서드가 존재해야 함</li>
     *   <li>메서드는 정확히 1개의 파라미터를 가져야 함</li>
     * </ul>
     *
     * <p><strong>사용 예시:</strong></p>
     * <pre>
     * {@code
     * List<Message> messages = messageRepository.findTop10ByOrderByCreatedAtDesc();
     * List<MessageDto> dtoList = PageHelper.toList(messages, MessageDto.class);
     * return ApiResponse.ok(dtoList);
     * }
     * </pre>
     *
     * @param <E>        Entity 타입
     * @param <D>        DTO 타입
     * @param entityList 변환할 Entity List
     * @param dtoClass   DTO 클래스 (예: {@code MessageDto.class})
     * @return 변환된 DTO List
     * @throws RuntimeException DTO 클래스에 {@code from()} 메서드가 없거나 변환 실패 시
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
                            throw new RuntimeException("DTO 변환 실패", e);
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("리스트 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 페이징 응답 구조를 생성합니다
     *
     * <p>변환된 DTO List와 원본 Page 객체를 받아서 페이징 정보가 포함된 Map을 생성합니다.</p>
     *
     * @param <D>     DTO 타입
     * @param content 변환된 DTO List
     * @param page    원본 Page 객체 (페이징 메타데이터 추출용)
     * @return 페이징 정보가 포함된 Map 객체
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
     * DTO 클래스에서 {@code from()} 메서드를 찾습니다
     *
     * <p>리플렉션을 사용하여 다음 조건을 만족하는 메서드를 탐색합니다:</p>
     * <ul>
     *   <li>메서드명이 "from"</li>
     *   <li>static 메서드</li>
     *   <li>파라미터가 정확히 1개</li>
     * </ul>
     *
     * @param dtoClass DTO 클래스
     * @return 찾은 {@code from()} 메서드
     * @throws RuntimeException 조건을 만족하는 {@code from()} 메서드를 찾을 수 없는 경우
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

        throw new RuntimeException(dtoClass.getSimpleName() + "에 static from() 메서드가 없습니다.");
    }
}