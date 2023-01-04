package au.org.aodn.nrmn.restapi.dto.sharedlink;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import au.org.aodn.nrmn.restapi.data.model.SharedLink;

@Data
@NoArgsConstructor
public class SharedLinkDto {

    private Long linkId;

    private String targetUrl;

    private String recipient;

    private String createdBy;

    private String created;

    private String content;

    private String expires;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SharedLinkDto(SharedLink sharedLink) {
        this.linkId = sharedLink.getLinkId();
        this.targetUrl = sharedLink.getTargetUrl();
        this.recipient = sharedLink.getReceipient();
        this.content = sharedLink.getLinkType().toString().toLowerCase();
        this.createdBy = sharedLink.getUser().getEmail();
        this.created = sharedLink.getCreated().format(formatter);
        this.expires = Objects.nonNull(sharedLink.getExpires()) ? sharedLink.getExpires().format(formatter) : "";
    }
}
