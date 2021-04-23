package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Immutable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ui_report_group")
public class ReportGroup {
    @Id
    @Column(name = "report_group")
    private String name;
}

