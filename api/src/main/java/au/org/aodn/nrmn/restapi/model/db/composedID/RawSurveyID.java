package au.org.aodn.nrmn.restapi.model.db.composedID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RawSurveyID implements Serializable {
    @JsonProperty(value = "ID")
    @Column(name = "id")
    public Integer id;

    @Column(name = "file_id")
    public String fileID;

}
