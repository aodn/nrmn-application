package au.org.aodn.nrmn.restapi.data.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
