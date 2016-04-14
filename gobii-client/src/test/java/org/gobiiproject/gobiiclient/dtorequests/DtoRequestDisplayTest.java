// ************************************************************************
// (c) 2016 GOBii Project
// Initial Version: Phil Glaser
// Create Date:   2016-03-25
// ************************************************************************
package org.gobiiproject.gobiiclient.dtorequests;

import org.gobiiproject.gobiimodel.dto.container.DisplayDTO;
import org.gobiiproject.gobiimodel.dto.container.NameIdListDTO;
import org.junit.Assert;
import org.junit.Test;

public class DtoRequestDisplayTest {


    @Test
    public void testGetContactsByIdForContactType() throws Exception {

        DtoRequestDisplay dtoRequestDisplay = new DtoRequestDisplay();

        DisplayDTO displayDTORequest = new DisplayDTO();
        displayDTORequest.setTableName("project");


        DisplayDTO displayDTOResponse = dtoRequestDisplay.getDisplayNames(displayDTORequest);

        Assert.assertNotEquals(displayDTOResponse,null);
        Assert.assertTrue(displayDTOResponse.getDisplayNamesByColumn().size() > 0);

//        Assert.assertNotEquals(null, nameIdListDTO);
//        Assert.assertEquals(true, nameIdListDTO.getDtoHeaderResponse().isSucceeded());
//        Assert.assertTrue(nameIdListDTO.getProjectNamesById().size() >= 0);

    } // testGetMarkers()


}