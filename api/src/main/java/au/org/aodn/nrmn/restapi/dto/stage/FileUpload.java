package au.org.aodn.nrmn.restapi.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUpload {
    private Long jobId;
    private Integer rowCount;
}
