package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.validation.ValidationLevelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "error_check")
public class ErrorCheckEntity {
    @EmbeddedId
    @JsonUnwrapped
    public ErrorID id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    public ValidationLevelType Type;

    @Column(name = "column_target")
    public String ColunmTarget;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
     public RawSurveyEntity rawSurveyEntity;
}
