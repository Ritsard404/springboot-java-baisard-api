/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.enums.geodata;

/**
 * 대륙 정보 열거형 / Continent information enum
 * 전 세계 대륙 분류 정보 제공
 */
public enum Continent {

    /**
     * 아시아 / Asia
     */
    ASIA("아시아", "Asia"),

    /**
     * 유럽 / Europe
     */
    EUROPE("유럽", "Europe"),

    /**
     * 북아메리카 / North America
     */
    NORTH_AMERICA("북미", "North America"),

    /**
     * 남아메리카 / South America
     */
    SOUTH_AMERICA("남미", "South America"),

    /**
     * 아프리카 / Africa
     */
    AFRICA("아프리카", "Africa"),

    /**
     * 오세아니아 / Oceania
     */
    OCEANIA("오세아니아", "Oceania"),

    /**
     * 알 수 없음 / Unknown
     */
    UNKNOWN("알 수 없음", "Unknown");

    private final String koreanName;  // 한글 대륙명 / Korean continent name
    private final String englishName; // 영어 대륙명 / English continent name

    /**
     * Continent 생성자 / Continent constructor
     *
     * @param koreanName  한글 대륙명 / Korean continent name
     * @param englishName 영어 대륙명 / English continent name
     */
    Continent(String koreanName, String englishName) {
        this.koreanName = koreanName;
        this.englishName = englishName;
    }

    /**
     * 한글 대륙명 반환 / Get Korean continent name
     *
     * @return 한글 대륙명 / Korean continent name
     */
    public String getKoreanName() {
        return koreanName;
    }

    /**
     * 영어 대륙명 반환 / Get English continent name
     *
     * @return 영어 대륙명 / English continent name
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * 문자열로 대륙 찾기 / Find continent by string
     *
     * @param value 검색할 문자열 / String to search
     * @return 해당하는 대륙 enum / Corresponding continent enum
     */
    public static Continent fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return UNKNOWN;
        }

        String searchValue = value.toUpperCase().replace(" ", "_");

        // enum 이름으로 검색
        try {
            return Continent.valueOf(searchValue);
        } catch (IllegalArgumentException e) {
            // 한글/영어 이름으로 검색
            for (Continent continent : Continent.values()) {
                if (continent.getKoreanName().equals(value) ||
                        continent.getEnglishName().equalsIgnoreCase(value)) {
                    return continent;
                }
            }
        }

        return UNKNOWN;
    }
}