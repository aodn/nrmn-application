package au.org.aodn.nrmn.restapi.repository.projections;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ObservableItemSupersededBy {

    String getSupersededByIds();

    String getSupersededByNames();
}
