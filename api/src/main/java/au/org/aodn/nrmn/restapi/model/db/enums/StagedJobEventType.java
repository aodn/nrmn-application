package au.org.aodn.nrmn.restapi.model.db.enums;

public enum StagedJobEventType {
    UPLOADED,
    VALIDATING,
    STAGING,
    STAGED,
    EDITING,
    INGESTING,
    CORRECTING,
    INGESTED,
    CORRECTED,
    DELETED,
    ABANDONED,
    ERROR
}
