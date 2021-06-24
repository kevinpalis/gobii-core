import {Injectable} from "@angular/core";
import {DtoRequestItem} from "./../core/dto-request-item";
import {ProcessType} from "../../model/type-process";
import {GobiiFileItem} from "../../model/gobii-file-item";
import {FilterParams} from "../../model/filter-params";
import {JsonToGfi} from "./jsontogfi/json-to-gfi";
import {FilterType} from "../../model/filter-type";
import {FilterParamNames} from "../../model/file-item-param-names";
import {PagedFileItemList} from "../../model/payload/paged-item-list";
import {Pagination} from "../../model/payload/pagination";


//@Injectable()
export class DtoRequestItemGfiPaged implements DtoRequestItem<PagedFileItemList> {

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

        if (this.fileItemParams.getQueryName() === FilterParamNames.DATASET_LIST_PAGED) {
            returnVal += "/datasets";

        }

        returnVal += "?pageSize=" + this.fileItemParams.getPageSize().toString() +
            "&pageNo=" + this.fileItemParams.getPageNum().toString() +
            "&queryId=" + this.fileItemParams.getPagedQueryId();


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

    public resultFromJson(json): PagedFileItemList {

        let fileItems: GobiiFileItem[] = [];

        let currentPage:number = json.header.pagination.currentPage;
        json.payload.data.forEach(jsonItem => {

            let currentFileItem:GobiiFileItem = this.jsonToGfi.convert(jsonItem);
            currentFileItem.setPageNumber(currentPage);
            fileItems.push(currentFileItem);

        });

        let returnVal: PagedFileItemList = new PagedFileItemList(fileItems, Pagination.fromJSON(json.header.pagination));

        return returnVal;

    }
} // DtoRequestItemNameIds()







