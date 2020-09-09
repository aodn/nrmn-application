package au.org.aodn.nrmn.restapi.model.db.composedID;

import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExcludedDataId  implements Serializable {
    private int programId;
    private int siteId;

}
