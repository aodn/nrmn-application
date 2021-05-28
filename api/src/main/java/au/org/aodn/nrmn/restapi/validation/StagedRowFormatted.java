package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
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

    private Optional<LocalTime> time;

    private Diver diver;


    private Diver pqs;

    private Integer depth;

    private Optional<Integer> surveyNum;

    private Integer method;

    private Integer block;

    private Optional<ObservableItem> species;

    private Optional<Integer> vis;

    private Directions direction;

    private Double latitude;

    private Double longitude;

    private String code;

    private Integer total;

    private Integer inverts;

    private Optional<Boolean> isInvertSizing;

    private StagedRow ref;

    private Map<Integer, Integer> measureJson;

    private Optional<UiSpeciesAttributes> speciesAttributesOpt;
}
