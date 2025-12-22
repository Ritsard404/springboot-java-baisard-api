/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.helper;

import com.ritsard.baisard.utils.enums.geodata.Continent;
import com.ritsard.baisard.utils.enums.geodata.Countries;

import java.util.List;
import java.util.regex.Pattern;

public class FormatterUtils {

    public static class KoreanDataFormatter {
        public static String formatBusinessNumber(String businessNumber) {
            if (businessNumber == null || businessNumber.isEmpty()) {
                return businessNumber;
            }
            String cleanNumber = businessNumber.replaceAll("[^\\d]", "");
            if (cleanNumber.length() == 10) {
                return cleanNumber.replaceFirst("(\\d{3})(\\d{2})(\\d{5})", "$1-$2-$3");
            }
            return businessNumber;
        }

        public static String formatResidentNumber(String residentNumber, boolean maskBackPart) {
            if (residentNumber == null || residentNumber.isEmpty()) {
                return residentNumber;
            }
            String cleanNumber = residentNumber.replaceAll("[^\\d]", "");
            if (cleanNumber.length() == 13) {
                if (maskBackPart) {
                    return cleanNumber.replaceFirst("(\\d{6})(\\d{1})(\\d{6})", "$1-$2******");
                }
                return cleanNumber.replaceFirst("(\\d{6})(\\d{7})", "$1-$2");
            }
            return residentNumber;
        }

        public static String formatPostalCode(String postalCode) {
            if (postalCode == null || postalCode.isEmpty()) {
                return postalCode;
            }
            String cleanCode = postalCode.replaceAll("[^\\d]", "");
            if (cleanCode.length() == 5) {
                return cleanCode;
            }
            return postalCode;
        }
    }

    public static class InternationalPhoneNumber {
        public static String formatInternationalNumber(String phoneNumber, Countries country) {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return phoneNumber;
            }
            if (country == null) {
                throw new IllegalArgumentException("\uad6d\uac00 \uc815\ubcf4\ub294 \ud544\uc218\uc785\ub2c8\ub2e4 / Country information is required");
            }
            String cleanNumber = phoneNumber.replaceAll("[^\\d+]", "");
            String localNumber = InternationalPhoneNumber.extractLocalNumber(cleanNumber, country.getDialingCode());
            if (localNumber.isEmpty()) {
                return phoneNumber;
            }
            String formattedLocal = InternationalPhoneNumber.formatLocalNumberByCountry(localNumber, country);
            return country.getInternationalPrefix() + " " + formattedLocal;
        }

        public static String formatInternationalNumber(String phoneNumber, String countryIdentifier) {
            Countries country = Countries.fromString(countryIdentifier);
            if (country == null) {
                country = Countries.fromCountryCode(countryIdentifier);
            }
            if (country == null) {
                country = Countries.fromDialingCode(countryIdentifier);
            }
            if (country == null) {
                return InternationalPhoneNumber.formatUnknownCountryNumber(phoneNumber);
            }
            return InternationalPhoneNumber.formatInternationalNumber(phoneNumber, country);
        }

        public static String formatWithAutoDetection(String phoneNumber) {
            Countries country = InternationalPhoneNumber.extractCountryFromNumber(phoneNumber);
            if (country != null) {
                return InternationalPhoneNumber.formatInternationalNumber(phoneNumber, country);
            }
            return InternationalPhoneNumber.formatUnknownCountryNumber(phoneNumber);
        }

        public static List<String> formatByContinent(String phoneNumber, Continent continent) {
            List<Countries> countriesInContinent = Countries.findByContinent(continent);
            return countriesInContinent.stream().map(country -> InternationalPhoneNumber.formatInternationalNumber(phoneNumber, country)).toList();
        }

        public static List<String> formatWithDialingCodeDuplicates(String phoneNumber, String dialingCode) {
            List<Countries> possibleCountries = Countries.findByDialingCode(dialingCode);
            return possibleCountries.stream().map(country -> InternationalPhoneNumber.formatInternationalNumber(phoneNumber, country)).toList();
        }

        private static String extractLocalNumber(String cleanNumber, String dialingCode) {
            if (cleanNumber.startsWith("+")) {
                cleanNumber = cleanNumber.substring(1);
            }
            if (cleanNumber.startsWith(dialingCode)) {
                return cleanNumber.substring(dialingCode.length());
            }
            return cleanNumber;
        }

