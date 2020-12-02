package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited(withModifiedFlag = true)
public class LengthWeight {
    @Basic
    @Column(name = "a", table ="lengthweight_ref")
    private Double a;

    @Basic
    @Column(name = "b", table ="lengthweight_ref")
    private Double b;

    @Basic
    @Column(name = "cf", table ="lengthweight_ref")
    private Double cf;

    @Basic
    @Column(name = "sgfgu", table ="lengthweight_ref")
    private String sgfgu;
}
