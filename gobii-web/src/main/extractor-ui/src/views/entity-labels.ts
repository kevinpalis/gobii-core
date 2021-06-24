import {EntityType, EntitySubType} from "../model/type-entity";
import {CvFilterType} from "../model/cv-filter-type";
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {ExtractorItemType} from "../model//type-extractor-item";
import {GobiiExtractFormat} from "../model/type-extract-format";

export class Labels {


    private static _instance:Labels = null;

    public static instance():Labels {

        if( this._instance === null ) {
            this._instance = new Labels();
        }

        return this._instance;
    }

    private constructor() {
        this.entityNodeLabels[EntityType.DATASET] = "Data Set";
        this.entityNodeLabels[EntityType.PLATFORM] = "Platform";
        this.entityNodeLabels[EntityType.MAPSET] = "Mapset";
        this.entityNodeLabels[EntityType.PROJECT] = "Project";
        this.entityNodeLabels[EntityType.EXPERIMENT] = "Experiment";
        this.entityNodeLabels[EntityType.MARKER_GROUP] = "Marker Groups";


        this.cvFilterNodeLabels[CvFilterType.DATASET_TYPE] = "Dataset Type";
        this.cvFilterNodeLabels[CvFilterType.JOB_STATUS] = "Job Status";

        this.entitySubtypeNodeLabels[EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR] = "Principal Investigator";
        this.entitySubtypeNodeLabels[EntitySubType.CONTACT_SUBMITED_BY] = "Submit As";

        this.extractorFilterTypeLabels[GobiiExtractFilterType.WHOLE_DATASET] = "By Dataset";
        this.extractorFilterTypeLabels[GobiiExtractFilterType.BY_SAMPLE] = "By Sample";
        this.extractorFilterTypeLabels[GobiiExtractFilterType.BY_MARKER] = "By Marker";

        this.treeExtractorTypeLabels[ExtractorItemType.SAMPLE_LIST_ITEM] = "Sample List";
        this.treeExtractorTypeLabels[ExtractorItemType.SAMPLE_FILE] = "Sample File";
        this.treeExtractorTypeLabels[ExtractorItemType.MARKER_LIST_ITEM] = "Marker List";
        this.treeExtractorTypeLabels[ExtractorItemType.MARKER_FILE] = "Marker File";
        this.treeExtractorTypeLabels[ExtractorItemType.CROP_TYPE] = "Crop Type";
        this.treeExtractorTypeLabels[ExtractorItemType.EXPORT_FORMAT] = "Format";
        this.treeExtractorTypeLabels[ExtractorItemType.JOB_ID] = "Job ID";
        this.treeExtractorTypeLabels[ExtractorItemType.SAMPLE_LIST_TYPE] = "List Type";

        this.extractFormatTypeLabels[GobiiExtractFormat.HAPMAP] = "Hapmap";
        this.extractFormatTypeLabels[GobiiExtractFormat.FLAPJACK] = "Flapjack";
        this.extractFormatTypeLabels[GobiiExtractFormat.META_DATA_ONLY] = "Meta Data";

    }


    public entityNodeLabels: Map < EntityType, string > = new Map<EntityType,string>();
    public entitySubtypeNodeLabels: Map < EntitySubType, string > = new Map<EntitySubType,string>();
    public cvFilterNodeLabels: Map < CvFilterType, string > = new Map<CvFilterType,string>();
    public extractorFilterTypeLabels: Map < GobiiExtractFilterType, string > = new Map<GobiiExtractFilterType, string>();
    public treeExtractorTypeLabels: Map<ExtractorItemType,string> = new Map<ExtractorItemType,string>();
    public extractFormatTypeLabels: Map<GobiiExtractFormat,string> = new Map<GobiiExtractFormat,string>();

}