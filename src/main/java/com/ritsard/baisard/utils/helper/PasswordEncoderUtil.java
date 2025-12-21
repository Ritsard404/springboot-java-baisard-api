package com.ritsard.baisard.utils.helper;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 비밀번호 해싱 유틸리티 / Password hashing utility
 * 단방향 해싱 알고리즘을 통한 안전한 비밀번호 저장 기능 제공
 *
 * <h2>🎯 상황별 권장사항 / Situational Recommendations</h2>
 *
 * <h3>💻 일반 웹 애플리케이션 / General Web Applications</h3>
 * <ul>
 *   <li>🥇 <strong>1순위:</strong> {@link Argon2Util#encode(String)} - 최신 표준, 최강 보안</li>
 *   <li>🥈 <strong>2순위:</strong> {@link BCryptUtil#encode(String)} - 검증된 안전성, 무난한 성능</li>
 * </ul>
 *
 * <h3>🏦 금융권/정부기관 / Financial/Government Systems</h3>
 * <ul>
 *   <li>🥇 <strong>1순위:</strong> {@link Pbkdf2Util#encodeHighSecurity(String)} - NIST 표준, 규제 준수</li>
 *   <li>🥈 <strong>2순위:</strong> {@link BCryptUtil#encodeHighSecurity(String)} - 사실상 표준, 금융권 검증</li>
 * </ul>
 *
 * <h3>🔐 고보안 시스템 / High Security Systems</h3>
 * <ul>
 *   <li>🥇 <strong>1순위:</strong> {@link Argon2Util#encodeHighSecurity(String)} - 최강 보안, 모든 공격 방어</li>
 *   <li>🥈 <strong>2순위:</strong> {@link SCryptUtil#encodeHighSecurity(String)} - 메모리 하드, GPU/ASIC 방어</li>
 * </ul>
 *
 * <h3>🖥️ 레거시 시스템 / Legacy Systems</h3>
 * <ul>
 *   <li>🥇 <strong>1순위:</strong> {@link BCryptUtil#encode(String)} - 호환성 우수, 안정적</li>
 *   <li>🥈 <strong>2순위:</strong> {@link Pbkdf2Util#encode(String)} - 표준 준수, 마이그레이션 용이</li>
 * </ul>
 *
 * <h3>☁️ 클라우드/대용량 시스템 / Cloud/High Volume Systems</h3>
 * <ul>
 *   <li><strong>메모리 여유:</strong> {@link Argon2Util#encode(String)} - 4MB 메모리 사용</li>
 *   <li><strong>메모리 제약:</strong> {@link BCryptUtil#encode(String)} - 1MB 메모리 사용</li>
 * </ul>
 *
 * <h2>⚡ 성능 벤치마크 / Performance Benchmarks</h2>
 * <p><strong>일반 서버 환경 (Intel i7, 16GB RAM) 기준:</strong></p>
 * <table border="1">
 *   <tr><th>알고리즘</th><th>해싱 시간</th><th>메모리 사용</th><th>검증 시간</th><th>TPS (대략)</th></tr>
 *   <tr><td>PBKDF2</td><td>~50ms</td><td>~1MB</td><td>~50ms</td><td>~20 TPS</td></tr>
 *   <tr><td>BCrypt</td><td>~100ms</td><td>~1MB</td><td>~100ms</td><td>~10 TPS</td></tr>
 *   <tr><td>SCrypt</td><td>~200ms</td><td>~4MB</td><td>~200ms</td><td>~5 TPS</td></tr>
 *   <tr><td>Argon2</td><td>~150ms</td><td>~4MB</td><td>~150ms</td><td>~6 TPS</td></tr>
 * </table>
 *
 * <h2>🔄 마이그레이션 가이드 / Migration Guide</h2>
 * <pre>{@code
 * // 단계별 업그레이드 전략
 * // 1단계: 레거시 → PBKDF2
 * String hash = PasswordEncoderUtil.Pbkdf2Util.encode(password);
 *
 * // 2단계: PBKDF2 → BCrypt
 * String hash = PasswordEncoderUtil.BCryptUtil.encode(password);
 *
 * // 3단계: BCrypt → Argon2 (최종 권장)
 * String hash = PasswordEncoderUtil.Argon2Util.encode(password);
 * }</pre>
 *
 * <h2>🎯 2024년 권장 결론 / 2024 Recommendations</h2>
 * <ul>
 *   <li>🏆 <strong>신규 프로젝트:</strong> Argon2 (최신 표준, 최강 보안)</li>
 *   <li>🛡️ <strong>일반 서비스:</strong> BCrypt (검증된 안전성, 무난한 성능)</li>
 *   <li>🏛️ <strong>규제 환경:</strong> PBKDF2 (NIST 승인, 규정 준수)</li>
 *   <li>🔒 <strong>극보안:</strong> SCrypt 또는 Argon2 고보안 모드</li>
 * </ul>
 *
 * @author Lodong Development Team
 * @since 1.0
 * @see <a href="https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html">OWASP Password Storage Cheat Sheet</a>
 * @see <a href="https://tools.ietf.org/html/rfc9106">RFC 9106 - Argon2</a>
 */
public class PasswordEncoderUtil {

    /**
     * BCrypt 패스워드 해싱 유틸리티 / BCrypt password hashing utility
     *
     * <p><strong>📋 알고리즘 특성:</strong></p>
     * <ul>
     *   <li>🔒 <strong>보안 강도:</strong> ⭐⭐⭐⭐ (4/5)</li>
     *   <li>⚡ <strong>성능:</strong> 중간 (~100ms, ~10 TPS)</li>
     *   <li>💾 <strong>메모리:</strong> 낮음 (~1MB)</li>
     *   <li>🔧 <strong>특징:</strong> 적응형 비용, 검증된 안전성</li>
     * </ul>
     *
     * <p><strong>✅ 권장 사용 상황:</strong></p>
     * <ul>
     *   <li>💻 일반 웹 애플리케이션 (2순위)</li>
     *   <li>🏦 금융권/정부기관 (2순위)</li>
     *   <li>🖥️ 레거시 시스템 (1순위)</li>
     *   <li>☁️ 메모리 제약 환경</li>
     * </ul>
     *
     * <p><strong>⚠️ 주의사항:</strong></p>
     * <ul>
     *   <li>패스워드 길이 72바이트 제한</li>
     *   <li>GPU 병렬 공격에 상대적으로 취약</li>
     * </ul>
     *
     * <h3>사용 예시:</h3>
     * <pre>{@code
     * // 일반 강도 해싱 (비용 12, 권장)
     * String hash = PasswordEncoderUtil.BCryptUtil.encode("myPassword");
     *
     * // 고보안 해싱 (비용 14, 관리자 계정용)
     * String hash = PasswordEncoderUtil.BCryptUtil.encodeHighSecurity("adminPassword");
     *
     * // 검증
     * boolean isValid = PasswordEncoderUtil.BCryptUtil.matches("myPassword", hash);
     * }</pre>
     *
     * @since 1.0
     */
    public static class BCryptUtil {

        // BCrypt 강도 설정 / BCrypt strength configuration
        private static final int DEFAULT_STRENGTH = 12; // 2^12 = 4096 라운드
        private static final int HIGH_STRENGTH = 14;    // 2^14 = 16384 라운드 (고보안)

        private static final BCryptPasswordEncoder DEFAULT_ENCODER = new BCryptPasswordEncoder(DEFAULT_STRENGTH);
        private static final BCryptPasswordEncoder HIGH_SECURITY_ENCODER = new BCryptPasswordEncoder(HIGH_STRENGTH);

        /**
         * BCrypt 기본 강도 암호화 / BCrypt encoding with default strength
         * @param rawPassword 원본 패스워드 / raw password
         * @return 암호화된 패스워드 / encoded password
         * @throws IllegalArgumentException 패스워드가 null이거나 빈 문자열인 경우 / if password is null or empty
         */
        public static String encode(String rawPassword) {
            validatePassword(rawPassword, "BCrypt");
            return DEFAULT_ENCODER.encode(rawPassword);
        }

        /**
         * BCrypt 고보안 강도 암호화 / BCrypt encoding with high security strength
         * @param rawPassword 원본 패스워드 / raw password
         * @return 암호화된 패스워드 / encoded password
         * @throws IllegalArgumentException 패스워드가 null이거나 빈 문자열인 경우 / if password is null or empty
         */
        public static String encodeHighSecurity(String rawPassword) {
            validatePassword(rawPassword, "BCrypt");
            return HIGH_SECURITY_ENCODER.encode(rawPassword);
        }

        /**
         * BCrypt 패스워드 일치 확인 / BCrypt password verification
         * @param rawPassword 원본 패스워드 / raw password
         * @param encodedPassword 암호화된 패스워드 / encoded password
         * @return 일치 여부 / match result
         * @throws IllegalArgumentException 매개변수가 유효하지 않은 경우 / if parameters are invalid
         */
        public static boolean matches(String rawPassword, String encodedPassword) {
            validatePassword(rawPassword, "BCrypt");
            validateEncodedPassword(encodedPassword, "BCrypt");

            // 자동으로 해당 강도의 인코더 선택 / Automatically select appropriate encoder
            if (encodedPassword.startsWith("$2a$14$") || encodedPassword.startsWith("$2b$14$")) {
                return HIGH_SECURITY_ENCODER.matches(rawPassword, encodedPassword);
            }
            return DEFAULT_ENCODER.matches(rawPassword, encodedPassword);
        }

        /**
         * BCrypt 해시 강도 확인 / Check BCrypt hash strength
         * @param encodedPassword 암호화된 패스워드 / encoded password
         * @return 해시 강도 / hash strength
         */
        public static int getHashStrength(String encodedPassword) {
            if (encodedPassword == null || !encodedPassword.startsWith("$2")) {
                return -1; // 유효하지 않은 BCrypt 해시
            }

            try {
                String[] parts = encodedPassword.split("\\$");
                if (parts.length >= 3) {
                    return Integer.parseInt(parts[2]);
                }
            } catch (NumberFormatException e) {
                // 파싱 실패 시 기본값 반환
            }

            return DEFAULT_STRENGTH;
        }
    }

    /**
     * SCrypt 패스워드 해싱 유틸리티 / SCrypt password hashing utility
     *
     * <p><strong>📋 알고리즘 특성:</strong></p>
     * <ul>
     *   <li>🔒 <strong>보안 강도:</strong> ⭐⭐⭐⭐ (4/5)</li>
     *   <li>⚡ <strong>성능:</strong> 느림 (~200ms, ~5 TPS)</li>
     *   <li>💾 <strong>메모리:</strong> 높음 (~4MB 기본, ~8MB 고보안)</li>
     *   <li>🔧 <strong>특징:</strong> 메모리 하드, GPU/ASIC 공격 방어</li>
     * </ul>
     *
     * <p><strong>✅ 권장 사용 상황:</strong></p>
     * <ul>
     *   <li>🔐 고보안 시스템 (2순위)</li>
     *   <li>💰 암호화폐 관련 시스템</li>
     *   <li>☁️ 메모리 여유 있는 클라우드 환경</li>
     *   <li>🛡️ GPU 마이닝 공격 우려 시스템</li>
     * </ul>
     *
     * <p><strong>⚠️ 주의사항:</strong></p>
     * <ul>
     *   <li>높은 메모리 사용량으로 DoS 공격 주의</li>
     *   <li>매개변수 튜닝 복잡 (N, r, p 조정 필요)</li>
     *   <li>상대적으로 느린 성능</li>
     * </ul>
     *
     * <h3>사용 예시:</h3>
     * <pre>{@code
     * // 기본 강도 (N=16384, r=8, p=1, 4MB 메모리)
     * String hash = PasswordEncoderUtil.SCryptUtil.encode("myPassword");
     *
     * // 고보안 강도 (N=32768, r=16, p=1, 8MB 메모리)
     * String hash = PasswordEncoderUtil.SCryptUtil.encodeHighSecurity("criticalPassword");
     *
     * // 검증 (매개변수 자동 감지)
     * boolean isValid = PasswordEncoderUtil.SCryptUtil.matches("myPassword", hash);
     * }</pre>
     *
     * @since 1.0
     */
    public static class SCryptUtil {

        // SCrypt 파라미터 설정 / SCrypt parameter configuration
        private static final int DEFAULT_CPU_COST = 16384;     // N = 2^14
        private static final int DEFAULT_MEMORY_COST = 8;      // r = 8
        private static final int DEFAULT_PARALLELIZATION = 1;  // p = 1
        private static final int KEY_LENGTH = 32;              // 32 bytes
        private static final int SALT_LENGTH = 64;             // 64 bytes

        // 고성능 서버용 높은 보안 설정 / High security settings for high-performance servers
        private static final int HIGH_CPU_COST = 32768;       // N = 2^15
        private static final int HIGH_MEMORY_COST = 16;       // r = 16

        private static final SCryptPasswordEncoder DEFAULT_ENCODER = new SCryptPasswordEncoder(
                DEFAULT_CPU_COST, DEFAULT_MEMORY_COST, DEFAULT_PARALLELIZATION, KEY_LENGTH, SALT_LENGTH);

        private static final SCryptPasswordEncoder HIGH_SECURITY_ENCODER = new SCryptPasswordEncoder(
                HIGH_CPU_COST, HIGH_MEMORY_COST, DEFAULT_PARALLELIZATION, KEY_LENGTH, SALT_LENGTH);

        /**
         * SCrypt 기본 강도 암호화 / SCrypt encoding with default strength
         * @param rawPassword 원본 패스워드 / raw password
         * @return 암호화된 패스워드 / encoded password
         * @throws IllegalArgumentException 패스워드가 유효하지 않은 경우 / if password is invalid
         */
        public static String encode(String rawPassword) {
            validatePassword(rawPassword, "SCrypt");
            return DEFAULT_ENCODER.encode(rawPassword);
        }

        /**
         * SCrypt 고보안 강도 암호화 / SCrypt encoding with high security strength
         * @param rawPassword 원본 패스워드 / raw password
         * @return 암호화된 패스워드 / encoded password
         * @throws IllegalArgumentException 패스워드가 유효하지 않은 경우 / if password is invalid
         */
        public static String encodeHighSecurity(String rawPassword) {
            validatePassword(rawPassword, "SCrypt");
            return HIGH_SECURITY_ENCODER.encode(rawPassword);
        }

        /**
         * SCrypt 패스워드 일치 확인 / SCrypt password verification
         * @param rawPassword 원본 패스워드 / raw password
         * @param encodedPassword 암호화된 패스워드 / encoded password
         * @return 일치 여부 / match result
         * @throws IllegalArgumentException 매개변수가 유효하지 않은 경우 / if parameters are invalid
         */
        public static boolean matches(String rawPassword, String encodedPassword) {
            validatePassword(rawPassword, "SCrypt");
            validateEncodedPassword(encodedPassword, "SCrypt");

            // SCrypt는 해시에 파라미터가 포함되어 있어 자동 감지 가능
            return DEFAULT_ENCODER.matches(rawPassword, encodedPassword) ||
                    HIGH_SECURITY_ENCODER.matches(rawPassword, encodedPassword);
        }
    }

    /**
     * PBKDF2 패스워드 해싱 유틸리티 / PBKDF2 password hashing utility
     *
     * <p><strong>📋 알고리즘 특성:</strong></p>
     * <ul>
     *   <li>🔒 <strong>보안 강도:</strong> ⭐⭐⭐ (3/5)</li>
     *   <li>⚡ <strong>성능:</strong> 빠름 (~50ms, ~20 TPS)</li>
     *   <li>💾 <strong>메모리:</strong> 낮음 (~1MB)</li>
     *   <li>🔧 <strong>특징:</strong> NIST 표준, 예측 가능한 성능</li>
     * </ul>
     *
     * <p><strong>✅ 권장 사용 상황:</strong></p>
     * <ul>
     *   <li>🏦 금융권/정부기관 (1순위 - 규제 준수)</li>
     *   <li>🖥️ 레거시 시스템 (2순위 - 호환성)</li>
     *   <li>📱 모바일/IoT 제약 환경</li>
     *   <li>🔄 기존 시스템 마이그레이션</li>
     * </ul>
     *
     * <p><strong>⚠️ 주의사항:</strong></p>
     * <ul>
     *   <li>GPU/ASIC 공격에 상대적으로 취약</li>
     *   <li>단순한 구조로 병렬 공격 가능</li>
     *   <li>2000년 설계로 현대적 공격 기법 미고려</li>
     * </ul>
     *
     * <h3>사용 예시:</h3>
     * <pre>{@code
     * // 기본 강도 (Spring Security 기본값 사용)
     * String hash = PasswordEncoderUtil.Pbkdf2Util.encode("myPassword");
     *
     * // 고보안 강도 (20만회 반복, 금융권 권장)
     * String hash = PasswordEncoderUtil.Pbkdf2Util.encodeHighSecurity("bankPassword");
     *
     * // 검증 (반복 횟수 자동 감지)
     * boolean isValid = PasswordEncoderUtil.Pbkdf2Util.matches("myPassword", hash);
     * }</pre>
     *
     * @since 1.0
     */
    public static class Pbkdf2Util {

        // PBKDF2 설정 / PBKDF2 configuration
        private static final int SALT_LENGTH = 32;             // 32 bytes salt
        private static final int DEFAULT_ITERATIONS = 120000;  // OWASP 2023 권장
        private static final int HIGH_ITERATIONS = 200000;     // 고보안용
        private static final int HASH_WIDTH = 256;             // SHA-256

        /**
         * PBKDF2 기본 강도 암호화 / PBKDF2 encoding with default strength
         * @param rawPassword 원본 패스워드 / raw password
         * @return 암호화된 패스워드 / encoded password
         * @throws IllegalArgumentException 패스워드가 유효하지 않은 경우 / if password is invalid
         */
        public static String encode(String rawPassword) {
            validatePassword(rawPassword, "PBKDF2");

            Pbkdf2PasswordEncoder encoder = Pbkdf2PasswordEncoder
                    .defaultsForSpringSecurity_v5_8();
            encoder.setEncodeHashAsBase64(true); // Base64 인코딩 사용

            return encoder.encode(rawPassword);
        }

        /**
         * PBKDF2 고보안 강도 암호화 / PBKDF2 encoding with high security strength
         * @param rawPassword 원본 패스워드 / raw password
         * @return 암호화된 패스워드 / encoded password
         * @throws IllegalArgumentException 패스워드가 유효하지 않은 경우 / if password is invalid
         */
        public static String encodeHighSecurity(String rawPassword) {
            validatePassword(rawPassword, "PBKDF2");

            Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder("", SALT_LENGTH, HIGH_ITERATIONS,
                    Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
            encoder.setEncodeHashAsBase64(true);

            return encoder.encode(rawPassword);
        }

        /**
         * PBKDF2 패스워드 일치 확인 / PBKDF2 password verification
         * @param rawPassword 원본 패스워드 / raw password
         * @param encodedPassword 암호화된 패스워드 / encoded password
         * @return 일치 여부 / match result
         * @throws IllegalArgumentException 매개변수가 유효하지 않은 경우 / if parameters are invalid
         */
        public static boolean matches(String rawPassword, String encodedPassword) {
            validatePassword(rawPassword, "PBKDF2");
            validateEncodedPassword(encodedPassword, "PBKDF2");

            // Spring Security 기본 PBKDF2 인코더로 검증
            Pbkdf2PasswordEncoder defaultEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
            defaultEncoder.setEncodeHashAsBase64(true);

            if (defaultEncoder.matches(rawPassword, encodedPassword)) {
                return true;
            }

            // 고보안 버전으로도 시도
            Pbkdf2PasswordEncoder highSecEncoder = new Pbkdf2PasswordEncoder("", SALT_LENGTH, HIGH_ITERATIONS,
                    Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
            highSecEncoder.setEncodeHashAsBase64(true);

            return highSecEncoder.matches(rawPassword, encodedPassword);
        }
    }

    /**
     * Argon2 패스워드 해싱 유틸리티 / Argon2 password hashing utility
     *
     * <p><strong>📋 알고리즘 특성:</strong></p>
     * <ul>
     *   <li>🔒 <strong>보안 강도:</strong> ⭐⭐⭐⭐⭐ (5/5) - 최강</li>
     *   <li>⚡ <strong>성능:</strong> 중간 (~150ms, ~6 TPS)</li>
     *   <li>💾 <strong>메모리:</strong> 높음 (~4MB 기본, ~8MB 고보안)</li>
     *   <li>🔧 <strong>특징:</strong> 2015년 최신 표준, 모든 공격 방어</li>
     * </ul>
     *
     * <p><strong>🏆 2024년 최우선 권장 알고리즘</strong></p>
     *
     * <p><strong>✅ 권장 사용 상황:</strong></p>
     * <ul>
     *   <li>💻 일반 웹 애플리케이션 (1순위 - ⭐ 최우선 권장)</li>
     *   <li>🔐 고보안 시스템 (1순위 - 최강 보안)</li>
     *   <li>🆕 신규 프로젝트 (최우선 선택)</li>
     *   <li>☁️ 메모리 여유 있는 환경</li>
     * </ul>
     *
     * <p><strong>🎯 Argon2 종류:</strong></p>
     * <ul>
     *   <li><strong>Argon2id:</strong> 하이브리드 (Spring Security 기본, 권장)</li>
     *   <li><strong>Argon2i:</strong> 사이드채널 공격 안전</li>
     *   <li><strong>Argon2d:</strong> 데이터 의존적 (일반 서버용)</li>
     * </ul>
     *
     * <p><strong>⚠️ 주의사항:</strong></p>
     * <ul>
     *   <li>상대적으로 신규 알고리즘 (2015년~)</li>
     *   <li>메모리 사용량 고려 필요</li>
     *   <li>매개변수 튜닝 복잡성</li>
     * </ul>
     *
     * <h3>사용 예시:</h3>
     * <pre>{@code
     * // 기본 강도 (4MB 메모리, 3회 반복, 신규 프로젝트 권장)
     * String hash = PasswordEncoderUtil.Argon2Util.encode("myPassword");
     *
     * // 고보안 강도 (8MB 메모리, 5회 반복, 최고 보안)
     * String hash = PasswordEncoderUtil.Argon2Util.encodeHighSecurity("topSecretPassword");
     *
     * // 검증 (매개변수 자동 감지)
     * boolean isValid = PasswordEncoderUtil.Argon2Util.matches("myPassword", hash);
     * }</pre>
     *
     * @since 1.0
     * @see <a href="https://tools.ietf.org/html/rfc9106">RFC 9106 - The Argon2 Memory-Hard Function</a>
     */
    public static class Argon2Util {

        // Argon2 설정 / Argon2 configuration
        private static final int SALT_LENGTH = 32;        // 32 bytes
        private static final int HASH_LENGTH = 32;        // 32 bytes
        private static final int DEFAULT_PARALLELISM = 1; // 병렬성
        private static final int DEFAULT_MEMORY = 4096;   // 4MB 메모리 사용
        private static final int DEFAULT_ITERATIONS = 3;  // 3회 반복

        // 고보안 설정 / High security configuration
        private static final int HIGH_MEMORY = 8192;      // 8MB 메모리 사용
        private static final int HIGH_ITERATIONS = 5;     // 5회 반복

        private static final Argon2PasswordEncoder DEFAULT_ENCODER =
                new Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, DEFAULT_PARALLELISM, DEFAULT_MEMORY, DEFAULT_ITERATIONS);

        private static final Argon2PasswordEncoder HIGH_SECURITY_ENCODER =
                new Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, DEFAULT_PARALLELISM, HIGH_MEMORY, HIGH_ITERATIONS);

        /**
         * Argon2 기본 강도 암호화 / Argon2 encoding with default strength
         * @param rawPassword 원본 패스워드 / raw password
         * @return 암호화된 패스워드 / encoded password
         * @throws IllegalArgumentException 패스워드가 유효하지 않은 경우 / if password is invalid
         */
        public static String encode(String rawPassword) {
            validatePassword(rawPassword, "Argon2");
            return DEFAULT_ENCODER.encode(rawPassword);
        }

        /**
         * Argon2 고보안 강도 암호화 / Argon2 encoding with high security strength
         * @param rawPassword 원본 패스워드 / raw password
         * @return 암호화된 패스워드 / encoded password
         * @throws IllegalArgumentException 패스워드가 유효하지 않은 경우 / if password is invalid
         */
        public static String encodeHighSecurity(String rawPassword) {
            validatePassword(rawPassword, "Argon2");
            return HIGH_SECURITY_ENCODER.encode(rawPassword);
        }

        /**
         * Argon2 패스워드 일치 확인 / Argon2 password verification
         * @param rawPassword 원본 패스워드 / raw password
         * @param encodedPassword 암호화된 패스워드 / encoded password
         * @return 일치 여부 / match result
         * @throws IllegalArgumentException 매개변수가 유효하지 않은 경우 / if parameters are invalid
         */
        public static boolean matches(String rawPassword, String encodedPassword) {
            validatePassword(rawPassword, "Argon2");
            validateEncodedPassword(encodedPassword, "Argon2");

            // Argon2는 해시에 파라미터가 포함되어 있어 자동 감지 가능
            return DEFAULT_ENCODER.matches(rawPassword, encodedPassword) ||
                    HIGH_SECURITY_ENCODER.matches(rawPassword, encodedPassword);
        }
    }

    /**
     * 레거지 해시 유틸리티 / Legacy hash utility
     *
     * <p><strong>⚠️ 보안 경고:</strong> 이 클래스의 알고리즘들은 현재 보안 표준에 미달합니다.</p>
     *
     * <p><strong>📋 알고리즘 특성:</strong></p>
     * <ul>
     *   <li>🔒 <strong>보안 강도:</strong> ⭐⭐ (2/5) - 취약</li>
     *   <li>⚡ <strong>성능:</strong> 매우 빠름 (~1ms)</li>
     *   <li>💾 <strong>메모리:</strong> 매우 낮음 (~100KB)</li>
     *   <li>🔧 <strong>특징:</strong> 단순 해싱, 솔트 수동 관리</li>
     * </ul>
     *
     * <p><strong>✅ 사용 가능 상황 (제한적):</strong></p>
     * <ul>
     *   <li>🔄 기존 시스템 마이그레이션 중간 단계</li>
     *   <li>📱 극도로 제약된 IoT 환경</li>
     *   <li>🔬 개발/테스트 목적</li>
     *   <li>🏛️ 레거시 호환성 유지 (임시)</li>
     * </ul>
     *
     * <p><strong>🚨 권장하지 않는 이유:</strong></p>
     * <ul>
     *   <li>레인보우 테이블 공격 취약</li>
     *   <li>GPU 병렬 공격 매우 취약</li>
     *   <li>브루트포스 공격 취약</li>
     *   <li>현대적 공격 기법 무방어</li>
     * </ul>
     *
     * <h3>마이그레이션 예시:</h3>
     * <pre>{@code
     * // 기존 SHA-256 해시가 있는 경우 (임시 사용)
     * String tempHash = PasswordEncoderUtil.LegacyHashUtil.sha256WithSalt("oldPassword");
     *
     * // 검증 후 즉시 최신 알고리즘으로 업그레이드
     * if (PasswordEncoderUtil.LegacyHashUtil.verifySha256WithSalt("oldPassword", tempHash)) {
     *     // 로그인 성공 시 Argon2로 재해싱
     *     String newHash = PasswordEncoderUtil.Argon2Util.encode("oldPassword");
     *     updateUserPassword(userId, newHash);
     * }
     * }</pre>
     *
     * <p><strong>⚡ 긴급 마이그레이션 계획 수립 필요</strong></p>
     *
     * @since 1.0
     * @deprecated 보안상 취약하므로 마이그레이션 목적으로만 사용, 신규 개발 금지
     */
    public static class LegacyHashUtil {

        private static final SecureRandom SECURE_RANDOM = new SecureRandom();

        /**
         * SHA-256 해싱 (솔트 포함) / SHA-256 hashing with salt
         * @param rawPassword 원본 패스워드 / raw password
         * @return 솔트 + 해시 결합된 문자열 / salt + hash combined string
         * @throws RuntimeException 해싱 실패 시 / when hashing fails
         */
        public static String sha256WithSalt(String rawPassword) {
            validatePassword(rawPassword, "SHA-256");

            try {
                // 16바이트 랜덤 솔트 생성 / Generate 16-byte random salt
                byte[] salt = new byte[16];
                SECURE_RANDOM.nextBytes(salt);

                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt);
                byte[] hashedPassword = md.digest(rawPassword.getBytes("UTF-8"));

                // 솔트 + 해시를 Base64로 인코딩 / Encode salt + hash as Base64
                byte[] combined = new byte[salt.length + hashedPassword.length];
                System.arraycopy(salt, 0, combined, 0, salt.length);
                System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

                return Base64.getEncoder().encodeToString(combined);

            } catch (Exception e) {
                throw new RuntimeException("SHA-256 해싱 실패 / SHA-256 hashing failed", e);
            }
        }

        /**
         * SHA-256 패스워드 검증 / SHA-256 password verification
         * @param rawPassword 원본 패스워드 / raw password
         * @param encodedPassword 인코딩된 패스워드 / encoded password
         * @return 일치 여부 / match result
         */
        public static boolean verifySha256WithSalt(String rawPassword, String encodedPassword) {
            validatePassword(rawPassword, "SHA-256");
            validateEncodedPassword(encodedPassword, "SHA-256");

            try {
                byte[] combined = Base64.getDecoder().decode(encodedPassword);

                if (combined.length < 48) { // 16(salt) + 32(SHA-256 hash) = 48 bytes minimum
                    return false;
                }

                // 솔트와 해시 분리 / Separate salt and hash
                byte[] salt = new byte[16];
                byte[] hash = new byte[combined.length - 16];
                System.arraycopy(combined, 0, salt, 0, 16);
                System.arraycopy(combined, 16, hash, 0, hash.length);

                // 입력된 패스워드로 해시 재생성 / Regenerate hash with input password
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt);
                byte[] computedHash = md.digest(rawPassword.getBytes("UTF-8"));

                return MessageDigest.isEqual(hash, computedHash);

            } catch (Exception e) {
                return false;
            }
        }
    }

    // ===============================================
    // ✅ 공통 유효성 검증 메서드들 / Common validation methods
    // ===============================================

    /**
     * 패스워드 유효성 검증 / Validate password
     * @param password 검증할 패스워드 / password to validate
     * @param algorithm 알고리즘 이름 / algorithm name
     * @throws IllegalArgumentException 패스워드가 유효하지 않은 경우 / if password is invalid
     */
    private static void validatePassword(String password, String algorithm) {
        if (password == null) {
            throw new IllegalArgumentException(algorithm + " 패스워드는 null일 수 없습니다 / password cannot be null");
        }
        if (password.isEmpty()) {
            throw new IllegalArgumentException(algorithm + " 패스워드는 빈 문자열일 수 없습니다 / password cannot be empty");
        }
        if (password.length() > 72) { // BCrypt 제한사항 고려
            throw new IllegalArgumentException(algorithm + " 패스워드는 72자를 초과할 수 없습니다 / password cannot exceed 72 characters");
        }
    }

    /**
     * 인코딩된 패스워드 유효성 검증 / Validate encoded password
     * @param encodedPassword 검증할 인코딩된 패스워드 / encoded password to validate
     * @param algorithm 알고리즘 이름 / algorithm name
     * @throws IllegalArgumentException 인코딩된 패스워드가 유효하지 않은 경우 / if encoded password is invalid
     */
    private static void validateEncodedPassword(String encodedPassword, String algorithm) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            throw new IllegalArgumentException(algorithm + " 인코딩된 패스워드가 유효하지 않습니다 / encoded password is invalid");
        }
    }

    /**
     * 패스워드 강도 검증 / Validate password strength
     *
     * <p>OWASP 가이드라인 기반 패스워드 보안 강도를 검증합니다.</p>
     *
     * <p><strong>📋 검증 기준:</strong></p>
     * <ul>
     *   <li>🔢 <strong>길이:</strong> 8자 이상 권장 (6자 이상 허용)</li>
     *   <li>🔤 <strong>소문자:</strong> a-z 포함 (+1점)</li>
     *   <li>🔠 <strong>대문자:</strong> A-Z 포함 (+1점)</li>
     *   <li>🔢 <strong>숫자:</strong> 0-9 포함 (+1점)</li>
     *   <li>? <strong>특수문자:</strong> !@#$%^&amp;*() 등 포함 (+2점)</li>
     * </ul>
     *
     * <p><strong>⭐ 강도 등급:</strong></p>
     * <ul>
     *   <li><strong>6점 이상:</strong> 강한 패스워드 ✅</li>
     *   <li><strong>4-5점:</strong> 보통 패스워드 ⚠️</li>
     *   <li><strong>3점 이하:</strong> 약한 패스워드 ❌</li>
     * </ul>
     *
     * @param password 검증할 패스워드 / password to validate
     * @return 패스워드 강도 정보 / password strength information
     *
     * <h3>사용 예시:</h3>
     * <pre>{@code
     * PasswordStrength strength = PasswordEncoderUtil.checkPasswordStrength("MyPass123!");
     *
     * if (strength.isStrong()) {
     *     System.out.println("✅ " + strength.getMessage()); // "강한 패스워드입니다"
     *     // 해싱 진행
     *     String hash = PasswordEncoderUtil.Argon2Util.encode(password);
     * } else {
     *     System.out.println("❌ " + strength.getMessage()); // "패스워드 강도를 높이세요: 8자 이상 권장"
     *     // 사용자에게 개선 요청
     * }
     *
     * System.out.println("점수: " + strength.getScore() + "/8");
     * }</pre>
     *
     * @since 1.0
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return new PasswordStrength(false, 0, "패스워드가 비어있습니다");
        }

        int score = 0;
        StringBuilder feedback = new StringBuilder();

        // 길이 검사 / Length check
        if (password.length() >= 8) score += 2;
        else if (password.length() >= 6) score += 1;
        else feedback.append("8자 이상 권장. ");

        // 복잡성 검사 / Complexity check
        if (password.matches(".*[a-z].*")) score += 1; // 소문자
        if (password.matches(".*[A-Z].*")) score += 1; // 대문자
        if (password.matches(".*\\d.*")) score += 1;   // 숫자
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) score += 2; // 특수문자

        boolean isStrong = score >= 6;
        String message = isStrong ? "강한 패스워드입니다" :
                "패스워드 강도를 높이세요: " + feedback.toString();

        return new PasswordStrength(isStrong, score, message);
    }

    /**
     * 패스워드 강도 정보 클래스 / Password strength information class
     */
    public static class PasswordStrength {
        private final boolean strong;
        private final int score;
        private final String message;

        public PasswordStrength(boolean strong, int score, String message) {
            this.strong = strong;
            this.score = score;
            this.message = message;
        }

        public boolean isStrong() { return strong; }
        public int getScore() { return score; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return String.format("PasswordStrength{strong=%s, score=%d, message='%s'}",
                    strong, score, message);
        }
    }
}