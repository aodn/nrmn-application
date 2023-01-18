package au.org.aodn.nrmn.restapi.service.upload;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.RandomStringUtils;
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

    public Collection<SharedLinkDto> createLinks(SecUser user, String receipient, String expires, Collection<String> endpoints) throws Exception {

        // Default to 1 week expiry.
        var expiresDt = LocalDateTime.now().plusWeeks(1);
        try {
            expiresDt = LocalDate.parse(expires).atStartOfDay();
        } catch (DateTimeParseException dtpe) {
            logger.error(dtpe.getMessage());
        }

        var sharedLinks = new ArrayList<SharedLinkDto>();
        for(var ep : endpoints) {
            var sharedLink = new SharedLink();
            sharedLink.setUser(user);
            sharedLink.setReceipient(receipient);
            sharedLink.setLinkType(SharedLinkType.valueOf(ep.toUpperCase()));
            var secret = RandomStringUtils.random(20, 0, 0, true, true, null, new SecureRandom());
            sharedLink.setSecret(secret);
            sharedLink.setTargetUrl(s3IO.createS3Link(ep.toLowerCase(), secret, expiresDt));
            sharedLink.setExpires(expiresDt);
            sharedLinkRepository.save(sharedLink);
            sharedLinks.add(new SharedLinkDto(sharedLink));
        }

        return sharedLinks;
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
