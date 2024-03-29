package au.org.aodn.nrmn.restapi.data.model;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

import java.time.LocalDateTime;

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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diver_ref_diver_id")
    @Column(name = "diver_id", unique = true, updatable = false, nullable = false)
    private Integer diverId;

    @Column(name = "initials")
    @NotNull
    @NotBlank
    private String initials;

    @Column(name = "full_name")
    @NotNull
    @NotBlank
    private String fullName;

    @CreationTimestamp 
    @Column(name = "created", updatable = false)
    public LocalDateTime created;

    public Diver(String initials, String fullName) {
        this.initials = initials;
        this.fullName = fullName;
    }
}
