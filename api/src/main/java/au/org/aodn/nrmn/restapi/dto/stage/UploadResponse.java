package au.org.aodn.nrmn.restapi.dto.stage;

import java.util.Optional;

public class UploadResponse {
    private Optional<Long> id;
    private String error;
    private String message;

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

    public UploadResponse(Optional<Long> id, String error, String message) {
        this.id = id;
        this.error = error;
        this.message = message;
    }

    public Optional<Long> getId() {
        return this.id;
    }

    public String getError() {
        return this.error;
    }

    public String getMessage() {
        return this.message;
    }
}
