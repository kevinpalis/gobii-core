import {ProcessType} from "./type-process";
import {Guid} from "./guid";
import {EntitySubType, EntityType} from "./type-entity";
import {CvFilterType} from "./cv-filter-type";
import {GobiiExtractFilterType} from "./type-extractor-filter";
import {ExtractorItemType} from "./type-extractor-item";
import {GobiiFileItemCompoundId} from "./gobii-file-item-compound-id";
import {GobiiFileItemEntityRelation} from "./gobii-file-item-entity-relation";

export class GobiiFileItem extends GobiiFileItemCompoundId {

    private _fileItemUniqueId: string;

    protected constructor(private _gobiiExtractFilterType: GobiiExtractFilterType,
                          private _processType: ProcessType,
                          _extractorItemType: ExtractorItemType,
                          _entityType: EntityType,
                          _entitySubType: EntitySubType,
                          _cvFilterType: CvFilterType,
                          _cvFilterValue: string,
                          private _cropType: string,
                          private _itemId: string,
                          private _itemName: string,
                          private _selected: boolean,
                          private _required: boolean,
                          private _parentItemId: string,
                          private _entity: any = null,
                          private _entityRelations: GobiiFileItemEntityRelation[] = [],
                          private _hasEntity:boolean = false,
                          private _pageNumber:number,
                          private _isEphemeral:boolean = false) {

        super(_extractorItemType, _entityType, _entitySubType, _cvFilterType,_cvFilterValue);

        this._gobiiExtractFilterType = _gobiiExtractFilterType;
        this._processType = _processType;
        this._itemId = _itemId;
        this._itemName = _itemName;
        this._selected = _selected;
        this._required = _required;
        this._parentItemId = _parentItemId;
        this._entityRelations = _entityRelations;
        this._entity = _entity;
        this._hasEntity = _hasEntity;
        this._pageNumber = _pageNumber;
        this._isEphemeral = _isEphemeral;
        this._cropType = _cropType;

        this._fileItemUniqueId = Guid.generateUUID();
    }



    public static build(gobiiExtractFilterType: GobiiExtractFilterType,
                        processType: ProcessType): GobiiFileItem {

        let returnVal: GobiiFileItem = new GobiiFileItem(
            gobiiExtractFilterType,
            processType,
            ExtractorItemType.UNKNOWN,
            EntityType.UNKNOWN,
            EntitySubType.UNKNOWN,
            CvFilterType.UNKNOWN,
            null,
            null,
            null, //cropType;
            null,
            null,
            null,
            null,
            [],
            [],
            false,
            0,
            false
        );


        return returnVal;
    }
    
    public static copy(other: GobiiFileItem) {
        let returnVal: GobiiFileItem = new GobiiFileItem(
            null,
            null,
            ExtractorItemType.UNKNOWN,
            EntityType.UNKNOWN,
            EntitySubType.UNKNOWN,
            CvFilterType.UNKNOWN,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            [],
            [],
            false,
            0,
            false
            
        );
        Object.assign(returnVal, other);
        return returnVal;
    }

    setFileItemUniqueId(fileItemUniqueId: string): GobiiFileItem {
        this._fileItemUniqueId = fileItemUniqueId;
        return this;
    }

    getFileItemUniqueId(): string {
        return this._fileItemUniqueId;
    }

    getGobiiExtractFilterType(): GobiiExtractFilterType {
        return this._gobiiExtractFilterType;
    }

    setGobiiExtractFilterType(value: GobiiExtractFilterType): GobiiFileItem {

        if (value != null) {
            this._gobiiExtractFilterType = value;
        } else {
            this._gobiiExtractFilterType = GobiiExtractFilterType.UNKNOWN;
        }
        return this;
    }

    getProcessType(): ProcessType {
        return this._processType;
    }

    setProcessType(value: ProcessType): GobiiFileItem {

        if (value != null) {
            this._processType = value;
        } else {
            this._processType = ProcessType.UNKNOWN;
        }

        return this;
    }


    getExtractorItemType(): ExtractorItemType {
        return super.getExtractorItemType();
    }

    setExtractorItemType(value: ExtractorItemType): GobiiFileItem {

        super.setExtractorItemType(value);
        return this;
    }

    getEntityType(): EntityType {
        return super.getEntityType();
    }

