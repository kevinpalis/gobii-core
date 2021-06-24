import {Injectable} from "@angular/core";
import {DtoRequestItem} from "./../core/dto-request-item";
import {ProcessType} from "../../model/type-process";
import {Cv} from "../../model/cv";
import {EntityStats} from "../../model/entity-stats";
import {EntityType} from "../../model/type-entity";

export enum EntityRequestType {Count, ChildCountOfParent, LasetUpdated}

//@Injectable()
export class DtoRequestItemEntityStats implements DtoRequestItem<EntityStats> {


    public constructor(private requestType: EntityRequestType,
                       private entityType: EntityType,
                       private parentEntityType: EntityType,
                       private parentId: number) {
    }

    public getUrl(): string {

        let returnVal: string;
        let baseUrl:string = "crops/{cropType}/gobii/v1/";
        let entityTypeName: string = EntityType[this.entityType];

        if (this.requestType === EntityRequestType.Count) {

            returnVal = baseUrl + "entities/" + entityTypeName + "/count";

        } else if (this.requestType === EntityRequestType.ChildCountOfParent) {

            let parentEntityTypeName: string = EntityType[this.parentEntityType];
            let parentIdAsString: string = this.parentId.toLocaleString();

            returnVal = baseUrl + "entities/" + parentEntityTypeName + "/" + parentIdAsString + "/" + entityTypeName + "/count";
        } else {

            returnVal = baseUrl + "entities/" + entityTypeName + "/lastmodified";
        }

        return returnVal;

    } // getUrl()

    // this should be a GET only
    public getRequestBody(): string {
        return null;
    }

    public resultFromJson(json): EntityStats {

        let returnVal: EntityStats;

        let entityStatsFromServer:any = json.payload.data[0];
        if( entityStatsFromServer ) {
            returnVal = new EntityStats(entityStatsFromServer.count,
                new Date(entityStatsFromServer.lastModified)
            );
        }

        return returnVal;
    }


} // DtoRequestItemNameIds() 







