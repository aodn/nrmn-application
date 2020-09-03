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
@Table(name = "observable_item_ref"  )
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ObservableItemRefEntity  {

    @Id
    @Column(name = "observable_item_id")
    private int observableItemId;

    @Column(name = "observable_item_name")
    private String observableItemName;

    @Column(name = "obs_item_attribute")
    @Type(type = "jsonb")
    private Map<String,String> obsItemAttribute;
}
