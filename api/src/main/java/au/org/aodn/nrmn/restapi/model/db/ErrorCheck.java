package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "error_check")
public class ErrorCheck {

    @EmbeddedId
    @JsonUnwrapped
    private ErrorID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type")
    private ValidationCategory errorType;

    @Column(name = "column_target")
    private String ColunmTarget;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rowId")
    private StagedSurvey row;
}
