package com.ritsard.baisard.global.utils;

import com.ritsard.baisard.file.entity.v2.FileInfo;
import com.ritsard.baisard.file.utils.DomainResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageUtils {
    private final DomainResolver domainResolver;

    /**
     * 상대 경로를 절대 URL로 변환
     *
     * @param relativePath 예: /api/ebook/files/abc123/xyz456
     * @return 예: https://example.com/api/ebook/files/abc123/xyz456
     */
    public String toAbsoluteUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) return null;
        return domainResolver.resolveCurrentDomain() + relativePath;
    }

    /**
     * 주어진 URL에서 암호화된 ID를 추출합니다.
     * URL 형식은 "/files/{encryptedId}"이어야 합니다.
     *
     * @param url 파일의 URL
     * @return 암호화된 ID, 예: "1tryq9q0z7w48"
     */
    public String extractEncryptedId(String url) {
        if (!StringUtils.hasText(url)) return null;

//        Pattern pattern = Pattern.compile(".*/files/([^/]+)$");
        Pattern pattern = Pattern.compile(".*/(files|videos)/([^/?]+).*");

        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
//            return matcher.group(1); // e.g. "1tryq9q0z7w48"
            return matcher.group(2); // the actual encryptedId

        }
        return null;
    }

    /**
     * 이는 암호화된 ID와 일치하는 이미지의 URL인 엔터티에 필드를 설정합니다.
     *
     * @param encryptedId
     * @param files
     * @param setter
     * @param <T>
     */
    public <T extends FileInfo> void setImageUrlFromFiles(String encryptedId, List<T> files, Consumer<String> setter) {
        if (!StringUtils.hasText(encryptedId) || files == null || files.isEmpty()) return;

        for (T file : files) {
            String fileEncryptedId = extractEncryptedId(file.getUrl());
            if (encryptedId.equals(fileEncryptedId)) {
                log.debug("file.getUrl() = {}", file.getUrl());

                setter.accept(file.getUrl());
                break;
            }
        }
    }

    /**
     * 주어진 파일 목록에서 모든 파일의 URL을 절대 URL로 변환하여 반환합니다.
     * 이 메서드는 파일 목록이 비어 있거나 null인 경우 빈 리스트를 반환합니다.
     *
     * @param files 파일 목록
     */
    public <T extends FileInfo> List<String> extractAllUrls(List<T> files) {
        if (files == null || files.isEmpty()) return List.of();
        return files.stream()
                .map(f -> this.toAbsoluteUrl(f.getUrl()))
                .filter(StringUtils::hasText)
                .toList();
    }

    public <T extends FileInfo> String extractUrl(T file) {
        if (file == null || !StringUtils.hasText(file.getUrl())) return null;
        return this.toAbsoluteUrl(file.getUrl());
    }


    /**
     * 주어진 암호화된 ID 목록과 파일 목록을 비교하여 일치하는 파일의 URL을 설정합니다.
     *
     * @param encryptedIds 암호화된 ID 목록
     * @param files        파일 목록
     * @param consumer     URL을 처리할 소비자
     * @param <T>          FileInfo의 하위 유형
     */
    public <T extends FileInfo> void setImageUrlFromFiles(List<String> encryptedIds, List<T> files, Consumer<String> consumer) {
        if (encryptedIds == null || files == null) return;

        Set<String> idSet = new HashSet<>(encryptedIds);

        for (T file : files) {
            String url = file.getUrl();
            if (!StringUtils.hasText(url)) continue;

            String fileEncryptedId = extractEncryptedId(file.getUrl());
            if (fileEncryptedId != null && idSet.contains(fileEncryptedId)) {
                consumer.accept(file.getUrl());
            }
        }
    }

    /**
     * 주어진 암호화된 ID를 사용하여 파일 ID를 설정하고, 해당 파일 목록에서 URL을 찾아 설정합니다.
     *
     * @param encryptedId  암호화된 ID
     * @param fileIdSetter 파일 ID를 설정할 소비자 (null일 수 있음)
     * @param filesGetter  파일 목록을 제공하는 공급자
     * @param urlSetter    URL을 설정할 소비자
     * @param <T>          FileInfo의 하위 유형
     */
    public <T extends FileInfo> void bindImageFromEncryptedId(
            String encryptedId,
            Consumer<List<String>> fileIdSetter,
            Supplier<List<T>> filesGetter,
            Consumer<String> urlSetter
    ) {
        if (encryptedId == null || encryptedId.isBlank()) return;

        if (fileIdSetter != null) {
            fileIdSetter.accept(List.of(encryptedId));
        }

        setImageUrlFromFiles(
                encryptedId,
                filesGetter.get(),
                urlSetter
        );
    }

    /**
     * 주어진 암호화된 ID 목록을 사용하여 파일 ID를 설정하고, 해당 파일 목록에서 URL을 찾아 설정합니다.
     *
     * @param encryptedIds 암호화된 ID 목록
     * @param fileIdSetter 파일 ID를 설정할 소비자
     * @param filesGetter  파일 목록을 제공하는 공급자
     * @param urlAdder     URL을 추가할 소비자
     * @param <T>          FileInfo의 하위 유형
     */
    public <T extends FileInfo> void bindImageFromEncryptedId(
            List<String> encryptedIds,
            Consumer<List<String>> fileIdSetter,
            Supplier<List<T>> filesGetter,
            Consumer<String> urlAdder
    ) {
        if (encryptedIds == null || encryptedIds.isEmpty()) return;

        fileIdSetter.accept(encryptedIds);

        setImageUrlFromFiles(
                encryptedIds,
                filesGetter.get(),
                urlAdder
        );
    }

    public String humanReadableByteCount(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), pre);
    }
}
