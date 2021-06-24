import {EntitySubType, EntityType} from "./type-entity";
import {CvFilterType} from "./cv-filter-type";
import {GobiiExtractFilterType} from "./type-extractor-filter";
import {ExtractorItemType} from "./type-extractor-item";

export class GobiiFileItemCompoundId {

    constructor(private _extractorItemType: ExtractorItemType = ExtractorItemType.UNKNOWN,
                private _entityType: EntityType = EntityType.UNKNOWN,
                private _entitySubType: EntitySubType = EntitySubType.UNKNOWN,
                private _cvFilterType: CvFilterType = CvFilterType.UNKNOWN,
                private _cvFilterValue: string = null,
                private _isExtractCriterion: boolean = true) {

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

    public compoundIdeEquals(gobiiFileItemCompoundId: GobiiFileItemCompoundId): boolean {
        return this.getExtractorItemType() === gobiiFileItemCompoundId.getExtractorItemType()
            && this.getEntityType() === gobiiFileItemCompoundId.getEntityType()
            && this.getEntitySubType() === gobiiFileItemCompoundId.getEntitySubType()
            && this.getCvFilterType() === gobiiFileItemCompoundId.getCvFilterType()
            && this.getCvFilterValue() === gobiiFileItemCompoundId.getCvFilterValue()
    }


    getExtractorItemType(): ExtractorItemType {
        return this._extractorItemType;
    }

    setExtractorItemType(value: ExtractorItemType): GobiiFileItemCompoundId {
        if (value != null) {
            this._extractorItemType = value;
        } else {
            this._extractorItemType = ExtractorItemType.UNKNOWN;
        }

        return this;
    }

    getEntityType(): EntityType {
        return this._entityType;
    }

    setEntityType(value: EntityType): GobiiFileItemCompoundId {

        if (value != null) {
            this._entityType = value;
        } else {
            this._entityType = EntityType.UNKNOWN;
        }

        return this;
    }

    getEntitySubType(): EntitySubType {
        return this._entitySubType;
    }

    setEntitySubType(value: EntitySubType): GobiiFileItemCompoundId {

        if (value != null) {
            this._entitySubType = value;
        } else {
            this._entitySubType = EntitySubType.UNKNOWN;
        }

        return this;
    }

    getCvFilterType(): CvFilterType {
        return this._cvFilterType;
    }

    setCvFilterType(value: CvFilterType): GobiiFileItemCompoundId {
        if (value != null) {
            this._cvFilterType = value;
        } else {
            this._cvFilterType = CvFilterType.UNKNOWN;
        }
        return this;
    }

    getCvFilterValue(): string {
        return this._cvFilterValue;
    }

    setCvFilterValue(value: string): GobiiFileItemCompoundId {

        this._cvFilterValue = value;
        return this;
    }


    getIsExtractCriterion(): boolean {
        return this._isExtractCriterion;
    }

    setIsExtractCriterion(value: boolean): GobiiFileItemCompoundId {
        this._isExtractCriterion = value;
        return this;
    }
}
