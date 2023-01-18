package au.org.aodn.nrmn.restapi.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import au.org.aodn.nrmn.restapi.enums.SharedLinkType;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shared_link")
public class SharedLink {

    @Id
    @SequenceGenerator(name = "shared_link_id_seq", sequenceName = "shared_link_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="shared_link_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long linkId;

    @Enumerated(EnumType.STRING)
    @Column(name = "link_type", nullable = false)
    private SharedLinkType linkType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sec_user_id", referencedColumnName = "id", nullable = false)
    private SecUser user;

    @Column(name = "receipient", nullable = true)
    private String receipient;

    @CreationTimestamp 
    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDateTime updated;

    @Column(name = "expires", nullable = false)
    private LocalDateTime expires;

    @Column(name = "expired", nullable = true)
    private LocalDateTime expired;

    @Column(name = "target_url", nullable = true)
    private String targetUrl;

    @Column(name = "secret", nullable = true)
    private String secret;
}
