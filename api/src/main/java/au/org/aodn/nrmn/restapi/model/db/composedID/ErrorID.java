package au.org.aodn.nrmn.restapi.model.db.composedID;

import lombok.*;
import org.springframework.lang.Nullable;

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
    private Long rowId;

    @Column(name = "job_id")
    private long jobId;
    @Column(name = "message")
    private String message;
}
