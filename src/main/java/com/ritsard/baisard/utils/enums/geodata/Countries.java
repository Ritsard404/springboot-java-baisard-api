/*
 * Decompiled with CFR 0.152.
 */
package com.ritsard.baisard.utils.enums.geodata;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 국가 정보 열거형 / Countries information enum
 * 진정한 객체지향적 구조로 각 국가가 자신의 속성을 완벽히 관리
 */
public enum Countries {

    // 아시아 / Asia
    SOUTH_KOREA("대한민국", "KR", "82", Continent.ASIA),
    NORTH_KOREA("조선민주주의인민공화국", "KP", "850", Continent.ASIA),
    JAPAN("일본", "JP", "81", Continent.ASIA),
    CHINA("중국", "CN", "86", Continent.ASIA),
    TAIWAN("대만", "TW", "886", Continent.ASIA),
    HONG_KONG("홍콩", "HK", "852", Continent.ASIA),
    MACAU("마카오", "MO", "853", Continent.ASIA),
    SINGAPORE("싱가포르", "SG", "65", Continent.ASIA),
    MALAYSIA("말레이시아", "MY", "60", Continent.ASIA),
    THAILAND("태국", "TH", "66", Continent.ASIA),
    VIETNAM("베트남", "VN", "84", Continent.ASIA),
    PHILIPPINES("필리핀", "PH", "63", Continent.ASIA),
    INDONESIA("인도네시아", "ID", "62", Continent.ASIA),
    INDIA("인도", "IN", "91", Continent.ASIA),
    PAKISTAN("파키스탄", "PK", "92", Continent.ASIA),
    BANGLADESH("방글라데시", "BD", "880", Continent.ASIA),
    SRI_LANKA("스리랑카", "LK", "94", Continent.ASIA),
    MYANMAR("미얀마", "MM", "95", Continent.ASIA),
    CAMBODIA("캄보디아", "KH", "855", Continent.ASIA),
    LAOS("라오스", "LA", "856", Continent.ASIA),
    MONGOLIA("몽골", "MN", "976", Continent.ASIA),
    AFGHANISTAN("아프가니스탄", "AF", "93", Continent.ASIA),
    ARMENIA("아르메니아", "AM", "374", Continent.ASIA),
    AZERBAIJAN("아제르바이잔", "AZ", "994", Continent.ASIA),
    BAHRAIN("바레인", "BH", "973", Continent.ASIA),
    BHUTAN("부탄", "BT", "975", Continent.ASIA),
    BRUNEI("브루나이", "BN", "673", Continent.ASIA),
    GEORGIA("조지아", "GE", "995", Continent.ASIA),
    IRAN("이란", "IR", "98", Continent.ASIA),
    IRAQ("이라크", "IQ", "964", Continent.ASIA),
    ISRAEL("이스라엘", "IL", "972", Continent.ASIA),
    JORDAN("요단", "JO", "962", Continent.ASIA),
    KAZAKHSTAN("카자흐스탄", "KZ", "7", Continent.ASIA),
    KUWAIT("쿠웨이트", "KW", "965", Continent.ASIA),
    KYRGYZSTAN("키르기스스탄", "KG", "996", Continent.ASIA),
    LEBANON("레바논", "LB", "961", Continent.ASIA),
    MALDIVES("몰디브", "MV", "960", Continent.ASIA),
    NEPAL("네팔", "NP", "977", Continent.ASIA),
    OMAN("오만", "OM", "968", Continent.ASIA),
    QATAR("카타르", "QA", "974", Continent.ASIA),
    SAUDI_ARABIA("사우디아라비아", "SA", "966", Continent.ASIA),
    SYRIA("시리아", "SY", "963", Continent.ASIA),
    TAJIKISTAN("타지키스탄", "TJ", "992", Continent.ASIA),
    TURKEY("터키", "TR", "90", Continent.ASIA),
    TURKMENISTAN("투르크메니스탄", "TM", "993", Continent.ASIA),
    UNITED_ARAB_EMIRATES("아랍에미리트", "AE", "971", Continent.ASIA),
    UZBEKISTAN("우즈베키스탄", "UZ", "998", Continent.ASIA),
    YEMEN("예멘", "YE", "967", Continent.ASIA),

