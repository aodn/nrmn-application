package au.org.aodn.nrmn.restapi.model.db.audit;

import org.apache.logging.log4j.ThreadContext;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevision revision = (AuditRevision) revisionEntity;

        // Find currently authenticated username and add it to the revision
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String currentPrincipalName = authentication.getName();
            revision.setUsername(currentPrincipalName);
        }

        // Find the id of the api call that caused this revision and add it to the revision
        String requestId = ThreadContext.get("requestId");
        revision.setApiRequestId(requestId);
    }
}
