package au.org.aodn.nrmn.restapi.model.db;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.hibernate.annotations.Cache;
import org.hibernate.envers.Audited;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Cache(region = "entities", usage = READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "diver_ref")
@Audited(withModifiedFlag = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Diver {
    @Id
    @SequenceGenerator(name = "diver_ref_diver_id", sequenceName = "diver_ref_diver_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="diver_ref_diver_id")
    @Column(name = "diver_id", unique = true, updatable = false, nullable = false)
    @Schema(title = "Id", accessMode = Schema.AccessMode.READ_ONLY, hidden = true)
    private Integer diverId;

    @Column(name = "initials")
    @Schema(title = "Initials")
    @NotNull
    @NotBlank
    private String initials;

    @Column(name = "full_name")
    @Schema(title = "Full name")
    @NotNull
    @NotBlank
    private String fullName;

    public Diver(String initials, String fullName) {
        this.initials = initials;
        this.fullName = fullName;
    }
}
