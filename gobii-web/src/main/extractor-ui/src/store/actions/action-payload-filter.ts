import {GobiiFileItemCompoundId} from "../../model/gobii-file-item-compound-id";
import {Pagination} from "../../model/payload/pagination";
import {GobiiExtractFilterType} from "../../model/type-extractor-filter";
import {Payload} from "../../model/payload/payload";
import {GobiiFileItem} from "../../model/gobii-file-item";

export class PayloadFilter {
    constructor(public gobiiExtractFilterType: GobiiExtractFilterType,
                public targetEntityUniqueId:GobiiFileItemCompoundId,
                public relatedEntityUniqueId:GobiiFileItemCompoundId,
                public relatedEntityFilterValue: string,
                public targetEntityFilterValue: string,
                public entityLasteUpdated: Date,
                public pagination: Pagination) {

    }

    static fromPayloadFilter(payloadFilterToCopy:PayloadFilter) : PayloadFilter {

        return new PayloadFilter(
            payloadFilterToCopy.gobiiExtractFilterType,
            payloadFilterToCopy.targetEntityUniqueId,
            payloadFilterToCopy.relatedEntityUniqueId,
            payloadFilterToCopy.relatedEntityFilterValue,
            payloadFilterToCopy.targetEntityFilterValue,
            payloadFilterToCopy.entityLasteUpdated,
            payloadFilterToCopy.pagination
        );
    }
}

/**
 * bug/GSD-557
 * Interface for JobType:JobStatus key-value pair.
 * Added to define jobStatusFilterValues parameter in
 * class ExtractReadyPayloadFilter.
 * @typedef {Object.<string, Array<string>|null>}
 * example: {
 *     "extract" : "pending",
 *     ...
 * }
 */
export interface JobTypeFilters {
    [key: string]: string[];
}



/**
 * bug/GSD-557
 * @extends PayloadFilter
 * Existing PayloadFilter does not have provision for filters which requires
 * multiple conditions to be satisfied.
 * For Extract ready filter, two jobTypes with different values needs to
 * checked.
 * @param jobStatusFilterValues
 */

export class ExtractReadyPayloadFilter extends PayloadFilter{

    constructor(public gobiiExtractFilterType: GobiiExtractFilterType,
        public targetEntityUniqueId:GobiiFileItemCompoundId,
        public relatedEntityUniqueId:GobiiFileItemCompoundId,
        public relatedEntityFilterValue: string,
        public targetEntityFilterValue: string,
        public entityLasteUpdated: Date,
        public pagination: Pagination,
        public jobStatusFilterValues: JobTypeFilters
    ) {
        super(
            gobiiExtractFilterType, targetEntityUniqueId, relatedEntityUniqueId,
            relatedEntityFilterValue, targetEntityFilterValue, entityLasteUpdated,
            pagination
        );

    }

    /**
     * Checks whether the passed datasetItem is extract ready or not for this Instance.
     * @param fi - datasetItem
     */
    public checkExtractReady(fi:GobiiFileItem) {
        return ExtractReadyPayloadFilter.isExtractReady(
            fi, this.jobStatusFilterValues);
    }

    /**
     * Checks whether the datasetItem is extract ready or not using the given job status filter values.
     * @param fi - datasetItem
     * @param filterValues - job status filter values.
     */
    static isExtractReady(fi:GobiiFileItem, filterValues:JobTypeFilters) {
        let jobTypeName:string = fi.getEntity().jobTypeName.trim();
        if(jobTypeName in filterValues)
        {
            if(filterValues[jobTypeName].indexOf(
                fi.getEntity().jobStatusName.trim()) > -1){
                return true;
            }
            else {
                return false;
            }
        }
        else if(jobTypeName === 'n/a') {
            return false;
        }
        else {
            return true;
        }
    }
}

