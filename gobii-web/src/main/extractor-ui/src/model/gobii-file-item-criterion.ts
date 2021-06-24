import {EntitySubType, EntityType} from "./type-entity";
import {CvFilterType} from "./cv-filter-type";
import {GobiiExtractFilterType} from "./type-extractor-filter";
import {ExtractorItemType} from "./type-extractor-item";
import {GobiiFileItemCompoundId} from "./gobii-file-item-compound-id";

export class GobiiFileItemCriterion extends GobiiFileItemCompoundId {

    constructor(gobiiFileItemCompoundId: GobiiFileItemCompoundId,
                private _isPresent = false) {
        super(gobiiFileItemCompoundId.getExtractorItemType(),
            gobiiFileItemCompoundId.getEntityType(),
            gobiiFileItemCompoundId.getEntitySubType(),
            gobiiFileItemCompoundId.getCvFilterType(),
            gobiiFileItemCompoundId.getCvFilterValue());
    }

    public isPresent(): boolean {
        return this._isPresent;
    }

    public setIPresent(value: boolean) {
        this._isPresent = value;
    }

}
