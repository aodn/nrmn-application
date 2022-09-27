package au.org.aodn.nrmn.restapi.enums;

import java.util.Arrays;

public enum SurveyField {
    UNKNOWN("UNKNOWN"),
    ID("ID"), BUDDY("Buddy"), INVERTS("Inverts"), DIVER("Diver"),
    SITE_NO("Site No."), SITE_NAME("Site Name"), LATITUDE("Latitude"), LONGITUDE("Longitude"),
    DATE("Date"), VIS("Vis"), DIRECTION("Direction"), TIME("Time"), P_QS("P-Qs"), DEPTH("Depth"),
    METHOD("Method"), BLOCK("Block"), CODE("Code"), SPECIES("Species"), COMMON_NAME("Common Name"),
    TOTAL("Total"), USE_INVERT_SIZING("Use InvertSizing"), M2_INVERT_SIZING_SPECIES("M2 Invert Sizing Species"),
    L5("L5"), L95("L95"), LMAX("Lmax"),
    TWO_FIVE("2.5", Boolean.TRUE, 1), FIVE("5", Boolean.TRUE, 2), SEVEN_FIVE("7.5", Boolean.TRUE, 3),
    TEN("10", Boolean.TRUE, 4), TWELVE_FIVE("12.5", Boolean.TRUE, 5), FIFTEEN("15", Boolean.TRUE, 6),
    TWENTY("20", Boolean.TRUE, 7), TWENTY_FIVE("25", Boolean.TRUE, 8), THIRTY("30", Boolean.TRUE, 9),
    THIRTY_FIVE("35", Boolean.TRUE, 10), FORTY("40", Boolean.TRUE, 11), FIFTY("50", Boolean.TRUE, 12),
    SIXTY_TWO_FIVE("62.5", Boolean.TRUE, 13), SEVENTY_FIVE("75", Boolean.TRUE, 14),
    EIGHTY_SEVEN_FIVE("87.5", Boolean.TRUE, 15), HUNDRED("100", Boolean.TRUE, 16),
    HUNDRED_TWELVE_FIVE("112.5", Boolean.TRUE, 17), HUNDRED_TWENTY_FIVE("125", Boolean.TRUE, 18),
    HUNDRED_THIRTY_SEVEN_FIVE("137.5", Boolean.TRUE, 19), HUNDRED_FIFTY("150", Boolean.TRUE, 20),
    HUNDRED_SIXTY_TWO_FIVE("162.5", Boolean.TRUE, 21), HUNDRED_SEVENTY_FIVE("175", Boolean.TRUE, 22),
    HUNDRED_EIGHTY_SEVEN_FIVE("187.5", Boolean.TRUE, 23), TWO_HUNDRED("200", Boolean.TRUE, 24),
    TWO_HUNDRED_FIFTY("250", Boolean.TRUE, 25), THREE_HUNDRED("300", Boolean.TRUE, 26),
    THREE_HUNDRED_FIFTY("350", Boolean.TRUE, 27), FOUR_HUNDRED("400", Boolean.TRUE, 28),
    FOUR_HUNDRED_FIFTY("450", Boolean.TRUE, 29), FIVE_HUNDRED("500", Boolean.TRUE, 30),
    FIFE_HUNDRED_FIFTY("550", Boolean.TRUE, 31), SIX_HUNDRED("600", Boolean.TRUE, 32),
    SIX_HUNDRED_FIFTY("650", Boolean.TRUE, 33), SEVEN_HUNDRED("700", Boolean.TRUE, 34),
    SEVEN_HUNDRED_FIFTY("750", Boolean.TRUE, 35), EIGHT_HUNDRED("800", Boolean.TRUE, 36),
    EIGHT_HUNDRED_FIFTY("850", Boolean.TRUE, 37), NINE_HUNDRED("900", Boolean.TRUE, 38),
    NINE_HUNDRED_FIFTY("950", Boolean.TRUE, 39), THOUSAND("1000", Boolean.TRUE, 40);


    private final String val;
    private final Boolean measurement;
    private final Integer pos;

    SurveyField(String v) {
        this(v, Boolean.FALSE, -1);
    }

    SurveyField(String v, Boolean measurement, Integer pos) {
        this.val = v;
        this.measurement = measurement;
        this.pos = pos;
    }

    public static SurveyField getEnum(final String v) {
        return Arrays.stream(SurveyField.values())
                // Avoid UI export and excel header cases sensitive issue
                .filter(p -> v != null && p.val.equalsIgnoreCase(v.trim()))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public Boolean isMeasurement() {
        return measurement;
    }

    public Integer getPosition() {
        return pos;
    }

    @Override
    public String toString() {
        return val;
    }
}
