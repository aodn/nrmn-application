package au.org.aodn.nrmn.restapi.data.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lengthweight_ref")
public class LengthWeight {
    @Id
    @Column(name = "observable_item_id")
    private Integer id;

    @Basic
    @Column(name = "a")
    private Double a;

    @Basic
    @Column(name = "b")
    private Double b;

    @Basic
    @Column(name = "cf")
    private Double cf;

    @Basic
    @Column(name = "sgfgu")
    private String sgfgu;
}
