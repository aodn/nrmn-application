package au.org.aodn.nrmn.db.model.enums;

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
