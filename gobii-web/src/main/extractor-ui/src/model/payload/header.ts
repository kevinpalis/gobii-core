import {HeaderStatusMessage} from "./../dto-header-status-message"
import {DtoHeaderAuth} from "../dto-header-auth";
import {Status} from "./status"
import {Pagination} from "./pagination";

export class Header {

    public constructor(public cropType: string,
                       public dtoHeaderAuth: DtoHeaderAuth,
                       public status: Status,
                       public gobiiVersion: string,
                       public pagination: Pagination) {
    }

    public static fromJSON(json: any): Header {

        let cropType: string = json.cropType;
        let dtoHeaderAuth: DtoHeaderAuth = DtoHeaderAuth.fromJSON(json.dtoHeaderAuth);
        let status: Status = Status.fromJSON(json.status);
        let gobiiVersion: string = json.gobiiVersion;
        let pagination: Pagination =  json.pagination ? Pagination.fromJSON(json.pagination) : null;

        return new Header(cropType,
            dtoHeaderAuth,
            status,
            gobiiVersion,
            pagination); // new

    } // fromJson()
}
