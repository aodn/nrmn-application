package au.org.aodn.nrmn.restapi.service.upload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.model.SharedLink;
import au.org.aodn.nrmn.restapi.data.repository.SharedLinkRepository;
import au.org.aodn.nrmn.restapi.dto.sharedlink.SharedLinkDto;
import au.org.aodn.nrmn.restapi.enums.SharedLinkType;

@Service
public class SharedLinkService {

    private static Logger logger = LoggerFactory.getLogger(SharedLinkService.class);

    @Autowired
    private SharedLinkRepository sharedLinkRepository;

    @Autowired
    private S3IO s3IO;

    public String createLink(SecUser user, SharedLinkDto sharedLinkDto) throws Exception {
        // Default to 1 week expiry.
        var expires = LocalDateTime.now().plusWeeks(1);
        try {
            expires = LocalDate.parse(sharedLinkDto.getExpires()).atStartOfDay();
        } catch (DateTimeParseException dtpe) {
            logger.error(dtpe.getMessage());
        }
        var sharedLink = new SharedLink();
        sharedLink.setUser(user);
        sharedLink.setReceipient(sharedLinkDto.getRecipient());
        sharedLink.setLinkType(SharedLinkType.valueOf(sharedLinkDto.getContent().toUpperCase()));
        sharedLink.setTargetUrl(s3IO.createS3Link(sharedLinkDto.getContent().toLowerCase(), expires));
        sharedLink.setExpires(expires);
        sharedLinkRepository.save(sharedLink);
        return sharedLink.getTargetUrl();
    }

    public void expireLink(Long id) throws Exception {
        var link = sharedLinkRepository.getReferenceById(id);
        s3IO.deleteS3Link(link.getTargetUrl());
        sharedLinkRepository.delete(link);
    }

    public void expireLink(SharedLink link) throws Exception {
        s3IO.deleteS3Link(link.getTargetUrl());
        sharedLinkRepository.delete(link);
    }
}
