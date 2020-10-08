package au.org.aodn.nrmn.restapi.model.db;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Audited(withModifiedFlag = true)
@Table(name = "observation")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ObservationEntity {
    @Id
    @Column(name = "observation_id")
    private int observationId;

    @Column(name = "measure_value")
    private Integer measureValue;

    @Column(name = "observation_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> observationAttribute;
}
