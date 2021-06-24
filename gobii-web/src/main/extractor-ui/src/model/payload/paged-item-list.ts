import {Pagination} from "./pagination";
import {GobiiFileItem} from "../gobii-file-item";


export class PagedFileItemList {

    public constructor(public gobiiFileItems: GobiiFileItem[],
                       public pagination: Pagination) {
    }
}
