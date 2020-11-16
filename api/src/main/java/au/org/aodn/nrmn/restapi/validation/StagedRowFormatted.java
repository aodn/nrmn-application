package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Getter
@NoArgsConstructor
public class StagedRowFormatted {
    private long id;

    private Site site;

    private Date date;

    private Diver diver;

    private Diver buddy;

    private Diver pqs;

    private Double depth;

    private Integer method;

    private Integer block;

    private AphiaRef species;


    private Integer vis;

    private Directions direction;


    private Integer code;

    private Integer total;

    private Integer inverts;

    private Integer m2InvertSizingSpecies;

    private Integer l5;

    private Integer l95;

    private Boolean isInvertSizing;

    private Integer lmax;

    private Map<String, Integer> measureJson;
}
