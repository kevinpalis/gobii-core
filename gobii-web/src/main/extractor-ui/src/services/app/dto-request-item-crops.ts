import { DtoRequestItem } from '../core/dto-request-item';
import { Crop } from '../../model/crop';

export class DtoRequestItemCrops implements DtoRequestItem<Crop[]> {
    getUrl(): string {
        return "crops"
    }
    getRequestBody(): string {
        return null;
    }
    resultFromJson(json: any): Crop[] {
        let returnVal:Crop[] = [];
        if (json.result.data) {
            json.result.data.forEach(
                item => {
                    returnVal.push(new Crop(item.cropType, item.userAuthorized));
                }
            )
        };
        return returnVal;
    }
    
}