    // 유럽 / Europe
    UNITED_KINGDOM("영국", "GB", "44", Continent.EUROPE),
    GERMANY("독일", "DE", "49", Continent.EUROPE),
    FRANCE("프랑스", "FR", "33", Continent.EUROPE),
    ITALY("이탈리아", "IT", "39", Continent.EUROPE),
    SPAIN("스페인", "ES", "34", Continent.EUROPE),
    NETHERLANDS("네덜란드", "NL", "31", Continent.EUROPE),
    BELGIUM("벨기에", "BE", "32", Continent.EUROPE),
    SWITZERLAND("스위스", "CH", "41", Continent.EUROPE),
    AUSTRIA("오스트리아", "AT", "43", Continent.EUROPE),
    SWEDEN("스웨덴", "SE", "46", Continent.EUROPE),
    NORWAY("노르웨이", "NO", "47", Continent.EUROPE),
    DENMARK("덴마크", "DK", "45", Continent.EUROPE),
    FINLAND("핀란드", "FI", "358", Continent.EUROPE),
    POLAND("폴란드", "PL", "48", Continent.EUROPE),
    CZECH_REPUBLIC("체코", "CZ", "420", Continent.EUROPE),
    SLOVAKIA("슬로바키아", "SK", "421", Continent.EUROPE),
    HUNGARY("헝가리", "HU", "36", Continent.EUROPE),
    ROMANIA("루마니아", "RO", "40", Continent.EUROPE),
    BULGARIA("불가리아", "BG", "359", Continent.EUROPE),
    GREECE("그리스", "GR", "30", Continent.EUROPE),
    PORTUGAL("포르투갈", "PT", "351", Continent.EUROPE),
    IRELAND("아일랜드", "IE", "353", Continent.EUROPE),
    ICELAND("아이슬란드", "IS", "354", Continent.EUROPE),
    LUXEMBOURG("룩셈부르크", "LU", "352", Continent.EUROPE),
    MALTA("몰타", "MT", "356", Continent.EUROPE),
    CYPRUS("키프로스", "CY", "357", Continent.EUROPE),
    RUSSIA("러시아", "RU", "7", Continent.EUROPE),
    UKRAINE("우크라이나", "UA", "380", Continent.EUROPE),
    BELARUS("벨라루스", "BY", "375", Continent.EUROPE),
    LITHUANIA("리투아니아", "LT", "370", Continent.EUROPE),
    LATVIA("라트비아", "LV", "371", Continent.EUROPE),
    ESTONIA("에스토니아", "EE", "372", Continent.EUROPE),
    ALBANIA("알바니아", "AL", "355", Continent.EUROPE),
    ANDORRA("안도라", "AD", "376", Continent.EUROPE),
    BOSNIA_AND_HERZEGOVINA("보스니아헤르체고비나", "BA", "387", Continent.EUROPE),
    CROATIA("크로아티아", "HR", "385", Continent.EUROPE),
    KOSOVO("코소보", "XK", "383", Continent.EUROPE),
    LIECHTENSTEIN("리히텐슈타인", "LI", "423", Continent.EUROPE),
    MOLDOVA("몰도바", "MD", "373", Continent.EUROPE),
    MONACO("모나코", "MC", "377", Continent.EUROPE),
    MONTENEGRO("몬테네그로", "ME", "382", Continent.EUROPE),
    NORTH_MACEDONIA("북마케도니아", "MK", "389", Continent.EUROPE),
    SAN_MARINO("산마리노", "SM", "378", Continent.EUROPE),
    SERBIA("세르비아", "RS", "381", Continent.EUROPE),
    SLOVENIA("슬로베니아", "SI", "386", Continent.EUROPE),
    VATICAN_CITY("바티칸시국", "VA", "39", Continent.EUROPE),

