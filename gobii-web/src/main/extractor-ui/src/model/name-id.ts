import {EntityType} from "./type-entity";
export class NameId {

    constructor(public id: string,
                public fkId: string,
                public name: string,
                public entityType: EntityType,
                public fkEntityType: EntityType,
                public entityLasetModified: Date) {
        
    }
}
