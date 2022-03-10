package au.org.aodn.nrmn.restapi.dto.stage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import au.org.aodn.nrmn.restapi.model.db.StagedJobLog;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StagedJobDto implements Serializable {

  private Long id;

  private String reference;

  private Boolean isExtendedSize;

  private StatusJobType status;

  private SourceJobType source;

  private String programName;

  private Timestamp created;

  private Timestamp lastUpdated;

  private String creatorEmail;

  private List<StagedJobLog> logs;

  private List<Integer> surveyIds;
}
