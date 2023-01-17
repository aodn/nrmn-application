package au.org.aodn.nrmn.restapi.dto.sharedlink;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedLinkPutDto {

    private String recipient;

    private String createdBy;

    private String created;

    private String expires;

    private List<String> endpoints;
}
