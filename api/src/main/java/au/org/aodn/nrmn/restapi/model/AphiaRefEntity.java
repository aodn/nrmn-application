package au.org.aodn.nrmn.restapi.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "aphia_ref", schema = "nrmn", catalog = "nrmn")
public class AphiaRefEntity {
    private int aphiaId;
    private String url;
    private String scientificname;
    private String authority;
    private String status;
    private String unacceptreason;
    private Integer taxonRankId;
    private String rank;
    private Integer validAphiaId;
    private String validName;
    private String validAuthority;
    private Integer parentNameUsageId;
    private String rankKingdom;
    private String rankPhylum;
    private String rankClass;
    private String rankOrder;
    private String rankFamily;
    private String rankGenus;
    private String citation;
    private String lsid;
    private Boolean isMarine;
    private Boolean isBrackish;
    private Boolean isFreshwater;
    private Boolean isTerrestrial;
    private Boolean isExtinct;
    private String matchType;
    private Timestamp modified;

    @Id
    @Column(name = "aphia_id")
    public int getAphiaId() {
        return aphiaId;
    }

    public void setAphiaId(int aphiaId) {
        this.aphiaId = aphiaId;
    }

    @Basic
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "scientificname")
    public String getScientificname() {
        return scientificname;
    }

    public void setScientificname(String scientificname) {
        this.scientificname = scientificname;
    }

    @Basic
    @Column(name = "authority")
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "unacceptreason")
    public String getUnacceptreason() {
        return unacceptreason;
    }

    public void setUnacceptreason(String unacceptreason) {
        this.unacceptreason = unacceptreason;
    }

    @Basic
    @Column(name = "taxon_rank_id")
    public Integer getTaxonRankId() {
        return taxonRankId;
    }

    public void setTaxonRankId(Integer taxonRankId) {
        this.taxonRankId = taxonRankId;
    }

    @Basic
    @Column(name = "rank")
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Basic
    @Column(name = "valid_aphia_id")
    public Integer getValidAphiaId() {
        return validAphiaId;
    }

    public void setValidAphiaId(Integer validAphiaId) {
        this.validAphiaId = validAphiaId;
    }

    @Basic
    @Column(name = "valid_name")
    public String getValidName() {
        return validName;
    }

    public void setValidName(String validName) {
        this.validName = validName;
    }

    @Basic
    @Column(name = "valid_authority")
    public String getValidAuthority() {
        return validAuthority;
    }

    public void setValidAuthority(String validAuthority) {
        this.validAuthority = validAuthority;
    }

    @Basic
    @Column(name = "parent_name_usage_id")
    public Integer getParentNameUsageId() {
        return parentNameUsageId;
    }

    public void setParentNameUsageId(Integer parentNameUsageId) {
        this.parentNameUsageId = parentNameUsageId;
    }

    @Basic
    @Column(name = "rank_kingdom")
    public String getRankKingdom() {
        return rankKingdom;
    }

    public void setRankKingdom(String rankKingdom) {
        this.rankKingdom = rankKingdom;
    }

    @Basic
    @Column(name = "rank_phylum")
    public String getRankPhylum() {
        return rankPhylum;
    }

    public void setRankPhylum(String rankPhylum) {
        this.rankPhylum = rankPhylum;
    }

    @Basic
    @Column(name = "rank_class")
    public String getRankClass() {
        return rankClass;
    }

    public void setRankClass(String rankClass) {
        this.rankClass = rankClass;
    }

    @Basic
    @Column(name = "rank_order")
    public String getRankOrder() {
        return rankOrder;
    }

    public void setRankOrder(String rankOrder) {
        this.rankOrder = rankOrder;
    }

    @Basic
    @Column(name = "rank_family")
    public String getRankFamily() {
        return rankFamily;
    }

    public void setRankFamily(String rankFamily) {
        this.rankFamily = rankFamily;
    }

    @Basic
    @Column(name = "rank_genus")
    public String getRankGenus() {
        return rankGenus;
    }

    public void setRankGenus(String rankGenus) {
        this.rankGenus = rankGenus;
    }

    @Basic
    @Column(name = "citation")
    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    @Basic
    @Column(name = "lsid")
    public String getLsid() {
        return lsid;
    }

    public void setLsid(String lsid) {
        this.lsid = lsid;
    }

    @Basic
    @Column(name = "is_marine")
    public Boolean getMarine() {
        return isMarine;
    }

    public void setMarine(Boolean marine) {
        isMarine = marine;
    }

    @Basic
    @Column(name = "is_brackish")
    public Boolean getBrackish() {
        return isBrackish;
    }

    public void setBrackish(Boolean brackish) {
        isBrackish = brackish;
    }

    @Basic
    @Column(name = "is_freshwater")
    public Boolean getFreshwater() {
        return isFreshwater;
    }

    public void setFreshwater(Boolean freshwater) {
        isFreshwater = freshwater;
    }

    @Basic
    @Column(name = "is_terrestrial")
    public Boolean getTerrestrial() {
        return isTerrestrial;
    }

    public void setTerrestrial(Boolean terrestrial) {
        isTerrestrial = terrestrial;
    }

    @Basic
    @Column(name = "is_extinct")
    public Boolean getExtinct() {
        return isExtinct;
    }

    public void setExtinct(Boolean extinct) {
        isExtinct = extinct;
    }

    @Basic
    @Column(name = "match_type")
    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    @Basic
    @Column(name = "modified")
    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AphiaRefEntity that = (AphiaRefEntity) o;

        if (aphiaId != that.aphiaId) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (scientificname != null ? !scientificname.equals(that.scientificname) : that.scientificname != null)
            return false;
        if (authority != null ? !authority.equals(that.authority) : that.authority != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (unacceptreason != null ? !unacceptreason.equals(that.unacceptreason) : that.unacceptreason != null)
            return false;
        if (taxonRankId != null ? !taxonRankId.equals(that.taxonRankId) : that.taxonRankId != null) return false;
        if (rank != null ? !rank.equals(that.rank) : that.rank != null) return false;
        if (validAphiaId != null ? !validAphiaId.equals(that.validAphiaId) : that.validAphiaId != null) return false;
        if (validName != null ? !validName.equals(that.validName) : that.validName != null) return false;
        if (validAuthority != null ? !validAuthority.equals(that.validAuthority) : that.validAuthority != null)
            return false;
        if (parentNameUsageId != null ? !parentNameUsageId.equals(that.parentNameUsageId) : that.parentNameUsageId != null)
            return false;
        if (rankKingdom != null ? !rankKingdom.equals(that.rankKingdom) : that.rankKingdom != null) return false;
        if (rankPhylum != null ? !rankPhylum.equals(that.rankPhylum) : that.rankPhylum != null) return false;
        if (rankClass != null ? !rankClass.equals(that.rankClass) : that.rankClass != null) return false;
        if (rankOrder != null ? !rankOrder.equals(that.rankOrder) : that.rankOrder != null) return false;
        if (rankFamily != null ? !rankFamily.equals(that.rankFamily) : that.rankFamily != null) return false;
        if (rankGenus != null ? !rankGenus.equals(that.rankGenus) : that.rankGenus != null) return false;
        if (citation != null ? !citation.equals(that.citation) : that.citation != null) return false;
        if (lsid != null ? !lsid.equals(that.lsid) : that.lsid != null) return false;
        if (isMarine != null ? !isMarine.equals(that.isMarine) : that.isMarine != null) return false;
        if (isBrackish != null ? !isBrackish.equals(that.isBrackish) : that.isBrackish != null) return false;
        if (isFreshwater != null ? !isFreshwater.equals(that.isFreshwater) : that.isFreshwater != null) return false;
        if (isTerrestrial != null ? !isTerrestrial.equals(that.isTerrestrial) : that.isTerrestrial != null)
            return false;
        if (isExtinct != null ? !isExtinct.equals(that.isExtinct) : that.isExtinct != null) return false;
        if (matchType != null ? !matchType.equals(that.matchType) : that.matchType != null) return false;
        if (modified != null ? !modified.equals(that.modified) : that.modified != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = aphiaId;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (scientificname != null ? scientificname.hashCode() : 0);
        result = 31 * result + (authority != null ? authority.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (unacceptreason != null ? unacceptreason.hashCode() : 0);
        result = 31 * result + (taxonRankId != null ? taxonRankId.hashCode() : 0);
        result = 31 * result + (rank != null ? rank.hashCode() : 0);
        result = 31 * result + (validAphiaId != null ? validAphiaId.hashCode() : 0);
        result = 31 * result + (validName != null ? validName.hashCode() : 0);
        result = 31 * result + (validAuthority != null ? validAuthority.hashCode() : 0);
        result = 31 * result + (parentNameUsageId != null ? parentNameUsageId.hashCode() : 0);
        result = 31 * result + (rankKingdom != null ? rankKingdom.hashCode() : 0);
        result = 31 * result + (rankPhylum != null ? rankPhylum.hashCode() : 0);
        result = 31 * result + (rankClass != null ? rankClass.hashCode() : 0);
        result = 31 * result + (rankOrder != null ? rankOrder.hashCode() : 0);
        result = 31 * result + (rankFamily != null ? rankFamily.hashCode() : 0);
        result = 31 * result + (rankGenus != null ? rankGenus.hashCode() : 0);
        result = 31 * result + (citation != null ? citation.hashCode() : 0);
        result = 31 * result + (lsid != null ? lsid.hashCode() : 0);
        result = 31 * result + (isMarine != null ? isMarine.hashCode() : 0);
        result = 31 * result + (isBrackish != null ? isBrackish.hashCode() : 0);
        result = 31 * result + (isFreshwater != null ? isFreshwater.hashCode() : 0);
        result = 31 * result + (isTerrestrial != null ? isTerrestrial.hashCode() : 0);
        result = 31 * result + (isExtinct != null ? isExtinct.hashCode() : 0);
        result = 31 * result + (matchType != null ? matchType.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        return result;
    }
}