    // 북미 / North America
    UNITED_STATES("미국", "US", "1", Continent.NORTH_AMERICA),
    CANADA("캐나다", "CA", "1", Continent.NORTH_AMERICA),
    MEXICO("멕시코", "MX", "52", Continent.NORTH_AMERICA),
    GUATEMALA("과테말라", "GT", "502", Continent.NORTH_AMERICA),
    PANAMA("파나마", "PA", "507", Continent.NORTH_AMERICA),
    COSTA_RICA("코스타리카", "CR", "506", Continent.NORTH_AMERICA),
    NICARAGUA("니카라과", "NI", "505", Continent.NORTH_AMERICA),
    HONDURAS("온두라스", "HN", "504", Continent.NORTH_AMERICA),
    EL_SALVADOR("엘살바도르", "SV", "503", Continent.NORTH_AMERICA),
    BELIZE("벨리즈", "BZ", "501", Continent.NORTH_AMERICA),
    BAHAMAS("바하마", "BS", "1242", Continent.NORTH_AMERICA),
    BARBADOS("바베이도스", "BB", "1246", Continent.NORTH_AMERICA),
    CUBA("쿠바", "CU", "53", Continent.NORTH_AMERICA),
    DOMINICA("도미니카", "DM", "1767", Continent.NORTH_AMERICA),
    DOMINICAN_REPUBLIC("도미니카공화국", "DO", "1809", Continent.NORTH_AMERICA),
    GRENADA("그레나다", "GD", "1473", Continent.NORTH_AMERICA),
    HAITI("아이티", "HT", "509", Continent.NORTH_AMERICA),
    JAMAICA("자메이카", "JM", "1876", Continent.NORTH_AMERICA),
    SAINT_KITTS_AND_NEVIS("세인트키츠네비스", "KN", "1869", Continent.NORTH_AMERICA),
    SAINT_LUCIA("세인트루시아", "LC", "1758", Continent.NORTH_AMERICA),
    SAINT_VINCENT_AND_THE_GRENADINES("세인트빈센트그레나딘", "VC", "1784", Continent.NORTH_AMERICA),
    TRINIDAD_AND_TOBAGO("트리니다드토바고", "TT", "1868", Continent.NORTH_AMERICA),

    // 남미 / South America
    BRAZIL("브라질", "BR", "55", Continent.SOUTH_AMERICA),
    ARGENTINA("아르헨티나", "AR", "54", Continent.SOUTH_AMERICA),
    CHILE("칠레", "CL", "56", Continent.SOUTH_AMERICA),
    COLOMBIA("콜롬비아", "CO", "57", Continent.SOUTH_AMERICA),
    PERU("페루", "PE", "51", Continent.SOUTH_AMERICA),
    VENEZUELA("베네수엘라", "VE", "58", Continent.SOUTH_AMERICA),
    ECUADOR("에콰도르", "EC", "593", Continent.SOUTH_AMERICA),
    BOLIVIA("볼리비아", "BO", "591", Continent.SOUTH_AMERICA),
    PARAGUAY("파라과이", "PY", "595", Continent.SOUTH_AMERICA),
    URUGUAY("우루과이", "UY", "598", Continent.SOUTH_AMERICA),
    GUYANA("가이아나", "GY", "592", Continent.SOUTH_AMERICA),
    SURINAME("수리남", "SR", "597", Continent.SOUTH_AMERICA),

