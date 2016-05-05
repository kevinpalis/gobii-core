// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.dtorequests;

import com.google.gson.JsonObject;
import org.gobiiproject.gobiiclient.core.RestRequest;
import org.gobiiproject.gobiiclient.core.Urls;
import org.gobiiproject.gobiimodel.dto.container.ContactDTO;
import org.gobiiproject.gobiimodel.dto.container.DataSetDTO;
import org.gobiiproject.gobiimodel.types.SystemUserDetail;
import org.gobiiproject.gobiimodel.types.SystemUserNames;
import org.gobiiproject.gobiimodel.types.SystemUsers;

public class DtoRequestContact {

    public ContactDTO processContact(ContactDTO contactDTO) throws Exception {

        ContactDTO returnVal = null;

        RestRequest<ContactDTO> restRequest = new RestRequest<>(ContactDTO.class);

        SystemUsers systemUsers = new SystemUsers();
        SystemUserDetail userDetail = systemUsers.getDetail(SystemUserNames.USER_READER.toString());
        String token = restRequest.getTokenForUser(userDetail.getUserName(), userDetail.getPassword());

        returnVal = restRequest.getTypedHtppResponseForDto(Urls.URL_CONTACT, contactDTO, token);

        return returnVal;

    } // getPing()

//    public ProjectDTO updateProject(ProjectDTO projectDTO) throws Exception {
//
//        ProjectDTO returnVal = null;
//
//        RestRequest<ProjectDTO> restRequest = new RestRequest<>(ProjectDTO.class);
//
//        SystemUsers systemUsers = new SystemUsers();
//        SystemUserDetail userDetail = systemUsers.getDetail(SystemUserNames.USER_READER.toString());
//        String token = restRequest.getTokenForUser(userDetail.getUserName(), userDetail.getPassword());
//
//        returnVal = restRequest.getTypedHtppResponseForDto(Urls.URL_PING_PROJECT, projectDTO, token);
//
//        return returnVal;
//
//    }


}