    setEntityType(value: EntityType): GobiiFileItem {

        super.setEntityType(value);
        return this;
    }

    getEntitySubType(): EntitySubType {
        return super.getEntitySubType();
    }

    setEntitySubType(value: EntitySubType): GobiiFileItem {

        super.setEntitySubType(value);
        return this;
    }

    getCvFilterType(): CvFilterType {
        return super.getCvFilterType();
    }

    setCvFilterType(value: CvFilterType): GobiiFileItem {
        super.setCvFilterType(value);
        return this;
    }

    getCvFilterValue(): string {
        return super.getCvFilterValue();
    }

    setCvFilterValue(value: string) {

        super.setCvFilterValue(value);
        return this;
    }

    getIsExtractCriterion(): boolean {
        return super.getIsExtractCriterion();
    }

    setIsExtractCriterion(value: boolean): GobiiFileItem {
        super.setIsExtractCriterion(value);
        return this;
    }

    getItemId(): string {
        return this._itemId;
    }

    setItemId(value: string): GobiiFileItem {
        this._itemId = value;
        return this;
    }

    getItemName(): string {
        return this._itemName;
    }

    setItemName(value: string): GobiiFileItem {
        this._itemName = value;
        return this;
    }

    getSelected(): boolean {
        return this._selected;
    }

    setSelected(value: boolean): GobiiFileItem {
        this._selected = value;
        return this;
    }

    getRequired(): boolean {
        return this._required;
    }

    setRequired(value: boolean): GobiiFileItem {
        this._required = value;
        return this;
    }

    getParentItemId(): string {
        return this._parentItemId;
    }

    setParentItemId(parentIteIid: string): GobiiFileItem {
        this._parentItemId = parentIteIid;
        return this;
    }

    setEntity(entity: any) {
        this._entity = entity;
        this._hasEntity = true;
        return this;
    }

    getEntity(): any {
        return this._entity;
    }

    hasEntity() :boolean {
        return this._hasEntity;
    }

    withRelatedEntityValue(gobiiFileItemEntityRelation: GobiiFileItemEntityRelation, relatedId: string): GobiiFileItem {

        let existingGobiiFileItemEntityRelation =
            this._entityRelations.find(er => er.compoundIdeEquals(gobiiFileItemEntityRelation));

        if (existingGobiiFileItemEntityRelation) {
            existingGobiiFileItemEntityRelation.setRelatedEntityId(relatedId);
        } else {
            this._entityRelations.push((new GobiiFileItemEntityRelation).setRelatedEntityId(relatedId))
        }

        return this;
    }

    withRelatedEntity(newGobiiFileItemEntityRelation: GobiiFileItemEntityRelation): GobiiFileItem {

        let existingGobiiFileItemEntityRelation =
            this._entityRelations.find(er => er.compoundIdeEquals(newGobiiFileItemEntityRelation));

        if (existingGobiiFileItemEntityRelation) {
            existingGobiiFileItemEntityRelation.setRelatedEntityId(newGobiiFileItemEntityRelation.getRelatedEntityId());
        } else {
            this._entityRelations.push(newGobiiFileItemEntityRelation)
        }

        return this;
    }

    getRelatedEntityFilterValue(compoundUniqueId:GobiiFileItemCompoundId): string {

        let returnVal:string = null;

        let gobiiFileItemEntityRelation =
            this._entityRelations.find(er => er.compoundIdeEquals(compoundUniqueId));

        if( gobiiFileItemEntityRelation) {
            returnVal = gobiiFileItemEntityRelation.getRelatedEntityId()
        }
        return returnVal;

    }

    getRelatedEntities(): GobiiFileItemEntityRelation[] {
        return this._entityRelations;
    }


    setPageNumber(value:number): GobiiFileItem {
        this._pageNumber = value;
        return this;
    }

    getPageNumber(): number {
        return this._pageNumber;
    }

    getIsEphemeral(): boolean {
        return this._isEphemeral;
    }

    setIsEphemeral(value: boolean): GobiiFileItem {
        this._isEphemeral = value;
        return this;
    }

    getCropType(): string {
        return this._cropType;
    }

    setCropType(value: string) {
        this._cropType = value;
        return this;
    }

} // GobiiFileItem()
