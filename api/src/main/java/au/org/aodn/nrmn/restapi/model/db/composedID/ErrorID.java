package au.org.aodn.nrmn.restapi.model.db.composedID;

import lombok.*;

import javax.persistence.Column;
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
    @Column(name = "row_id")
    private long rowId;
    @Column(name = "job_id")
    private String jobId;
    @Column(name = "message")
    private String message;
}
