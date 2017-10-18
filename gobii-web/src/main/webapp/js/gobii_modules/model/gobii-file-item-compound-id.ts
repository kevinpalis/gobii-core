import {EntitySubType, EntityType} from "./type-entity";
import {CvFilterType} from "./cv-filter-type";
import {GobiiExtractFilterType} from "./type-extractor-filter";
import {ExtractorItemType} from "./type-extractor-item";

export class GobiiFileItemCompoundId {

    constructor(private _extractorItemType: ExtractorItemType = ExtractorItemType.UNKNOWN,
                private _entityType: EntityType = EntityType.UNKNOWN,
                private _entitySubType: EntitySubType = EntitySubType.UNKNOWN,
                private _cvFilterType: CvFilterType = CvFilterType.UNKNOWN) {

        if (this._cvFilterType === null) {
            this._cvFilterType = CvFilterType.UNKNOWN;
        }

        if (this._extractorItemType == null) {
            this._extractorItemType = ExtractorItemType.UNKNOWN;
        }

        if (this._entityType == null) {
            this._entityType = EntityType.UNKNOWN;
        }

        if (this._entitySubType == null) {
            this._entitySubType = EntitySubType.UNKNOWN;
        }

    }

    public equals(gobiiFileItemCompoundId:GobiiFileItemCompoundId): boolean {
        return this.getExtractorItemType() === gobiiFileItemCompoundId.getExtractorItemType()
        && this.getEntityType() === gobiiFileItemCompoundId.getEntityType()
        && this.getEntitySubType() === gobiiFileItemCompoundId.getEntitySubType()
        && this.getCvFilterType() === gobiiFileItemCompoundId.getCvFilterType()
    }


    getExtractorItemType(): ExtractorItemType {
        return this._extractorItemType;
    }

    setExtractorItemType(value: ExtractorItemType) {
        if (value != null) {
            this._extractorItemType = value;
        } else {
            this._extractorItemType = ExtractorItemType.UNKNOWN;
        }
    }

    getEntityType(): EntityType {
        return this._entityType;
    }

    setEntityType(value: EntityType) {

        if (value != null) {
            this._entityType = value;
        } else {
            this._entityType = EntityType.UNKNOWN;
        }
    }

    getEntitySubType(): EntitySubType {
        return this._entitySubType;
    }

    setEntitySubType(value: EntitySubType) {
        if (value != null) {
            this._entitySubType = value;
        } else {
            this._entitySubType = EntitySubType.UNKNOWN;
        }
    }

    getCvFilterType(): CvFilterType {
        return this._cvFilterType;
    }

    setCvFilterType(value: CvFilterType) {
        if (value != null) {
            this._cvFilterType = value;
        } else {
            this._cvFilterType = CvFilterType.UNKNOWN;
        }
    }
}
