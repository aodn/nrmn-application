package au.org.aodn.nrmn.restapi.dto.sharedlink;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import au.org.aodn.nrmn.restapi.data.model.SharedLink;

@Data
@NoArgsConstructor
public class SharedLinkDto {

    @Id
    private Long linkId;

    @NotEmpty
    private String targetUrl;

    private Boolean isActive;

    public SharedLinkDto(SharedLink sharedLink) {
        this.linkId = sharedLink.getLinkId();
        this.targetUrl = sharedLink.getTargetUrl();
    }
}
