import {GobiiFileItem} from "../../../model/gobii-file-item";

export interface JsonToGfi {
    convert(json): GobiiFileItem;
}