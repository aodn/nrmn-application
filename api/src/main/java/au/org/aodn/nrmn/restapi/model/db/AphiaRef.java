package au.org.aodn.nrmn.restapi.model.db;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Cache;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

@Entity
@Cache(region = "entities", usage = READ_WRITE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "aphia_ref")
public class AphiaRef {
    @Id
    @Column(name = "aphia_id", unique = true, nullable = false)
    @Schema(title = "Aphia id")
    private Integer aphiaId;

    @Basic
    @Column(name = "url")
    @Schema(title = "Url")
    private String url;

    @Basic
    @Column(name = "scientificname")
    @Schema(title = "Scientific name")
    private String scientificName;

    @Basic
    @Column(name = "authority")
    @Schema(title = "Authority")
    private String authority;

    @Basic
    @Schema(title = "Status")
    @Column(name = "status")
    private String status;

    @Basic
    @Column(name = "unacceptreason")
    @Schema(title = "Unaccept reason")
    private String unacceptReason;

    @Basic
    @Column(name = "taxon_rank_id")
    @Schema(title = "Taxon rank id")
    private Integer taxonRankId;

    @Basic
    @Column(name = "rank")
    @Schema(title = "Rank")
    private String rank;

    @Basic
    @Column(name = "valid_aphia_id")
    @Schema(title = "Valid aphia id")
    private Integer validAphiaId;

    @Basic
    @Column(name = "valid_name")
    @Schema(title = "Valid name")
    private String validName;

    @Basic
    @Schema(title = "Valid authority")
    @Column(name = "valid_authority")
    private String validAuthority;

    @Basic
    @Column(name = "parent_name_usage_id")
    @Schema(title = "Parent name usage id")
    private Integer parentNameUsageId;

    @Basic
    @Column(name = "rank_kingdom")
    @Schema(title = "Rank kingdom")
    private String kingdom;

    @Basic
    @Column(name = "rank_phylum")
    @Schema(title = "Rank phylum")
    private String phylum;

    @Basic
    @Column(name = "rank_class")
    @Schema(title = "Rank class")
    private String className;

    @Basic
    @Column(name = "rank_order")
    @Schema(title = "Rank order")
    private String order;

    @Basic
    @Column(name = "rank_family")
    @Schema(title = "Rank family")
    private String family;

    @Basic
    @Column(name = "rank_genus")
    @Schema(title = "Rank genus")
    private String rankGenus;

    @Basic
    @Column(name = "citation")
    @Schema(title = "Citation")
    private String citation;

    @Basic
    @Column(name = "lsid")
    @Schema(title = "Lsid")
    private String lsid;

    @Basic
    @Column(name = "is_marine")
    @Schema(title = "Marine")
    private Boolean isMarine;

    @Basic
    @Column(name = "is_brackish")
    @Schema(title = "Brackish")
    private Boolean isBrackish;

    @Basic
    @Column(name = "is_freshwater")
    @Schema(title = "Freshwater")
    private Boolean isFreshwater;

    @Basic
    @Column(name = "is_terrestrial")
    @Schema(title = "Terrestrial")
    private Boolean isTerrestrial;

    @Basic
    @Column(name = "is_extinct")
    @Schema(title = "Extinct")
    private Boolean isExtinct;

    @Basic
    @Column(name = "match_type")
    @Schema(title = "Match type")
    private String matchType;

    @Basic
    @Column(name = "modified")
    @Schema(title = "Modified")
    private Timestamp modified;
}
