package au.org.aodn.nrmn.restapi.repository.projections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ObservableItemRow {

    @Schema(title = "ID")
    Integer getId();

    @Schema(title = "Type")
    String getTypeName();

    @Schema(title = "Name")
    String getName();

    @Schema(title = "Common Name")
    String getCommonName();

    @Schema(title = "Superseded By")
    String getSupersededBy();

    @Schema(title = "Superseded Names")
    String getSupersededNames();

    @Schema(title = "Superseded IDs")
    String getSupersededIDs();

    @Schema(title = "Phylum")
    String getPhylum();

    @JsonProperty(value = "class")
    String getClassName();

    @Schema(title = "Order")
    String getOrder();

    @Schema(title = "Family")
    String getFamily();

    @Schema(title = "Genus")
    String getGenus();
}
