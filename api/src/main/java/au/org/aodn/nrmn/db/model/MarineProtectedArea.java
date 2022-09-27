package au.org.aodn.nrmn.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;

@Entity
@Immutable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ui_mpa")
public class MarineProtectedArea {
    @Id
    @Column(name = "mpa")
    private String name;
}

