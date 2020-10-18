package au.org.aodn.nrmn.restapi.dto.stage;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class UploadResponse {
    private Optional<FileUpload> file;
    private List<ErrorInput> errors;
}
