/**
 * ContactsController.java
 * Gobii API endpoint controllers for contacts
 * 
 * 
 * @author Rodolfo N. Duldulao, Jr. <rnduldulaojr@gmail.com>
 * @version 1.0
 * @since 2020-09-28
 */
package org.gobiiproject.gobiiweb.controllers.gdm.v3;

import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiidomain.services.gdmv3.ContactService;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterListPayload;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;

import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

@Scope(value = "request")
@RestController
@Api()
@CrossOrigin
@AllArgsConstructor
public class ContactsController {
    
    private ContactService contactService;

    /**
     * List Contacts
     * @return
     */
    @GetMapping(GobiiControllerType.SERVICE_PATH_GOBII_V3 + "/contacts")
    @ResponseBody
    public ResponseEntity<BrApiMasterListPayload<ContactDTO>> getContacts(
        @PathVariable final String cropType,
        @RequestParam(required=false, defaultValue = "0") Integer page,
        @RequestParam(required=false, defaultValue = "1000") Integer pageSize,
        //@RequestParam(required=false) Integer organizationId
        @RequestParam(required=false, defaultValue = "pi") String role,
        @RequestParam(required = false) String userName
    ) throws Exception {
        Integer pageSizeToUse = ControllerUtils.getPageSize(pageSize);
        PagedResult<ContactDTO> pagedResult = 
            contactService.getUsers(cropType, role.toLowerCase(), page, pageSizeToUse, userName);
        BrApiMasterListPayload<ContactDTO> payload = ControllerUtils.getMasterListPayload(pagedResult);
        return ResponseEntity.ok(payload);
    }

}