        private static String formatLocalNumberByCountry(String localNumber, Countries country) {
            Continent continent = country.getContinent();
            switch (continent) {
                case NORTH_AMERICA: {
                    return InternationalPhoneNumber.formatNorthAmericanNumber(localNumber, country);
                }
                case ASIA: {
                    return InternationalPhoneNumber.formatAsianNumber(localNumber, country);
                }
                case EUROPE: {
                    return InternationalPhoneNumber.formatEuropeanNumber(localNumber, country);
                }
                case OCEANIA: {
                    return InternationalPhoneNumber.formatOceanianNumber(localNumber, country);
                }
            }
            return InternationalPhoneNumber.formatDefaultNumber(localNumber);
        }

        private static String formatNorthAmericanNumber(String localNumber, Countries country) {
            switch (country) {
                case UNITED_STATES:
                case CANADA: {
                    if (localNumber.length() != 10) break;
                    return localNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "($1) $2-$3");
                }
                case MEXICO: {
                    if (localNumber.length() != 10) break;
                    return localNumber.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "$1 $2 $3");
                }
                default: {
                    return InternationalPhoneNumber.formatDefaultNumber(localNumber);
                }
            }
            return localNumber;
        }

        private static String formatAsianNumber(String localNumber, Countries country) {
            switch (country) {
                case JAPAN: {
                    return InternationalPhoneNumber.formatJapaneseNumber(localNumber);
                }
                case CHINA: {
                    return InternationalPhoneNumber.formatChineseNumber(localNumber);
                }
                case SOUTH_KOREA: {
                    return KoreanPhoneNumber.formatPhoneNumber(localNumber);
                }
                case SINGAPORE: {
                    if (localNumber.length() != 8) break;
                    return localNumber.replaceFirst("(\\d{4})(\\d{4})", "$1 $2");
                }
                case INDIA: {
                    if (localNumber.length() != 10) break;
                    return localNumber.replaceFirst("(\\d{5})(\\d{5})", "$1 $2");
                }
                default: {
                    return InternationalPhoneNumber.formatDefaultNumber(localNumber);
                }
            }
            return localNumber;
        }

        private static String formatEuropeanNumber(String localNumber, Countries country) {
            switch (country) {
                case UNITED_KINGDOM: {
                    return InternationalPhoneNumber.formatUKNumber(localNumber);
                }
                case GERMANY: {
                    return InternationalPhoneNumber.formatGermanNumber(localNumber);
                }
                case FRANCE: {
                    return InternationalPhoneNumber.formatFrenchNumber(localNumber);
                }
                case ITALY: {
                    if (localNumber.length() < 9) break;
                    return localNumber.replaceFirst("(\\d{2,4})(\\d{6,8})", "$1 $2");
                }
                case SPAIN: {
                    if (localNumber.length() != 9) break;
                    return localNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{3})", "$1 $2 $3");
                }
                default: {
                    return InternationalPhoneNumber.formatDefaultNumber(localNumber);
                }
            }
            return localNumber;
        }

        private static String formatOceanianNumber(String localNumber, Countries country) {
            switch (country) {
                case AUSTRALIA: {
                    return InternationalPhoneNumber.formatAustralianNumber(localNumber);
                }
                case NEW_ZEALAND: {
                    if (localNumber.length() < 8) break;
                    return localNumber.replaceFirst("(\\d{1,2})(\\d{3,4})(\\d{4})", "$1 $2 $3");
                }
                default: {
                    return InternationalPhoneNumber.formatDefaultNumber(localNumber);
                }
            }
            return localNumber;
        }

        private static String formatJapaneseNumber(String localNumber) {
            if (localNumber.length() >= 10) {
                if (localNumber.startsWith("3") || localNumber.startsWith("6")) {
                    return localNumber.replaceFirst("(\\d{1})(\\d{4})(\\d{4})", "$1-$2-$3");
                }
                if (localNumber.startsWith("90") || localNumber.startsWith("80")) {
                    return localNumber.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "$1-$2-$3");
                }
            }
            return localNumber;
        }

        private static String formatChineseNumber(String localNumber) {
            if (localNumber.length() == 11 && localNumber.startsWith("1")) {
                return localNumber.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1 $2 $3");
            }
            if (localNumber.length() >= 10) {
                return localNumber.replaceFirst("(\\d{2,3})(\\d{4})(\\d{4})", "$1-$2-$3");
            }
            return localNumber;
        }

        private static String formatUKNumber(String localNumber) {
            if (localNumber.length() == 10) {
                if (localNumber.startsWith("20")) {
                    return localNumber.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "$1 $2 $3");
                }
                return localNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1 $2 $3");
            }
            return localNumber;
        }

        private static String formatGermanNumber(String localNumber) {
            if (localNumber.length() >= 10) {
                return localNumber.replaceFirst("(\\d{2,4})(\\d{6,8})", "$1-$2");
            }
            return localNumber;
        }

        private static String formatFrenchNumber(String localNumber) {
            if (localNumber.length() == 9) {
                return localNumber.replaceFirst("(\\d{1})(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1 $2 $3 $4 $5");
            }
            return localNumber;
        }

        private static String formatAustralianNumber(String localNumber) {
            if (localNumber.length() == 9) {
                return localNumber.replaceFirst("(\\d{1})(\\d{4})(\\d{4})", "$1 $2 $3");
            }
            return localNumber;
        }

        private static String formatDefaultNumber(String localNumber) {
            if (localNumber.length() == 10) {
                return localNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
            }
            if (localNumber.length() == 11) {
                return localNumber.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
            }
            if (localNumber.length() == 8) {
                return localNumber.replaceFirst("(\\d{4})(\\d{4})", "$1-$2");
            }
            return localNumber;
        }

        private static String formatUnknownCountryNumber(String phoneNumber) {
            String cleanNumber = phoneNumber.replaceAll("[^\\d+]", "");
            if (cleanNumber.startsWith("+")) {
                return cleanNumber;
            }
            if (cleanNumber.length() >= 10) {
                return "+" + cleanNumber;
            }
            return phoneNumber;
        }

        public static boolean isValidInternationalNumber(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return false;
            }
            String cleanNumber = phoneNumber.replaceAll("[^\\d+]", "");
            return cleanNumber.startsWith("+") && cleanNumber.length() >= 8 && cleanNumber.length() <= 18;
        }

        public static Countries extractCountryFromNumber(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return null;
            }
            String cleanNumber = phoneNumber.replaceAll("[^\\d+]", "");
            if (!cleanNumber.startsWith("+")) {
                return null;
            }
            cleanNumber = cleanNumber.substring(1);
            for (int i = 1; i <= Math.min(4, cleanNumber.length()); ++i) {
                String possibleCode = cleanNumber.substring(0, i);
                List<Countries> possibleCountries = Countries.findByDialingCode(possibleCode);
                if (possibleCountries.isEmpty()) continue;
                return possibleCountries.get(0);
            }
            return null;
        }

        public static List<Countries> extractAllPossibleCountries(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return List.of();
            }
            String cleanNumber = phoneNumber.replaceAll("[^\\d+]", "");
            if (!cleanNumber.startsWith("+")) {
                return List.of();
            }
            cleanNumber = cleanNumber.substring(1);
            for (int i = 1; i <= Math.min(4, cleanNumber.length()); ++i) {
                String possibleCode = cleanNumber.substring(0, i);
                List<Countries> possibleCountries = Countries.findByDialingCode(possibleCode);
                if (possibleCountries.isEmpty()) continue;
                return possibleCountries;
            }
            return List.of();
        }
    }

    public static class KoreanPhoneNumber {
        private static final Pattern MOBILE_PATTERN = Pattern.compile("^01[016789]\\d{7,8}$");
        private static final Pattern SEOUL_PATTERN = Pattern.compile("^02\\d{7,8}$");
        private static final Pattern AREA_CODE_PATTERN = Pattern.compile("^0(3[1-9]|4[1-4]|5[1-5]|6[1-4])\\d{7,8}$");
        private static final Pattern INTERNET_PHONE_PATTERN = Pattern.compile("^050[2-9]\\d{7}$");
        private static final Pattern TOLL_FREE_PATTERN = Pattern.compile("^080\\d{7}$");
        private static final Pattern REPRESENTATIVE_PATTERN = Pattern.compile("^1[5-6]\\d{2}\\d{4}$");
        private static final Pattern EMERGENCY_PATTERN = Pattern.compile("^1[1-9]{2}$");

        public static String removeDashes(String phoneNumber) {
            if (phoneNumber == null) {
                return null;
            }
            return phoneNumber.replaceAll("[-\\s()]", "");
        }

        public static String formatPhoneNumber(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return phoneNumber;
            }
            String cleanNumber = KoreanPhoneNumber.removeDashes(phoneNumber);
            if (!KoreanPhoneNumber.isValidKoreanPhoneNumber(cleanNumber)) {
                throw new IllegalArgumentException("\uc720\ud6a8\ud558\uc9c0 \uc54a\uc740 \ud55c\uad6d \uc804\ud654\ubc88\ud638 \ud615\uc2dd\uc785\ub2c8\ub2e4: " + phoneNumber);
            }
            return KoreanPhoneNumber.formatByPattern(cleanNumber);
        }

        private static String formatByPattern(String cleanNumber) {
            if (MOBILE_PATTERN.matcher(cleanNumber).matches()) {
                if (cleanNumber.length() == 10) {
                    return cleanNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
                }
                if (cleanNumber.length() == 11) {
                    return cleanNumber.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
                }
            }
            if (SEOUL_PATTERN.matcher(cleanNumber).matches()) {
                if (cleanNumber.length() == 9) {
                    return cleanNumber.replaceFirst("(\\d{2})(\\d{3})(\\d{4})", "$1-$2-$3");
                }
                if (cleanNumber.length() == 10) {
                    return cleanNumber.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "$1-$2-$3");
                }
            }
            if (AREA_CODE_PATTERN.matcher(cleanNumber).matches()) {
                if (cleanNumber.length() == 10) {
                    return cleanNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
                }
                if (cleanNumber.length() == 11) {
                    return cleanNumber.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
                }
            }
            if (INTERNET_PHONE_PATTERN.matcher(cleanNumber).matches()) {
                return cleanNumber.replaceFirst("(\\d{4})(\\d{4})(\\d{4})", "$1-$2-$3");
            }
            if (TOLL_FREE_PATTERN.matcher(cleanNumber).matches()) {
                return cleanNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
            }
            if (REPRESENTATIVE_PATTERN.matcher(cleanNumber).matches()) {
                return cleanNumber.replaceFirst("(\\d{4})(\\d{4})", "$1-$2");
            }
            if (EMERGENCY_PATTERN.matcher(cleanNumber).matches()) {
                return cleanNumber;
            }
            return cleanNumber;
        }

        public static boolean isValidKoreanPhoneNumber(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return false;
            }
            String cleanNumber = KoreanPhoneNumber.removeDashes(phoneNumber);
            return MOBILE_PATTERN.matcher(cleanNumber).matches() || SEOUL_PATTERN.matcher(cleanNumber).matches() || AREA_CODE_PATTERN.matcher(cleanNumber).matches() || INTERNET_PHONE_PATTERN.matcher(cleanNumber).matches() || TOLL_FREE_PATTERN.matcher(cleanNumber).matches() || REPRESENTATIVE_PATTERN.matcher(cleanNumber).matches() || EMERGENCY_PATTERN.matcher(cleanNumber).matches();
        }

        public static PhoneNumberType getPhoneNumberType(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return PhoneNumberType.UNKNOWN;
            }
            String cleanNumber = KoreanPhoneNumber.removeDashes(phoneNumber);
            if (MOBILE_PATTERN.matcher(cleanNumber).matches()) {
                return PhoneNumberType.MOBILE;
            }
            if (SEOUL_PATTERN.matcher(cleanNumber).matches()) {
                return PhoneNumberType.SEOUL;
            }
            if (AREA_CODE_PATTERN.matcher(cleanNumber).matches()) {
                return PhoneNumberType.AREA_CODE;
            }
            if (INTERNET_PHONE_PATTERN.matcher(cleanNumber).matches()) {
                return PhoneNumberType.INTERNET_PHONE;
            }
            if (TOLL_FREE_PATTERN.matcher(cleanNumber).matches()) {
                return PhoneNumberType.TOLL_FREE;
            }
            if (REPRESENTATIVE_PATTERN.matcher(cleanNumber).matches()) {
                return PhoneNumberType.REPRESENTATIVE;
            }
            if (EMERGENCY_PATTERN.matcher(cleanNumber).matches()) {
                return PhoneNumberType.EMERGENCY;
            }
            return PhoneNumberType.UNKNOWN;
        }

        public static enum PhoneNumberType {
            MOBILE("\ud734\ub300\ud3f0"),
            SEOUL("\uc11c\uc6b8"),
            AREA_CODE("\uc9c0\uc5ed\ubc88\ud638"),
            INTERNET_PHONE("\uc778\ud130\ub137\uc804\ud654"),
            TOLL_FREE("\ubb34\ub8cc\uc804\ud654"),
            REPRESENTATIVE("\ub300\ud45c\ubc88\ud638"),
            EMERGENCY("\uc751\uae09\ubc88\ud638"),
            UNKNOWN("\uc54c \uc218 \uc5c6\uc74c");

            private final String description;

            private PhoneNumberType(String description) {
                this.description = description;
            }

            public String getDescription() {
                return this.description;
            }
        }
    }
}

