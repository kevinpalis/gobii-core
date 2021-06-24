import {FilterType} from "./filter-type";
export class SampleMarkerList {

    constructor(public  isArray: boolean,
                public  items: string[],
                public  uploadFileName: string) {
    }
}