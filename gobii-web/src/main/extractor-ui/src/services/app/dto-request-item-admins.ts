import { DtoRequestItem } from '../core/dto-request-item';
import { Contact } from 'src/model/contact';

export class DtoRequestItemAdmins implements DtoRequestItem<Contact[]> {
    getUrl(): string {
        return "contacts/admin"
    }
    getRequestBody(): string {
        return null;
    }
    resultFromJson(json: any): Contact[] {
        let returnVal:Contact[] = [];
        if (json.result.data) {
            json.result.data.forEach(
                item => {
                    returnVal.push(
                        new Contact(item.piContactId, item.piContactLastName, item.piContactFirstName, null, item.email, item.organizationId, null)
                    );
                }
            )
        };
        return returnVal;
    }
    
}