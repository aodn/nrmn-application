package au.org.aodn.nrmn.restapi.data.repository.projections;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ObservableItemSuperseded {

    String getSupersededIds();

    String getSupersededNames();
}