    // 아프리카 / Africa
    SOUTH_AFRICA("남아프리카공화국", "ZA", "27", Continent.AFRICA),
    EGYPT("이집트", "EG", "20", Continent.AFRICA),
    NIGERIA("나이지리아", "NG", "234", Continent.AFRICA),
    KENYA("케냐", "KE", "254", Continent.AFRICA),
    GHANA("가나", "GH", "233", Continent.AFRICA),
    ETHIOPIA("에티오피아", "ET", "251", Continent.AFRICA),
    MOROCCO("모로코", "MA", "212", Continent.AFRICA),
    ALGERIA("알제리", "DZ", "213", Continent.AFRICA),
    TUNISIA("튀니지", "TN", "216", Continent.AFRICA),
    LIBYA("리비아", "LY", "218", Continent.AFRICA),
    ANGOLA("앙골라", "AO", "244", Continent.AFRICA),
    BENIN("베냉", "BJ", "229", Continent.AFRICA),
    BOTSWANA("보츠와나", "BW", "267", Continent.AFRICA),
    BURKINA_FASO("부르키나파소", "BF", "226", Continent.AFRICA),
    BURUNDI("부룬디", "BI", "257", Continent.AFRICA),
    CAMEROON("카메룬", "CM", "237", Continent.AFRICA),
    CAPE_VERDE("카보베르데", "CV", "238", Continent.AFRICA),
    CENTRAL_AFRICAN_REPUBLIC("중앙아프리카공화국", "CF", "236", Continent.AFRICA),
    CHAD("차드", "TD", "235", Continent.AFRICA),
    COMOROS("코모로", "KM", "269", Continent.AFRICA),
    CONGO("콩고", "CG", "242", Continent.AFRICA),
    DEMOCRATIC_REPUBLIC_OF_THE_CONGO("콩고민주공화국", "CD", "243", Continent.AFRICA),
    DJIBOUTI("지부티", "DJ", "253", Continent.AFRICA),
    EQUATORIAL_GUINEA("적도기니", "GQ", "240", Continent.AFRICA),
    ERITREA("에리트레아", "ER", "291", Continent.AFRICA),
    ESWATINI("에스와티니", "SZ", "268", Continent.AFRICA),
    GABON("가봉", "GA", "241", Continent.AFRICA),
    GAMBIA("감비아", "GM", "220", Continent.AFRICA),
    GUINEA("기니", "GN", "224", Continent.AFRICA),
    GUINEA_BISSAU("기니비사우", "GW", "245", Continent.AFRICA),
    IVORY_COAST("코트디부아르", "CI", "225", Continent.AFRICA),
    LESOTHO("레소토", "LS", "266", Continent.AFRICA),
    LIBERIA("라이베리아", "LR", "231", Continent.AFRICA),
    MADAGASCAR("마다가스카르", "MG", "261", Continent.AFRICA),
    MALAWI("말라위", "MW", "265", Continent.AFRICA),
    MALI("말리", "ML", "223", Continent.AFRICA),
    MAURITANIA("모리타니", "MR", "222", Continent.AFRICA),
    MAURITIUS("모리셔스", "MU", "230", Continent.AFRICA),
    MOZAMBIQUE("모잠비크", "MZ", "258", Continent.AFRICA),
    NAMIBIA("나미비아", "NA", "264", Continent.AFRICA),
    NIGER("니제르", "NE", "227", Continent.AFRICA),
    RWANDA("르완다", "RW", "250", Continent.AFRICA),
    SAO_TOME_AND_PRINCIPE("상투메프린시페", "ST", "239", Continent.AFRICA),
    SENEGAL("세네갈", "SN", "221", Continent.AFRICA),
    SEYCHELLES("세이셸", "SC", "248", Continent.AFRICA),
    SIERRA_LEONE("시에라리온", "SL", "232", Continent.AFRICA),
    SOMALIA("소말리아", "SO", "252", Continent.AFRICA),
    SOUTH_SUDAN("남수단", "SS", "211", Continent.AFRICA),
    SUDAN("수단", "SD", "249", Continent.AFRICA),
    TANZANIA("탄자니아", "TZ", "255", Continent.AFRICA),
    TOGO("토고", "TG", "228", Continent.AFRICA),
    UGANDA("우간다", "UG", "256", Continent.AFRICA),
    ZAMBIA("잠비아", "ZM", "260", Continent.AFRICA),
    ZIMBABWE("짐바브웨", "ZW", "263", Continent.AFRICA),

    // 오세아니아 / Oceania
    AUSTRALIA("호주", "AU", "61", Continent.OCEANIA),
    NEW_ZEALAND("뉴질랜드", "NZ", "64", Continent.OCEANIA),
    FIJI("피지", "FJ", "679", Continent.OCEANIA),
    PAPUA_NEW_GUINEA("파푸아뉴기니", "PG", "675", Continent.OCEANIA),
    KIRIBATI("키리바시", "KI", "686", Continent.OCEANIA),
    MARSHALL_ISLANDS("마셜군도", "MH", "692", Continent.OCEANIA),
    MICRONESIA("미크로네시아", "FM", "691", Continent.OCEANIA),
    NAURU("나우루", "NR", "674", Continent.OCEANIA),
    PALAU("팔라우", "PW", "680", Continent.OCEANIA),
    SAMOA("사모아", "WS", "685", Continent.OCEANIA),
    SOLOMON_ISLANDS("솔로몬군도", "SB", "677", Continent.OCEANIA),
    TIMOR_LESTE("동티모르", "TL", "670", Continent.OCEANIA),
    TONGA("통가", "TO", "676", Continent.OCEANIA),
    TUVALU("투발루", "TV", "688", Continent.OCEANIA),
    VANUATU("바누아투", "VU", "678", Continent.OCEANIA);

