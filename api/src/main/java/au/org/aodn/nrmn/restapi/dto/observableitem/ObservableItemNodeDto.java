package au.org.aodn.nrmn.restapi.dto.observableitem;

import java.util.ArrayList;
import java.util.List;

public class ObservableItemNodeDto {

    public ObservableItemGetDto parent;
    public ObservableItemGetDto self;
    public List<ObservableItemNodeDto> children = new ArrayList<>();

}
