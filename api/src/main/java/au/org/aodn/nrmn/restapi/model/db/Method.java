package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "method_ref")
public class Method {
    @Id
    @Column(name = "method_id", unique = true, nullable = false)
    private Integer methodId;

    @Basic
    @Column(name = "method_name")
    private String methodName;

    @Basic
    @Column(name = "is_active")
    private Boolean isActive;
}
