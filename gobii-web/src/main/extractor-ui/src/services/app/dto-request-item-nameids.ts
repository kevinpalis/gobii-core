import {Injectable} from "@angular/core";
import {NameId} from "../../model/name-id";
import {DtoRequestItem} from "./../core/dto-request-item";
import {EntityType, entityTypefromString} from "../../model/type-entity";
import {FilterType} from "../../model/filter-type";
import {ProcessType} from "../../model/type-process";

//@Injectable()
export class DtoRequestItemNameIds implements DtoRequestItem<NameId[]> {

    public constructor(entityType:EntityType,
                       entityFilter:FilterType = null,
                       entityFilterValue:string = null) {
        this.entityType = entityType;
        this.entityFilter = entityFilter;
        this.entityFilterValue = entityFilterValue;
    }

    public getRequestBody():string {
        return null;
    }


    public getUrl():string {

        let baseUrl:string = "crops/{cropType}/gobii/v1/names";

        let returnVal:string = baseUrl + "/" + EntityType[this.entityType].toLowerCase();

        if (this.entityFilter && (FilterType.NONE.valueOf() !== this.entityFilter)) {
            returnVal += "?"
                + "filterType=" + FilterType[this.entityFilter].toLowerCase()
                + "&"
                + "filterValue="
                + this.entityFilterValue;
        }

        return returnVal;

    } // getUrl()

    private entityType:EntityType;

    public setEntity(entityType:EntityType) {
        this.entityType = entityType;
    }

    private entityFilter:FilterType;

    private entityFilterValue:string;

    public resultFromJson(json):NameId[] {

        let returnVal:NameId[] = [];

        //let nameListItems:Object[] = json.payload.data;

        json.payload.data.forEach(item => {
            let entityLasetModified = new Date(item.entityLasetModified);
            let currentId:string = String(item.id);
            let currentName:string = item.name;
            let fkId: string = String(item.fkId);
            let fkEntityType: EntityType = entityTypefromString(item.gobiiFkEntityNameType);
            returnVal.push(new NameId(currentId,
                fkId,
                currentName,
                fkEntityType,
                this.entityType,
                entityLasetModified));
        });

        return returnVal;
        //return [new NameId(1, 'foo'), new NameId(2, 'bar')];
    }


} // DtoRequestItemNameIds() 







