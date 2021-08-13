package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StagedRowFormatted {
    private long id;

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
    
    public String getSurvey() {
        return site != null && site.getSiteCode() != null && date != null && depth != null ? (site.getSiteCode() + "/" + date + "/" + depth).toUpperCase() : null;
    }

    public String getSurveyGroup() {
        return getSurvey() != null ? (getSurvey() + "." + surveyNum).toUpperCase() : null;
    }
}
