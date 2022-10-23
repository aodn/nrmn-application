package au.org.aodn.nrmn.restapi.enums;

public class MeasureType {
    public static final Integer FishSizeClass = 1;
    public static final Integer InSituQuadrat = 2;
    public static final Integer MacrocystisBlock = 3;
    public static final Integer InvertSizeClass = 4;
    public static final Integer SingleItem = 5;
    public static final Integer Absence = 6;
    public static final Integer LimpetQuadrat = 7;

    public static Integer fromMethodId(Integer methodId) {
        switch (methodId) {
            case SurveyMethod.M3:
                return InSituQuadrat;
            case SurveyMethod.M4:
                return MeasureType.MacrocystisBlock;
            case SurveyMethod.M5:
                return MeasureType.LimpetQuadrat;
            case SurveyMethod.M12:
                return MeasureType.SingleItem;
            default:
                return MeasureType.FishSizeClass;
        }
    }
}
