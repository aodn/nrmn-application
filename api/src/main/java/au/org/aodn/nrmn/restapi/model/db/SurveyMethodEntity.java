package au.org.aodn.nrmn.restapi.model.db;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Audited(withModifiedFlag = true)
@Table(name = "survey_method"  )
public class SurveyMethodEntity  {
    @Id
    @Column(name = "survey_method_id")
    private int surveyMethodId;

    @Column(name = "block_num")
    private Integer blockNum;

    @Column(name = "survey_not_done")
    private Boolean surveyNotDone;
}
