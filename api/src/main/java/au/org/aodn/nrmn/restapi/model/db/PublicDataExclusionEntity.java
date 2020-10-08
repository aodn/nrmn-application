package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.composedID.ExcludedDataId;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name ="public_data_exclusion")
public class PublicDataExclusionEntity {

    @EmbeddedId
    private ExcludedDataId excludedDataId;

    @ManyToOne
    @MapsId("siteId")
    @JoinColumn(name = "site_id")
    private SiteRefEntity site;

    @ManyToOne
    @MapsId("programId")
    @JoinColumn(name = "program_id")
    private ProgramRefEntity program;
}
