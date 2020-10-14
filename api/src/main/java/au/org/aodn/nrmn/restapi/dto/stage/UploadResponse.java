package au.org.aodn.nrmn.restapi.dto.stage;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UploadResponse {
    private List<FileUpload> files;
    private List<ErrorInput> errors;
}
