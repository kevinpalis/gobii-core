import {Injectable} from "@angular/core";
import {DtoRequestItem} from "./../core/dto-request-item";
import {ProcessType} from "../../model/type-process";
import {GobiiFileItem} from "../../model/gobii-file-item";
import {FilterParams} from "../../model/filter-params";
import {JsonToGfi} from "./jsontogfi/json-to-gfi";
import {FilterType} from "../../model/filter-type";
import {FilterParamNames} from "../../model/file-item-param-names";


//@Injectable()
export class DtoRequestItemGfi implements DtoRequestItem<GobiiFileItem[]> {

    public constructor(private fileItemParams: FilterParams,
                       private id: string = null,
                       private jsonToGfi: JsonToGfi) {
        this.fileItemParams = fileItemParams;
        this.id = id;
        this.jsonToGfi = jsonToGfi;

        if (this.fileItemParams.getFilterType() !== FilterType.ENTITY_LIST
            && this.fileItemParams.getFilterType() !== FilterType.ENTITY_BY_ID) {
            throw new Error("The FileItemParams with ID "
                + this.fileItemParams.getQueryName()
                + " is not of type " + FilterType[FilterType.ENTITY_LIST]
                + " or " + FilterType[FilterType.ENTITY_BY_ID]);
        }
    }

    public getUrl(): string {

        let returnVal: string = "crops/{cropType}/gobii/v1";

        if (this.fileItemParams.getQueryName() === FilterParamNames.DATASET_BY_DATASET_ID ||
            this.fileItemParams.getQueryName() === FilterParamNames.DATASET_LIST ) {
            returnVal += "/datasets";

            if (this.fileItemParams.getFilterType() === FilterType.ENTITY_BY_ID) {
                returnVal += "/" + this.id;
            }
        } else if (this.fileItemParams.getQueryName() === FilterParamNames.ANALYSES_BY_DATASET_ID) {
            returnVal += "/datasets/" + this.id + "/analyses";
        }

        return returnVal;
    } // getUrl()

    private processType: ProcessType = ProcessType.READ;

    // this is probably not being used anymore
    public getRequestBody(): string {

        return JSON.stringify({
            "processType": ProcessType[this.processType],
            "id": this.id
        })
    }

    public resultFromJson(json): GobiiFileItem[] {

        let returnVal: GobiiFileItem[] = [];
        json.payload.data.forEach(jsonItem => {

            returnVal.push(this.jsonToGfi.convert(jsonItem));

        });

        return returnVal;

    }
} // DtoRequestItemNameIds()