    // ✅ 인스턴스 필드들 - 각 국가가 자신의 정보를 완벽히 소유
    private final String koreanName;   // 한글 국가명 / Korean country name
    private final String countryCode;  // 국가 코드 (ISO 3166-1 alpha-2) / Country code
    private final String dialingCode;  // 국제 전화번호 코드 / International dialing code
    private final Continent continent; // 대륙 정보 / Continent information

    /**
     * Countries 생성자 / Countries constructor
     *
     * @param koreanName  한글 국가명 / Korean country name
     * @param countryCode 국가 코드 / Country code
     * @param dialingCode 국제 전화번호 코드 / International dialing code
     * @param continent   대륙 정보 / Continent information
     */
    Countries(String koreanName, String countryCode, String dialingCode, Continent continent) {
        this.koreanName = koreanName;
        this.countryCode = countryCode;
        this.dialingCode = dialingCode;
        this.continent = continent;
    }

    // ===============================================
    // ✅ 인스턴스 메서드들 - 각 국가가 자신의 정보를 스스로 제공
    // ===============================================

    /**
     * 한글 국가명 반환 / Get Korean country name
     *
     * @return 한글 국가명 / Korean country name
     */
    public String getKoreanName() {
        return koreanName;
    }

    /**
     * 영문 국가명 반환 (enum 이름 기반) / Get English country name (based on enum name)
     *
     * @return 영문 국가명 / English country name
     */
    public String getEnglishName() {
        return this.name().replace("_", " ");
    }

    /**
     * 국가 코드 반환 / Get country code
     *
     * @return 국가 코드 / Country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * 국제 전화번호 코드 반환 / Get international dialing code
     *
     * @return 국제 전화번호 코드 / International dialing code
     */
    public String getDialingCode() {
        return dialingCode;
    }

    /**
     * ✅ 대륙 정보 반환 - 국가가 스스로 자신의 대륙을 알고 있음 / Get continent information - country knows its own continent
     *
     * @return 대륙 정보 / Continent information
     */
    public Continent getContinent() {
        return continent;
    }

    /**
     * 대륙 한글명 반환 / Get continent Korean name
     *
     * @return 대륙 한글명 / Continent Korean name
     */
    public String getContinentKoreanName() {
        return continent.getKoreanName();
    }

    /**
     * 대륙 영문명 반환 / Get continent English name
     *
     * @return 대륙 영문명 / Continent English name
     */
    public String getContinentEnglishName() {
        return continent.getEnglishName();
    }

    /**
     * 완전한 국제 전화번호 접두사 반환 / Get full international phone prefix
     *
     * @return +국가코드 형태 / +country_code format
     */
    public String getInternationalPrefix() {
        return "+" + dialingCode;
    }

    /**
     * ✅ 같은 대륙 국가인지 확인 / Check if same continent country
     *
     * @param other 비교할 국가 / Country to compare
     * @return 같은 대륙 여부 / Whether same continent
     */
    public boolean isSameContinent(Countries other) {
        return other != null && this.continent == other.continent;
    }

    /**
     * ✅ 같은 전화번호 코드를 사용하는지 확인 / Check if same dialing code
     *
     * @param other 비교할 국가 / Country to compare
     * @return 같은 전화번호 코드 사용 여부 / Whether using same dialing code
     */
    public boolean hasSameDialingCode(Countries other) {
        return other != null && this.dialingCode.equals(other.dialingCode);
    }

    /**
     * ✅ 특정 대륙에 속하는지 확인 / Check if belongs to specific continent
     *
     * @param continent 확인할 대륙 / Continent to check
     * @return 해당 대륙 소속 여부 / Whether belongs to the continent
     */
    public boolean belongsToContinent(Continent continent) {
        return this.continent == continent;
    }

