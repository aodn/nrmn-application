package au.org.aodn.nrmn.restapi.dto.stage;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StagedJobRowDto implements Serializable {

  private Long id;

  private String reference;

  private Boolean isExtendedSize;

  private String status;

  private String source;

  private String program;

  private Timestamp created;

  private String creator;

}
