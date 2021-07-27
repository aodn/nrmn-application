package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.Optional;

public class UploadResponse {
    private Optional<Long> id;
    private String error;

    public UploadResponse(){}

    public UploadResponse(Optional<Long> id) {
        this.id = id;
    }
    public UploadResponse(String error) {
        this.error = error;
    }

    public UploadResponse(Optional<Long> id, String error) {
        this.id = id;
        this.error = error;
    }

    public Optional<Long> getId() {
        return this.id;
    }

    public String getError() {
        return this.error;
    }
}
