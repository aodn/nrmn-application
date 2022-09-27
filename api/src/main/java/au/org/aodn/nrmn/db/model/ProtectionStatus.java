package au.org.aodn.nrmn.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Immutable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ui_protection_status")
public class ProtectionStatus {
    @Id
    @Column(name = "protection_status")
    private String name;
}

