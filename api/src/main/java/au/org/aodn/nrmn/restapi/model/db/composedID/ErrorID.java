package au.org.aodn.nrmn.restapi.model.db.composedID;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

//error message unique per cell
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ErrorID implements Serializable {
    private long surveyId;
    private String jobId;
    private String Message;
}
