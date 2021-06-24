import {GobiiFileItem} from "../../../model/gobii-file-item";
import {JsonToGfi} from "./json-to-gfi";
import {EntityType} from "../../../model/type-entity";
import {ExtractorItemType} from "../../../model/type-extractor-item";
import {ProcessType} from "../../../model/type-process";
import {GobiiExtractFilterType} from "../../../model/type-extractor-filter";
import {FilterParams} from "../../../model/filter-params";
import {FilterParamsColl} from "../../core/filter-params-coll";
import {Analysis} from "../../../model/analysis";

export class JsonToGfiAnalysis implements JsonToGfi {

    constructor(private _filterParams: FilterParams,
                private _filterParamsColl: FilterParamsColl) {
        this._filterParams = _filterParams;
        this._filterParamsColl = _filterParamsColl;
    } //ctor

    public convert(jsonItem): GobiiFileItem {

        let returnVal:GobiiFileItem  = null;

        let analysis: Analysis = new Analysis(
            jsonItem.analysisId,
            jsonItem.analysisName,
            jsonItem.analysisDescription,
            jsonItem.anlaysisTypeId,
            jsonItem.program,
            jsonItem.programVersion,
            jsonItem.algorithm,
            jsonItem.sourceName,
            jsonItem.sourceVersion,
            jsonItem.sourceUri,
            jsonItem.referenceId,
            jsonItem.timeExecuted,
            jsonItem.statusId);

        // note that we are the parent id to the job status ID. This is because
        // the primary filtering use case is by job status.
        returnVal = GobiiFileItem.build(
            GobiiExtractFilterType.UNKNOWN, ProcessType.READ
        ).setExtractorItemType(ExtractorItemType.ENTITY)
            .setEntityType(EntityType.ANALYSIS)
            .setItemName(analysis.analysisName)
            .setItemId(analysis.analysisId.toString())
            .setEntity(analysis);

        return returnVal;
    }
}