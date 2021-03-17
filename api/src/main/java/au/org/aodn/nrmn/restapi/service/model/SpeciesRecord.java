package au.org.aodn.nrmn.restapi.service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class SpeciesRecord {
    @JsonAlias("AphiaID")
    private Integer aphiaId;

    private String url;

    @JsonAlias("scientificname")
    private String scientificName;

    private String authority;

    private String status;

    @JsonAlias("unacceptreason")
    private String unacceptReason;

    @JsonAlias("taxonRankID")
    private Integer taxonRankId;

    private String rank;

    @JsonAlias("valid_AphiaID")
    private Integer validAphiaId;

    @JsonAlias("valid_name")
    private String validName;

    @JsonAlias("valid_authority")
    private String validAuthority;

    @JsonAlias("parentNameUsageID")
    private Integer parentNameUsageId;

    @JsonAlias("kingdom")
    private String rankKingdom;

    @JsonAlias("phylum")
    private String rankPhylum;

    @JsonAlias("class")
    private String rankClass;

    @JsonAlias("order")
    private String rankOrder;

    @JsonAlias("family")
    private String rankFamily;

    @JsonAlias("genus")
    private String rankGenus;

    private String citation;

    private String lsid;

    private Boolean isMarine;

    private Boolean isBrackish;

    private Boolean isFreshwater;

    private Boolean isTerrestrial;

    private Boolean isExtinct;

    @JsonAlias("match_type")
    private String matchType;

    private Timestamp modified;
}
