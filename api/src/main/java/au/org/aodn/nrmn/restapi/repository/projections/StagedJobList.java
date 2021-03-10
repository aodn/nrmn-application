package au.org.aodn.nrmn.restapi.repository.projections;

import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.SecUser;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(name = "list", types = {StagedJob.class})

public interface StagedJobList {

    Long getId();

    String getReference();

    Boolean getIsExtendedSize();

    SourceJobType getSource();

    StatusJobType getStatus();

    SecUser getCreator();

    Program getProgram();

    Timestamp getCreated();

    Timestamp getLastUpdated();




}
