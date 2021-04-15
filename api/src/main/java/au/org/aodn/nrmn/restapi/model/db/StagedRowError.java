package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "staged_row_error")
public class StagedRowError {

    @EmbeddedId
    @JsonUnwrapped
    private ErrorID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type")
    private ValidationCategory errorType;


    @Enumerated(EnumType.STRING)
    @Column(name = "error_level")
    private ValidationLevel errorLevel;

    @Column(name = "column_target")
    private String columnTarget;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rowId")
    @EqualsAndHashCode.Exclude
    @JoinColumn(foreignKey = @ForeignKey(name = "staged_row_error_staged_row_id_fkey"))
    private StagedRow row;
}
