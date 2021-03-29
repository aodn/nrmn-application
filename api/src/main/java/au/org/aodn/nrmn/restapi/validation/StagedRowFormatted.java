package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
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

    private Diver buddy;

    private Diver pqs;

    private Integer depth;

    private  Integer surveyNum;

    private Integer method;

    private Integer block;

    private ObservableItem species;


    private Optional<Integer> vis;

    private Directions direction;


    private String code;

    private Integer total;

    private Integer inverts;

    private Integer m2InvertSizingSpecies;

    private Double l5;

    private Double l95;

    private Boolean isInvertSizing;

    private Double lMax;

    private StagedRow ref;

    private Map<Integer, Integer> measureJson;
}
