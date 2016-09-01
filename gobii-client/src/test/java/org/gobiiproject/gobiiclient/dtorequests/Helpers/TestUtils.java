package org.gobiiproject.gobiiclient.dtorequests.Helpers;

import org.gobiiproject.gobiimodel.dto.DtoMetaData;
import org.gobiiproject.gobiimodel.dto.container.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.container.NameIdListDTO;
import org.gobiiproject.gobiimodel.dto.header.HeaderStatusMessage;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Phil on 4/18/2016.
 */
public class TestUtils {

    public static boolean  checkAndPrintHeaderMessages(DtoMetaData dtoMetaData ) {

        boolean returnVal = false;

        if (!dtoMetaData.getDtoHeaderResponse().isSucceeded()) {
            returnVal = true;
            System.out.println();
            System.out.println("*** Header errors: ");
            for (HeaderStatusMessage currentStatusMesage : dtoMetaData.getDtoHeaderResponse().getStatusMessages()) {
                System.out.println(currentStatusMesage.getMessage());
            }
        }

        return returnVal;
    }

    public static List<NameIdDTO> sortNameIdList(List<NameIdDTO> nameIdListDtoResponse){
        List<NameIdDTO> sortedNameIdList = nameIdListDtoResponse;
        Collator myCollator = Collator.getInstance();

        Collections.sort(sortedNameIdList, new Comparator<NameIdDTO>() {
            @Override
            public int compare(NameIdDTO lhs, NameIdDTO rhs) {
                return myCollator.compare(lhs.getName().toLowerCase(),rhs.getName().toLowerCase());
            }
        });
        return sortedNameIdList;
    }


    public static boolean isNameIdListSorted(List<NameIdDTO> nameIdListDtoResponse){
        boolean isSorted = true;
        if (nameIdListDtoResponse.size() >0) {//if empty, exit
            Collator myCollator = Collator.getInstance();
            String prev = nameIdListDtoResponse.get(0).getName();
            for (NameIdDTO nameIdDTO : nameIdListDtoResponse) {
                if (myCollator.compare(prev.toLowerCase(), nameIdDTO.getName().toLowerCase()) > 0){
                    isSorted = false;
                    break;
                }
            }
        }
        return isSorted;
    }

    public static void printNameIdList(List<NameIdDTO> nameIdListDtoResponse){
        System.out.println("NameIDDTOList");
       for(NameIdDTO nameIdDTO : nameIdListDtoResponse){
           System.out.println(nameIdDTO.getName());
       }
    }
}
