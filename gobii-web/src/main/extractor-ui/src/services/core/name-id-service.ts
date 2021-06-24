import {Injectable} from "@angular/core";
import {NameId} from "../../model/name-id";
import {DtoRequestService} from "./dto-request.service";
import {FilterType} from "../../model/filter-type";
import {CvFilterType, CvFilters} from "../../model/cv-filter-type";
import {EntityType, EntitySubType} from "../../model/type-entity";
import {FilterParams} from "../../model/filter-params";
import {Observable} from 'rxjs/Observable';
import {DtoRequestItemNameIds} from "../app/dto-request-item-nameids";

/**
 * Created by Phil on 3/9/2017.
 */

@Injectable()
export class NameIdService {

    constructor(private _dtoRequestService: DtoRequestService<NameId[]>,) {

    } // ctor

    private getEntityFilterValue(nameIdRequestParams: FilterParams): string {

        let returnVal: string = null;

        if (nameIdRequestParams.getEntityType() === EntityType.CONTACT) {
            if (nameIdRequestParams.getEntitySubType() === EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR) {
                returnVal = "PI";
            }
        } else if (nameIdRequestParams.getEntityType() === EntityType.CV) {
            if (nameIdRequestParams.getCvFilterType() != null && nameIdRequestParams.getCvFilterType() != CvFilterType.UNKNOWN) {
                returnVal = CvFilters.get(CvFilterType.DATASET_TYPE);
            }
        }

        return returnVal;
    }


    public get (filterParams: FilterParams, relatedEntityFilterValue: string): Observable<NameId[]> {

        return Observable.create(observer => {


                let filterType: FilterType = filterParams.getFilterType() === FilterType.NONE ? null : filterParams.getFilterType();
                let cvFilterValue: string = null;
                if (filterType === FilterType.NAMES_BY_TYPEID) {
                    cvFilterValue = relatedEntityFilterValue;
                } else if (filterType === FilterType.NAMES_BY_TYPE_NAME) {
                    cvFilterValue = filterParams.getCvFilterValue();
                }

                this._dtoRequestService.get(new DtoRequestItemNameIds(
                    filterParams.getEntityType(),
                    filterType,
                    cvFilterValue))
                    .subscribe(nameIds => {
                            let nameIdsToReturn: NameId[] = [];
                            if (nameIds && ( nameIds.length > 0 )) {
                                nameIdsToReturn = nameIds;
                            }
                            // else {
                            //     nameIdsToReturn = [new NameId("0", "<none>", nameIdRequestParams.getEntityType())];
                            // }

                            observer.next(nameIdsToReturn);
                            observer.complete();
                        },
                        responseHeader => {

                            responseHeader.status.statusMessages.forEach(headerStatusMessage => {
                                observer.error(headerStatusMessage)
                            })
                        });


            }
        );
    }
}