import { GridApi } from "ag-grid-enterprise";

export enum SourceJobType {
    INGEST = 'INGEST',
    CORRECTION = 'CORRECTION'
}

export enum StatusJobType {
    FAILED = 'FAILED',
    STAGED = 'STAGED',
    PENDING = 'PENDING',
    INGESTING = 'INGESTING',
    INGESTED = 'INGESTED',
    CORRECTED = 'CORRECTED',
    ERROR = 'ERROR',
}

enum SecUserStatus {
    PENDING = 'PENDING',
    ACTIVE = 'ACTIVE',
    DEACTIVATED = 'DEACTIVATED'
}

enum SecRoleName {
    ROLE_USER = 'ROLE_USER',
    ROLE_DATA_OFFICER = 'ROLE_DATA_OFFICER',
    ROLE_POWER_USER = 'ROLE_POWER_USER',
    ROLE_ADMIN = 'ROLE_ADMIN',
    ROLE_SURVEY_EDITOR = 'ROLE_SURVEY_EDITOR'
}

enum StagedJobEventType {
    UPLOADED = 'UPLOADED',
    STAGED = 'STAGED',
    INGESTING = 'INGESTING',
    CORRECTING = 'CORRECTING',
    INGESTED = 'INGESTED',
    CORRECTED = 'CORRECTED',
    ERROR = 'ERROR',
    SUMMARY = 'SUMMARY',
    FILTER = 'FILTER'
}
// Not very good but no way as the type are 
// not specificed initiall so all hack around
export type ExtRow =
    { [key: string]: number | string } &
    { id: number, pos: number } &
    (CorrectionRow | StagedRow);

export interface CellFormatType {
    [key: string]: {
        levelId: string,
        message: string,
        rowIds?: Array<number>
    }
};

export interface Location {
    locationId: number;
    locationName: string;
    isActive: boolean;
}

export interface SurveyValidationError {
    id: number,
    message: string,
    categoryId: 'FORMAT' | 'DATA' | 'SPAN',
    levelId: 'BLOCKING' | 'WARNING' | 'DUPLICATE' | 'INFO',
    rowIds: Array<number>,
    columnNames: Array<string>,
};

export interface Site {
    siteId: number,
    siteCode: string,
    siteName: string,
    oldSiteCodes: Array<string>,
    location: Location,
    state: string,
    country: string,
    latitude: number,
    longitude: number,
    mpa: string,
    protectionStatus: string,
    relief?: number;
    slope?: number;
    waveExposure?: number;
    currents?: number;
    isActive: boolean;
    siteAttribute?: Record<string, string>;
}

export interface Program {
    programId: number;
    programName?: string;
    isActive?: boolean;
}

export interface Method {
    methodId: number;
    methodName?: string;
    isActive?: boolean;
}

export interface Diver {
    diverId: number;
    initials: string;
    fullName: string;
    created?: string; // LocalDateTime can be represented as a string in TypeScript
}

interface AphiaRelType {
    aphiaRelTypeId: number;
    aphiaRelTypeName?: string;
}

interface ObsItemType {
    obsItemTypeId: number;
    obsItemTypeName?: string;
    isActive?: boolean;
}

interface LengthWeight {
    a?: number;
    b?: number;
    cf?: number;
    sgfgu?: string;
}

export interface ObservableItem {
    observableItemId: number;
    created?: string; // LocalDateTime can be represented as a string in TypeScript
    updated?: string;
    observableItemName: string;
    obsItemType: ObsItemType;
    commonName?: string;
    phylum?: string;
    className?: string;
    order?: string;
    family?: string;
    genus?: string;
    speciesEpithet?: string;
    letterCode?: string;
    reportGroup?: string;
    habitatGroups?: string;
    supersededBy?: string;
    isInvertSized?: boolean;
    obsItemAttribute?: Record<string, string>;
    lengthWeight?: LengthWeight;
    aphiaId?: number;
    aphiaRelType?: AphiaRelType;
    methods?: Array<Method>;
}

interface MeasureTypeEntity {
    measureTypeId: number;
    measureTypeName?: string;
    isActive?: boolean;
}

interface Measure {
    measureId: number;
    measureType?: MeasureTypeEntity;
    measureName?: string;
    seqNo?: number;
    isActive?: boolean;
}

export interface Observation {
    observationId: number;
    measureValue?: number;
    observationAttribute?: Record<string, string>;
    surveyMethod?: SurveyMethodEntity;
    diver?: Diver;
    observableItem: ObservableItem;
    measure: Measure;
}

export interface SurveyMethodEntity {
    surveyMethodId: number;
    blockNum?: number;
    surveyNotDone?: boolean;
    surveyMethodAttribute?: Record<string, string>;
    survey: Survey;
    method: Method;
    observations?: Array<Observation>;
}

