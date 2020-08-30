package au.org.aodn.nrmn.restapi.model.db;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "site_ref"  )
@Audited(withModifiedFlag = true)
@EqualsAndHashCode
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class SiteRefEntity {

    @Id
    @Column(name = "site_id")
    private int siteId;

    @Column(name = "site_code")
    private String siteCode;

    @Column(name = "site_name")
    private String siteName;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "site_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> siteAttribute;

    @Column(name = "is_active")
    private Boolean isActive;
}
