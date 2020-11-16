package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aphia_ref")
public class AphiaRef {
    @Id
    @Column(name = "aphia_id", unique = true, nullable = false)
    private Integer aphiaId;

    @Basic
    @Column(name = "url")
    private String url;

    @Basic
    @Column(name = "scientificname")
    private String scientificName;

    @Basic
    @Column(name = "authority")
    private String authority;

    @Basic
    @Column(name = "status")
    private String status;

    @Basic
    @Column(name = "unacceptreason")
    private String unacceptReason;

    @Basic
    @Column(name = "taxon_rank_id")
    private Integer taxonRankId;

    @Basic
    @Column(name = "rank")
    private String rank;

    @Basic
    @Column(name = "valid_aphia_id")
    private Integer validAphiaId;

    @Basic
    @Column(name = "valid_name")
    private String validName;

    @Basic
    @Column(name = "valid_authority")
    private String validAuthority;

    @Basic
    @Column(name = "parent_name_usage_id")
    private Integer parentNameUsageId;

    @Basic
    @Column(name = "rank_kingdom")
    private String rankKingdom;

    @Basic
    @Column(name = "rank_phylum")
    private String rankPhylum;

    @Basic
    @Column(name = "rank_class")
    private String rankClass;

    @Basic
    @Column(name = "rank_order")
    private String rankOrder;

    @Basic
    @Column(name = "rank_family")
    private String rankFamily;

    @Basic
    @Column(name = "rank_genus")
    private String rankGenus;

    @Basic
    @Column(name = "citation")
    private String citation;

    @Basic
    @Column(name = "lsid")
    private String lsid;

    @Basic
    @Column(name = "is_marine")
    private Boolean isMarine;

    @Basic
    @Column(name = "is_brackish")
    private Boolean isBrackish;

    @Basic
    @Column(name = "is_freshwater")
    private Boolean isFreshwater;

    @Basic
    @Column(name = "is_terrestrial")
    private Boolean isTerrestrial;

    @Basic
    @Column(name = "is_extinct")
    private Boolean isExtinct;

    @Basic
    @Column(name = "match_type")
    private String matchType;

    @Basic
    @Column(name = "modified")
    private Timestamp modified;
}