export interface Survey {
    surveyId: number,
    surveyDate: Date,
    created: Date,
    updated: Date,
    surveyTime: Date,
    depth: number,
    surveyNum: number,
    visibility: number,
    direction: string,
    longitude: number,
    latitude: number,
    protectionStatus: string,
    insideMarinePark: string,
    notes: string,
    pqCatalogued: boolean,
    locked: boolean,
    pqZipUrl: string,
    pqDiverId: number,
    blockAbundanceSimulated: boolean,
    projectTitle: string,
    site: Site,
    program: Program,
    surveyMethods: Array<SurveyMethodEntity>,
}

interface SecRole {
    name: SecRoleName;
    version: number;
}

interface SecUser {
    userId: number;
    version: number;
    fullName?: string;
    expires?: string; // LocalDateTime can be represented as a string in TypeScript
    email: string;
    hashedPassword?: string;
    status: SecUserStatus;
    roles?: Array<SecRole>;
}

interface SpeciesSearchBodyDto {
    observableItemId?: number;
    startDate?: string;
    endDate?: string;
    geometry?: string;
    offset?: number;
    locationIds?: number[];
}

export interface CorrectionDiffCell {
    diffRowId?: string;
    columnName?: string;
    speciesName?: string;
    oldValue?: string;
    newValue?: string;
}

export interface CorrectionDiff {
    deletedRows?: string[];
    insertedRows?: string[];
    cellDiffs?: CorrectionDiffCell[];
}

interface StagedJobLog {
    id: number;
    stagedJob: StagedJob;
    eventTime?: string; // Timestamp can be represented as a string in TypeScript
    eventType: StagedJobEventType;
    details?: string;
    summary?: CorrectionDiff;
    filterSet?: SpeciesSearchBodyDto;
}

interface StagedJob {
    id: number;
    reference?: string;
    isExtendedSize?: boolean;
    status?: StatusJobType;
    source?: SourceJobType;
    program: Program;
    created?: string; // Timestamp can be represented as a string in TypeScript
    lastUpdated?: string;
    creator: SecUser;
    logs?: Array<StagedJobLog>;
    rows?: Array<StagedRow>;
    surveyIds?: Array<number>;
}

export interface StagedRow {
    id: number;
    surveyId?: string;
    diffRowId?: string;
    siteCode?: string;
    date?: string;
    diver?: string;
    depth?: string;
    method?: string;
    block?: string;
    species?: string;
    buddy?: string;
    siteName?: string;
    longitude?: string;
    latitude?: string;
    vis?: string;
    time?: string;
    direction?: string;
    pqs?: string;
    code?: string;
    commonName?: string;
    total?: string;
    inverts?: string;
    pos?: number;
    isInvertSizing?: string;
    measureJson?: string;
    stagedJob?: StagedJob;
    created?: string; // Timestamp can be represented as a string in TypeScript
    lastUpdated?: string;
}

export interface CorrectionRow {
    surveyId?: number;
    diffRowId?: string;
    diver?: string;
    pqDiver?: string; // Corresponds to "P-Qs" in the JSON
    siteCode?: string;
    siteName?: string;
    direction?: string;
    latitude?: string;
    longitude?: string;
    isInvertSizing?: string;
    species?: string;
    commonName?: string;
    code?: string;
    method?: string;
    block?: string;
    depth?: string;
    date?: string;
    time?: string;
    vis?: string;
    total?: string;
    measureJson?: string;
}

export interface CorrectionRows {
    programId?: number;
    programName?: string;
    surveyIds?: number[];
    rows: CorrectionRow[];
}

export interface ValidationResponse {
    job?: StagedJob;
    rowCount?: number;
    siteCount?: number;
    newSiteCount?: number;
    diverCount?: number;
    newDiverCount?: number;
    obsItemCount?: number;
    newObsItemCount?: number;
    surveyCount?: number;
    existingSurveyCount?: number;
    incompleteSurveyCount?: number;
    foundSites?: Record<string, boolean>;
    errors?: SurveyValidationError[];
    summary?: CorrectionDiff;
}

export interface CorrectionRequestBody {
    programId?: number;
    isMultiple?: boolean;
    surveyIds?: Array<number>;
    rows?: StagedRow[];
}

export interface InternalContext {
    errors: Array<SurveyValidationError>,
    highlighted: [],
    popUndo: (api: GridApi) => any,
    pushUndo: (api: GridApi) => any,
    putRowIds: [],
    undoStack: [],
    fullRefresh: boolean,
    useOverlay: string,
    validations?: Array<SurveyValidationError>,
    diffSummary?: CorrectionDiff,
    pendingPasteUndo: [],
    summary?: [] | ValidationResponse,
    errorList?: [],
    pasteMode: boolean,
    cellValidations?: CellFormatType[],
    originalData?: CorrectionRow[],
    rowData?: ExtRow[],
    rowPos?: number[],
    originalRowPos?: number[],
    isAdmin?: boolean,
}

export interface RowUpdate {
    rowId: number,
    row: StagedRow,
}

export interface JobResponse {
    job: StagedJob,
    rows: Array<StagedRow>,
}

export interface Measurement {
    field: string,
    invertSize: string,
    fishSize: string,
}
