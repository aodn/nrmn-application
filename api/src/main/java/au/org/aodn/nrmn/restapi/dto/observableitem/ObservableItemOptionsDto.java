package au.org.aodn.nrmn.restapi.dto.observableitem;

import java.util.List;

import au.org.aodn.nrmn.db.model.ObsItemType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObservableItemOptionsDto {
    ObservableItemTaxonomyDto taxonomy;
    List<ObsItemType> obsItemTypes;
    List<String> reportGroups;
    List<String> habitatGroups;
}
