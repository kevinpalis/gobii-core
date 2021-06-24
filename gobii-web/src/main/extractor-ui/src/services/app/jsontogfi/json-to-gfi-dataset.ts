import {GobiiFileItem} from "../../../model/gobii-file-item";
import {JsonToGfi} from "./json-to-gfi";
import {EntityType} from "../../../model/type-entity";
import {ExtractorItemType} from "../../../model/type-extractor-item";
import {ProcessType} from "../../../model/type-process";
import {GobiiExtractFilterType} from "../../../model/type-extractor-filter";
import {DataSet} from "../../../model/dataset";
import {FilterParams} from "../../../model/filter-params";
import {GobiiFileItemEntityRelation} from "../../../model/gobii-file-item-entity-relation";
import {FilterParamsColl} from "../../core/filter-params-coll";
import {FilterParamNames} from "../../../model/file-item-param-names";

export class JsonToGfiDataset implements JsonToGfi {

    constructor(private _filterParams: FilterParams,
                private _filterParamsColl: FilterParamsColl) {
        this._filterParams = _filterParams;
        this._filterParamsColl = _filterParamsColl;
    } //ctor

    public convert(jsonItem): GobiiFileItem {

        let returnVal:GobiiFileItem;

        let dataset: DataSet = new DataSet(
            jsonItem.id,
            jsonItem.piContactId,
            jsonItem.projectId,
            jsonItem.experimentId,
            jsonItem.datasetName,
            jsonItem.callingAnalysisId,
            jsonItem.dataTable,
            jsonItem.dataFile,
            jsonItem.qualityTable,
            jsonItem.qualityFile,
            jsonItem.status,
            jsonItem.datatypeId,
            jsonItem.analysesIds,
            new Date(jsonItem.createdDate),
            jsonItem.jobStatusId,
            jsonItem.jobStatusName,
            jsonItem.jobTypeId,
            jsonItem.jobTypeName,
            jsonItem.jobSubmittedDate ? new Date(jsonItem.jobSubmittedDate) : null,
            jsonItem.experimentName,
            jsonItem.projectName,
            jsonItem.protocolId,
            jsonItem.protocolName,
            jsonItem.platformId,
            jsonItem.platformName,
            jsonItem.callingAnalysisName,
            jsonItem.piEmail,
            jsonItem.piFirstName,
            jsonItem.piLastName,
            jsonItem.datatypeName,
            jsonItem.totalSamples,
            jsonItem.totalMarkers,
            jsonItem.modifiedDate ? new Date(jsonItem.modifiedDate): null,
            jsonItem.loaderLastName,
            jsonItem.loaderFirstName);

        let piContactRelation: GobiiFileItemEntityRelation = GobiiFileItemEntityRelation
            .fromGobiiFileItemCompoundId(
                this._filterParamsColl.getFilter(
                    FilterParamNames.CONTACT_PI_FILTER_OPTIONAL,
                    GobiiExtractFilterType.WHOLE_DATASET
            ).getTargetEtityUniqueId())
            .setRelatedEntityId(dataset.piContactId != null ? dataset.piContactId.toString() : null);

        let projectRelation: GobiiFileItemEntityRelation = GobiiFileItemEntityRelation
            .fromGobiiFileItemCompoundId(this._filterParamsColl.getFilter(FilterParamNames.PROJECT_FILTER_OPTIONAL,
                GobiiExtractFilterType.WHOLE_DATASET).getTargetEtityUniqueId())
            .setRelatedEntityId(dataset.projectId != null ? dataset.projectId.toString() : null);

        let experimentRelation: GobiiFileItemEntityRelation = GobiiFileItemEntityRelation
            .fromGobiiFileItemCompoundId(this._filterParamsColl.getFilter(FilterParamNames.EXPERIMENT_FILTER_OPTIONAL,
                GobiiExtractFilterType.WHOLE_DATASET).getTargetEtityUniqueId())
            .setRelatedEntityId(dataset.experimentId != null ? dataset.experimentId.toString() : null);

        let statusRelation: GobiiFileItemEntityRelation = GobiiFileItemEntityRelation
            .fromGobiiFileItemCompoundId(this._filterParamsColl.getFilter(FilterParamNames.CV_JOB_STATUS,
                GobiiExtractFilterType.WHOLE_DATASET).getTargetEtityUniqueId())
            .setRelatedEntityId(dataset.jobStatusId != null ? dataset.jobStatusId.toString() : null);

        // note that we are the parent id to the job status ID. This is because
        // the primary filtering use case is by job status.
        returnVal = GobiiFileItem.build(
            GobiiExtractFilterType.UNKNOWN, ProcessType.READ
        ).setExtractorItemType(ExtractorItemType.ENTITY)
            .setEntityType(EntityType.DATASET)
            .setItemName(dataset.datasetName)
            .setItemId(dataset.id.toString())
            .setEntity(dataset)
            .withRelatedEntity(piContactRelation)
            .withRelatedEntity(projectRelation)
            .withRelatedEntity(experimentRelation)
            .withRelatedEntity(statusRelation);

        return returnVal;
    }
}