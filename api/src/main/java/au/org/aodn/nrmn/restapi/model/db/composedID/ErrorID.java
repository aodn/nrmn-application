package au.org.aodn.nrmn.restapi.model.db.composedID;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long jobId;
    @Column(name = "message")
    private String message;
}
