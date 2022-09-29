package au.org.aodn.nrmn.restapi.data.repository.projections;

import org.springframework.data.rest.core.config.Projection;

import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;

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
