package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


public interface UiSpeciesAttributes {
     Long getId();

     String getSpeciesName();

     String getCommonName();

     Boolean getIsInvertSized();

     Double getL5();

     Double getL95();

     Long getMaxAbundance();

     Long getLmax();

}
