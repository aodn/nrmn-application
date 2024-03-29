package au.org.aodn.nrmn.restapi.service.validation;

import lombok.Data;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import au.org.aodn.nrmn.restapi.data.model.Diver;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.enums.Directions;

@Data
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StagedRowFormatted {
    private Long id;

    private Long surveyId;

    private Site site;

    private LocalDate date;

    @Builder.Default
    private Optional<LocalTime> time = Optional.empty();

    private Diver diver;

    private Diver pqs;

    private Integer depth;

    private Integer surveyNum;

    private Integer method;

    private Integer block;

    @Builder.Default
    private Optional<ObservableItem> species = Optional.empty();

    @Builder.Default
    private Optional<Double> vis = Optional.empty();

    private Directions direction;

    private Double latitude;

    private Double longitude;

    private String code;

    private Integer total;

    private Integer inverts;

    private Boolean isInvertSizing;

    private StagedRow ref;

    private Map<Integer, Integer> measureJson;

    @Builder.Default
    private Optional<UiSpeciesAttributes> speciesAttributesOpt = Optional.empty();

    public boolean isDuplicateOf(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StagedRowFormatted that = (StagedRowFormatted) o;
        return Objects.equals(date, that.date)
                && Objects.equals(depth, that.depth)
                && Objects.equals(surveyNum, that.surveyNum)
                && Objects.equals(diver, that.diver)
                && Objects.equals(site, that.site)
                && Objects.equals(block, that.block)
                && Objects.equals(method, that.method)
                && Objects.equals(species, that.species);
    }

    public Boolean isDebrisZero() {
        return ((code != null && code.equalsIgnoreCase("DEZ")) || (ref != null && ref.getSpecies().equalsIgnoreCase("Debris - Zero")));
    }

    public Boolean isSurveyNotDone() {
        return ((code != null && code.equalsIgnoreCase("SND")) || (ref != null && ref.getSpecies().equalsIgnoreCase("Survey Not Done")));
    }

    public String getMethodBlock() {
        return method.toString() + '-' + block.toString();
    }

    public String getSurvey() {
        return ref.getSurvey();
    }

    public String getSurveyGroup() {
        return ref.getSurveyGroup();
    }

    public String getDecimalDepth() {
        return String.format("%s.%d", getDepth(), getSurveyNum());
    }


    public String getDecimalSurvey() {
        return String.format("[%s, %s, %s]", getRef().getSiteCode(),  getDate(), getDecimalDepth());
    }

    public Integer observationTotal() { 
        return getMeasureJson().entrySet().stream().mapToInt(Map.Entry::getValue).sum() + ((!getMeasureJson().containsKey(0) && getInverts() != null) ? getInverts() : 0);
    }
}
