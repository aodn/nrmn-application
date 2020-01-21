package au.org.aodn.nrmn.restapi.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "site_ref", schema = "nrmn", catalog = "nrmn")
public class SiteRefEntity {
    private int siteId;
    private String siteCode;
    private String siteName;
    private Double longitude;
    private Double latitude;
    private Json siteAttribute;
    private Boolean isActive;

    @Id
    @Column(name = "site_id")
    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    @Basic
    @Column(name = "site_code")
    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    @Basic
    @Column(name = "site_name")
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Basic
    @Column(name = "longitude")
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Basic
    @Column(name = "latitude")
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Basic
    @Column(name = "site_attribute")
    @Type(type = "JsonType")
    public Json getSiteAttribute() {
        return siteAttribute;
    }

    public void setSiteAttribute(Json siteAttribute) {
        this.siteAttribute = siteAttribute;
    }

    @Basic
    @Column(name = "is_active")
    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SiteRefEntity that = (SiteRefEntity) o;

        if (siteId != that.siteId) return false;
        if (siteCode != null ? !siteCode.equals(that.siteCode) : that.siteCode != null) return false;
        if (siteName != null ? !siteName.equals(that.siteName) : that.siteName != null) return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        if (siteAttribute != null ? !siteAttribute.equals(that.siteAttribute) : that.siteAttribute != null)
            return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = siteId;
        result = 31 * result + (siteCode != null ? siteCode.hashCode() : 0);
        result = 31 * result + (siteName != null ? siteName.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (siteAttribute != null ? siteAttribute.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
