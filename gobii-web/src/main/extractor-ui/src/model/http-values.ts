import {HttpHeaders} from "@angular/common/http";
import {HeaderNames} from "./header-names";

export class HttpValues {

    public static S_FORBIDDEN = 403;

    public static makeTokenHeaders(token:string,gobiiCropType:string): HttpHeaders {
        return new HttpHeaders(HttpValues.getRawHeaders(token, gobiiCropType));
        // let returnVal = this.makeContentHeaders();
        // returnVal.set(HeaderNames.headerToken, token);
        // returnVal.set(HeaderNames.headerGobiiCrop, gobiiCropType);
        // return returnVal;
    }
    
    private static getRawHeaders(token:string,gobiiCropType:string): any {
        let headers = {};
        headers[HeaderNames.headerToken] = token; //TODO: this is extraneous, keycloak-angular already adds an Authorization header
        headers[HeaderNames.headerGobiiCrop]  = gobiiCropType;
        headers['Content-Type'] = 'application/json';
        headers['Accept'] = 'application/json';
        return headers;
    }

    public static makeContentHeaders(): HttpHeaders {
        let returnVal = new HttpHeaders();
        returnVal.set('Content-Type', 'application/json');
        returnVal.set('Accept', 'application/json');
        return returnVal;
    }
    
    public static makeLoginHeaders(userName:string,password): HttpHeaders {
        let returnVal: HttpHeaders = this.makeContentHeaders();
        returnVal.set(HeaderNames.headerUserName, userName);
        returnVal.set(HeaderNames.headerPassword, password);
        return returnVal;
    }

    public static makeAuthHeader(token:string,gobiiCropType:string): HttpHeaders {
        let returnVal = this.makeContentHeaders();
        returnVal.set('Authentication', 'Bearer ' + token);
        returnVal.set(HeaderNames.headerGobiiCrop, gobiiCropType);
        return returnVal;
    }
}