    // ===============================================
    // ✅ 정적 메서드들 - 완전히 Stream 기반, 하드코딩 제거
    // ===============================================

    // 검색을 위한 룩업 맵 / Lookup map for search
    private static final Map<String, List<Countries>> LOOKUP_MAP = new HashMap<>();

    static {
        for (Countries country : Countries.values()) {
            // enum 이름으로 검색 / Search by enum name
            addCountryToLookup(country.name(), country);

            // 한글 국가명으로 검색 / Search by Korean name
            addCountryToLookup(country.getKoreanName(), country);

            // 영문 국가명으로 검색 / Search by English name
            addCountryToLookup(country.getEnglishName().toUpperCase().replace(" ", "_"), country);

            // 국가 코드로 검색 / Search by country code
            addCountryToLookup(country.getCountryCode(), country);

            // 국제 전화번호 코드로 검색 / Search by dialing code
            addCountryToLookup(country.getDialingCode(), country);
        }

        // 일반적인 별칭 추가 / Add common aliases
        addCountryToLookup("USA", Countries.UNITED_STATES);
        addCountryToLookup("US", Countries.UNITED_STATES);
        addCountryToLookup("America", Countries.UNITED_STATES);
        addCountryToLookup("미국", Countries.UNITED_STATES);
        addCountryToLookup("UK", Countries.UNITED_KINGDOM);
        addCountryToLookup("Britain", Countries.UNITED_KINGDOM);
        addCountryToLookup("영국", Countries.UNITED_KINGDOM);
        addCountryToLookup("한국", Countries.SOUTH_KOREA);
        addCountryToLookup("Korea", Countries.SOUTH_KOREA);
        addCountryToLookup("대한민국", Countries.SOUTH_KOREA);
    }

