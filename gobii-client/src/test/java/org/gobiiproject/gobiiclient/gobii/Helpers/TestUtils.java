package org.gobiiproject.gobiiclient.gobii.Helpers;


import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiimodel.dto.children.NameIdDTO;
import org.gobiiproject.gobiiapimodel.payload.Header;
import org.gobiiproject.gobiiapimodel.payload.HeaderStatusMessage;
import org.gobiiproject.gobiimodel.dto.system.ConfigSettingsDTO;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.ServerCapabilityType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
//import org.gobiiproject.gobiimodel.tobemovedtoapimodel.Header;;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by Phil on 4/18/2016.
 */
public class TestUtils {

    public static boolean checkAndPrintHeaderMessages(Header header) {

        boolean returnVal = false;

        if (LineUtils.isNullOrEmpty(header.getGobiiVersion())) {
            returnVal = true;
            System.out.println("Response does not indicate the gobii web services version");
        }


        if (!header.getStatus().isSucceeded() ||
                header
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(headerStatusMessage -> headerStatusMessage.getGobiiStatusLevel().equals(GobiiStatusLevel.VALIDATION))
                        .count() > 0) {
            returnVal = true;
            System.out.println();
            System.out.println("*** Header errors: ");
            for (HeaderStatusMessage currentStatusMesage : header.getStatus().getStatusMessages()) {
                System.out.println(currentStatusMesage.getMessage());
            }
        }

        return returnVal;
    }

    public static boolean isBackEndSupported() throws Exception {

        boolean returnVal = false;

        GobiiClientContextAuth.authenticate();

        RestUri confgSettingsUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_CONFIGSETTINGS);
        GobiiEnvelopeRestResource<ConfigSettingsDTO,ConfigSettingsDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(confgSettingsUri);
        PayloadEnvelope<ConfigSettingsDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(ConfigSettingsDTO.class);

        if (resultEnvelope.getHeader().getStatus().isSucceeded()) {
            ConfigSettingsDTO configSettingsDTOResponse = resultEnvelope.getPayload().getData().get(0);
            returnVal = configSettingsDTOResponse.getServerCapabilities().containsKey(ServerCapabilityType.GOBII_BACKEND)
                    && configSettingsDTOResponse.getServerCapabilities().get(ServerCapabilityType.GOBII_BACKEND);
        }

        return returnVal;
    }


    public static List<NameIdDTO> sortNameIdList(List<NameIdDTO> nameIdListDtoResponse) {
        List<NameIdDTO> sortedNameIdList = nameIdListDtoResponse;
        Collator myCollator = Collator.getInstance();

        Collections.sort(sortedNameIdList, new Comparator<NameIdDTO>() {
            @Override
            public int compare(NameIdDTO lhs, NameIdDTO rhs) {
                return myCollator.compare(lhs.getName().toLowerCase(), rhs.getName().toLowerCase());
            }
        });
        return sortedNameIdList;
    }


    public static boolean isNameIdListSorted(List<NameIdDTO> nameIdListDtoResponse) {
        boolean isSorted = true;
        if (nameIdListDtoResponse.size() > 0) {//if empty, exit
            Collator myCollator = Collator.getInstance();
            String prev = nameIdListDtoResponse.get(0).getName();
            for (NameIdDTO nameIdDTO : nameIdListDtoResponse) {
                if (myCollator.compare(prev.toLowerCase(), nameIdDTO.getName().toLowerCase()) > 0) {
                    isSorted = false;
                    break;
                }
            }
        }
        return isSorted;
    }

    public static void printNameIdList(List<NameIdDTO> nameIdListDtoResponse) {
        System.out.println("NameIDDTOList");
        for (NameIdDTO nameIdDTO : nameIdListDtoResponse) {
            System.out.println(nameIdDTO.getName());
        }
    }

    public static List<Integer> makeListOfIntegersInRange(Integer size, Integer maxOfRange) {

        List<Integer> returnVal = new ArrayList<>();
        Random random = new Random(27350);

        while (returnVal.size() < size) {

            Integer currentVal = random.nextInt(maxOfRange);

            if (!returnVal.contains(currentVal)) {
                returnVal.add(currentVal);
            }
        }


        return returnVal;

    }
}
