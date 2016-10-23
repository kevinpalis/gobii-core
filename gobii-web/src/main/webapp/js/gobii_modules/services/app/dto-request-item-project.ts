import {Injectable} from "@angular/core";
import {DtoRequestItem} from "./../core/dto-request-item";
import {ProcessType} from "../../model/type-process";
import {Project} from "../../model/project";

@Injectable()
export class DtoRequestItemProject implements DtoRequestItem<Project> {

    public constructor(private projectId:number) {
        this.projectId = projectId;
    }

    public getUrl():string {
        let baseUrl:string = "brapi/v1/projects";

        let returnVal:string  = baseUrl;
        if( this.projectId ) {
            returnVal = baseUrl + "/" + this.projectId;
        }

        return returnVal;
    } // getUrl()

    private processType:ProcessType = ProcessType.READ;

    public getRequestBody():string {

        return JSON.stringify({
            "processType": ProcessType[this.processType],
            "projectId": this.projectId
        })
    }

    public resultFromJson(json):Project {

        let returnVal:Project = undefined;

        if( json.payload.data[0]) {
            returnVal = json.payload.data[0];
        }

        // json.payload.data.forEach(item => {
        //
        //
        //     returnVal.push(new Project(item.projectId,
        //         item.projectName,
        //         item.projectCode,
        //         item.projectDescription,
        //         item.piContact,
        //         item.createdBy,
        //         item.createdstring,
        //         item.modifiedBy,
        //         item.modifiedstring,
        //         item.projectStatus
        //     ));
        // });

        return returnVal;
    }


} // DtoRequestItemNameIds() 







