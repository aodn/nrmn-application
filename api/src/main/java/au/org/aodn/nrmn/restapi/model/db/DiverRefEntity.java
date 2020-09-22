package au.org.aodn.nrmn.restapi.model.db;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.envers.*;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited(withModifiedFlag = true)
@Table(name = "diver_ref")
@EqualsAndHashCode
@Description("Diver who performs survey")
public class DiverRefEntity {
    @Id
    @Column(name = "diver_id")
    private int diverId;

    @Column(name = "initials")
    @Description("test")
    @NotNull
    private String initials;

    @Column(name = "full_name")
    @NotNull
    @Size(min = 2, max= 10)
    @Description("fullname:test")
    private String fullName;
}