    /**
     * 문자열로 국가 찾기 / Find country by string
     *
     * @param value 검색할 문자열 / String to search
     * @return 해당하는 국가 enum / Corresponding country enum
     */
    public static Countries fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // 먼저 enum 이름으로 시도 / Try enum name first
            return Countries.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 룩업 맵에서 검색 / Search in lookup map
            List<Countries> found = LOOKUP_MAP.get(value.toUpperCase());
            return (found != null && !found.isEmpty()) ? found.get(0) : null;
        }
    }

    /**
     * 국가 코드로 국가 찾기 / Find country by country code
     *
     * @param countryCode 국가 코드 / Country code
     * @return 해당하는 국가 enum / Corresponding country enum
     */
    public static Countries fromCountryCode(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return null;
        }

        return Arrays.stream(Countries.values())
                .filter(country -> country.getCountryCode().equalsIgnoreCase(countryCode))
                .findFirst()
                .orElse(null);
    }

    /**
     * ✅ 국제 전화번호 코드로 모든 해당 국가 찾기 (중복 대응) / Find all countries by dialing code (duplicate support)
     *
     * @param dialingCode 국제 전화번호 코드 / International dialing code
     * @return 해당하는 모든 국가 목록 / List of all corresponding countries
     */
    public static List<Countries> findByDialingCode(String dialingCode) {
        if (dialingCode == null || dialingCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // +기호 제거 / Remove + sign
        String cleanCode = dialingCode.startsWith("+") ? dialingCode.substring(1) : dialingCode;

        return Arrays.stream(Countries.values())
                .filter(country -> country.getDialingCode().equals(cleanCode))
                .collect(Collectors.toList());
    }

    /**
     * ✅ 국제 전화번호 코드로 첫 번째 국가 찾기 (하위 호환성) / Find first country by dialing code (backward compatibility)
     *
     * @param dialingCode 국제 전화번호 코드 / International dialing code
     * @return 첫 번째 해당 국가 / First corresponding country
     */
    public static Countries fromDialingCode(String dialingCode) {
        List<Countries> countries = findByDialingCode(dialingCode);
        return countries.isEmpty() ? null : countries.get(0);
    }

    /**
     * ✅ 특정 대륙의 모든 국가 반환 (완전 Stream 기반) / Get all countries by continent (fully stream-based)
     *
     * @param continent 대륙 정보 / Continent information
     * @return 해당 대륙의 모든 국가 목록 / List of all countries in the continent
     */
    public static List<Countries> findByContinent(Continent continent) {
        if (continent == null || continent == Continent.UNKNOWN) {
            return Collections.emptyList();
        }

        return Arrays.stream(Countries.values())
                .filter(country -> country.belongsToContinent(continent))
                .collect(Collectors.toList());
    }

    /**
     * ✅ 대륙명으로 국가 찾기 (문자열 지원) / Find countries by continent name (string support)
     *
     * @param continentName 대륙명 / Continent name
     * @return 해당 대륙의 모든 국가 목록 / List of all countries in the continent
     */
    public static List<Countries> findByContinent(String continentName) {
        Continent continent = Continent.fromString(continentName);
        return findByContinent(continent);
    }

    /**
     * 룩업 맵에 국가 추가 / Add country to lookup map
     *
     * @param key     검색 키 / Search key
     * @param country 국가 enum / Country enum
     */
    private static void addCountryToLookup(String key, Countries country) {
        LOOKUP_MAP.computeIfAbsent(key.toUpperCase(), k -> new ArrayList<>()).add(country);
    }

    /**
     * 키워드로 국가 검색 / Search countries by keyword
     *
     * @param keyword 검색 키워드 / Search keyword
     * @return 매칭되는 국가 목록 / List of matching countries
     */
    public static List<Countries> findByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return LOOKUP_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().contains(keyword.toUpperCase()))
                .flatMap(entry -> entry.getValue().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * ✅ 모든 대륙 목록 반환 (Continent enum 기반) / Get all continent names (based on Continent enum)
     *
     * @return 대륙 목록 / List of continent names
     */
    public static List<String> getAllContinents() {
        return Arrays.stream(Continent.values())
                .filter(continent -> continent != Continent.UNKNOWN)
                .map(Continent::getKoreanName)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 대륙별 국가 통계 반환 (완전 자동화) / Get country statistics by continent (fully automated)
     *
     * @return 대륙별 국가 수 맵 / Map of country count by continent
     */
    public static Map<String, Long> getContinentStatistics() {
        return Arrays.stream(Countries.values())
                .collect(Collectors.groupingBy(
                        country -> country.getContinentKoreanName(),
                        Collectors.counting()
                ));
    }

    /**
     * ✅ 전화번호 코드 중복 현황 조회 (완전 자동화) / Get dialing code duplication status (fully automated)
     *
     * @return 전화번호 코드별 국가 목록 맵 / Map of countries by dialing code
     */
    public static Map<String, List<Countries>> getDialingCodeGroups() {
        return Arrays.stream(Countries.values())
                .collect(Collectors.groupingBy(Countries::getDialingCode));
    }

    /**
     * ✅ 중복 전화번호 코드만 조회 / Get only duplicate dialing codes
     *
     * @return 중복 전화번호 코드별 국가 목록 맵 / Map of countries with duplicate dialing codes
     */
    public static Map<String, List<Countries>> getDuplicateDialingCodes() {
        return getDialingCodeGroups().entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    // ===============================================
    // ✅ 하위 호환성을 위한 deprecated 메서드들
    // ===============================================

    /**
     * @deprecated {@link #findByContinent(Continent)} 사용 권장
     */
    @Deprecated
    public static List<Countries> getByContinent(Continent continent) {
        return findByContinent(continent);
    }

    /**
     * @deprecated {@link #findByContinent(String)} 사용 권장
     */
    @Deprecated
    public static List<Countries> getByContinent(String continentName) {
        return findByContinent(continentName);
    }

    /**
     * @deprecated {@link #findByDialingCode(String)} 사용 권장
     */
    @Deprecated
    public static List<Countries> fromDialingCodeList(String dialingCode) {
        return findByDialingCode(dialingCode);
    }

    /**
     * @deprecated {@link #findByKeyword(String)} 사용 권장
     */
    @Deprecated
    public static List<Countries> getByKeyword(String keyword) {
        return findByKeyword(keyword);
    }

    /**
     * @deprecated 각 국가의 {@link #getContinentKoreanName()} 사용 권장
     */
    @Deprecated
    public static String getContinentOf(Countries country) {
        return country != null ? country.getContinentKoreanName() : null;
    }
}
