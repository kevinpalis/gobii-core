import {Injectable} from "@angular/core";
import {HttpValues} from "../../model/http-values";
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "./authentication.service";
import {DtoRequestItem} from "./dto-request-item";
import {DtoHeaderResponse} from "../../model/dto-header-response";
import {PayloadEnvelope} from "../../model/payload/payload-envelope";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import {Header} from "../../model/payload/header";
import {Status} from "../../model/payload/status";
import {HeaderStatusMessage} from "../../model/dto-header-status-message";
import { map, flatMap, switchMap, tap } from 'rxjs/operators';
import { getCvTermsDataType } from 'src/store/reducers/fileitems-reducer';
import { connectableObservableDescriptor } from 'rxjs/internal/observable/ConnectableObservable';

@Injectable()
export class DtoRequestService2<T> {


    constructor(private _http: HttpClient,
                private _authenticationService: AuthenticationService) {
    }


    getGobiiCropType(): string {
        return this._authenticationService.getGobiiCropType();
    }

   
    // public post(dtoRequestItem: DtoRequestItem<T>): Observable<T> {

    //     let scope$ = this;

    //     return Observable.create(observer => {

    //         scope$._authenticationService
    //         .getToken()
    //         .pipe(
    //             switchMap( (token: string) =>  {
    //                 let headers = HttpValues.makeTokenHeaders(token, scope$._authenticationService.getGobiiCropType());
    //                 headers.keys().forEach(h => { console.log(h + ": " + headers.get(h))});
    //                 return this._http.post(dtoRequestItem.getUrl(), dtoRequestItem.getRequestBody(), {headers: headers}) 
    //             })
    //         )
    //         .subscribe(
    //             json => 
    //             {
    //                 let payloadResponse: PayloadEnvelope = PayloadEnvelope.fromJSON(json);

    //                 if (payloadResponse.header.status.succeeded) {
    //                     let result = dtoRequestItem.resultFromJson(json);
    //                     observer.next(result);
    //                     observer.complete();
    //                 } else {
    //                     observer.error(payloadResponse.header);
    //                 }
    //             },
    //             json => {
    //                 let obj = JSON.parse(json._body)
    //                 let payloadResponse: PayloadEnvelope = PayloadEnvelope.fromJSON(obj);
    //                 observer.error(payloadResponse.header);
    //             } // subscribe http
    //         )}
    //     );
    // }

    public get(dtoRequestItem: DtoRequestItem<T>): Observable<T> {

        let scope$ = this;

        return Observable.create(observer => {

            
            scope$._authenticationService
            .getToken()
            .pipe(
                //tap(token => {console.log(token);}),
                switchMap( (token: string) =>  {
                    let headers = HttpValues.makeTokenHeaders(token, scope$._authenticationService.getGobiiCropType());
                    return this._http
                        .get(dtoRequestItem.getUrl().replace("{cropType}", scope$._authenticationService.getGobiiCropType()), {headers: headers});
                })
                
            ).subscribe(
                json => 
                {
                    //console.log(json);
                    //let payloadResponse: PayloadEnvelope = PayloadEnvelope.fromJSON(json);
                    let result = dtoRequestItem.resultFromJson(json);
                    observer.next(result);
                    observer.complete();
                },
                json => {
                    console.log("Got error " + json);
                    //console.log("Got " + json._body);
                    //let obj = JSON.parse(json._body)
                    //let payloadResponse: PayloadEnvelope = PayloadEnvelope.fromJSON(obj);
                    observer.error(json);
                } // subscribe http
            )}
        );
    }


    // public get(dtoRequestItem: DtoRequestItem<T>, authenticate: boolean = true): Observable<T> {

    //     let scope$ = this;
    //     return Observable.create(observer => {

    //         //let token: string = this._authenticationService
    //         //    .getToken();
    //         scope$._authenticationService.getToken().subscribe(
    //             (token: string) => 
    //         )
    //         if (token || !authenticate) {

    //             let headers = HttpValues.makeTokenHeaders(token, scope$._authenticationService.getGobiiCropType());

    //             this._http
    //                 .get(dtoRequestItem.getUrl(),
    //                     {headers: headers})
    //                 .pipe(
    //                     map((response: any) => response.json())
    //                 )
    //                 .subscribe(json => {

    //                         let payloadResponse: PayloadEnvelope = PayloadEnvelope.fromJSON(json);

    //                         if (payloadResponse.header.status.succeeded) {
    //                             scope$._gobbiiVersion = payloadResponse.header.gobiiVersion;
    //                             let result = dtoRequestItem.resultFromJson(json);
    //                             observer.next(result);
    //                             observer.complete();
    //                         } else {
    //                             observer.error(payloadResponse);
    //                         }

    //                     },
    //                     json => {
    //                         let obj = JSON.parse(json._body)
    //                         let payloadResponse: PayloadEnvelope = PayloadEnvelope.fromJSON(obj);
    //                         observer.error(payloadResponse.header);
    //                     }); // subscribe http

    //         } else {
    //             let header: Header = new Header(null, null, new Status(false, [new HeaderStatusMessage("Unauthenticated", null, null)]), null, null);
    //             observer.error(header);
    //         }

    //     }); // observable

    // }


}
