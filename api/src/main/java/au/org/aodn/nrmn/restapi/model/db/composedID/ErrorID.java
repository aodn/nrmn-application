package au.org.aodn.nrmn.restapi.model.db.composedID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

//error message unique per cell
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ErrorID implements Serializable {
    public long stageSurveyID;
    public String FileID;
    public String Message;
}
