package org.gobiiproject.gobiimodel.modelmapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

/**
 * Module to map CV Ids to CV terms and vice versa.
 */
public class CvMapper {

    /**
     * Function maps the properties object with CV term as a key to Json Node with
     * cvIds as Key.
     *
     * @param cvList     List of Cv Entity
     * @param properties Map object with cv term as key which needs to be mapped to
     *                   their cvId as key
     * @return Json Node with cvId as Key. Returning Json Node as Entity Column is a
     *         Json Column.
     */
    public static JsonNode mapCvTermsToCvId(List<Cv> cvList, Map<String, String> properties) {

        JsonNode returnVal;

        ObjectMapper mapper = new ObjectMapper();

        try {

            Map<String, String> cvIdMappedProperties = new HashMap<>();

            for (Cv cv : cvList) {
                if (properties.containsKey(cv.getTerm())) {

                    cvIdMappedProperties.put(cv.getCvId().toString(), properties.get(cv.getTerm()));

                }
            }

            returnVal = mapper.reader().readTree(mapper.writeValueAsString(cvIdMappedProperties));

        } catch (Exception e) {

            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage());

        }

        return returnVal;
    }


    public static Map<String, String> mapCvIdToCvTerms(List<Cv> cvList, JsonNode propertiesJson) {

        Map<String, String> returnVal = new HashMap<>();

        return mapCvIdToCvTerms(cvList, propertiesJson, returnVal);

    }

    public static Map<String, String> mapCvIdToCvTerms(List<Cv> cvList,
                                                       JsonNode propertiesJson, Map<String, String> returnVal) {

        String cvValue;

        try {
            for(Cv cv : cvList) {
                if(propertiesJson.has(cv.getCvId().toString())) {

                    cvValue = propertiesJson.get(cv.getCvId().toString()).asText();

                    if(StringUtils.isNotEmpty(cvValue)) {
                        returnVal.put(cv.getTerm(), cvValue);
                    }
                }
            }
        }
        catch(Exception e) {

            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage());

        }

        return returnVal;

    }

    /**
     * Convert Cv data to List
     * @param cvList
     * @param propertiesJson
     * @return
     */
    public static List<CvPropertyDTO> listCvIdToCvTerms(List<Cv> cvList, Map<String, String> props) {
        List<CvPropertyDTO> dtoList = new java.util.ArrayList<>();
        if (cvList == null || props == null) return dtoList;
        try {
            for(Cv cv : cvList) {
                if(props.containsKey(cv.getCvId().toString())) {
                    CvPropertyDTO cvPropertyDTO = new CvPropertyDTO();
                    ModelMapper.mapEntityToDto(cv, cvPropertyDTO);
                    cvPropertyDTO.setPropertyValue(props.get(cv.getCvId().toString()));
                    dtoList.add(cvPropertyDTO);
                }
            }
        }
        catch(Exception e) {

            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage());

        }
        return dtoList;
    }

    /**
     * Convert List of DTOs to Map
     * @param dtoList
     * @return
     */
    public static Map<String, String> mapCvIdToCvTerms( List<CvPropertyDTO> dtoList) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        if (dtoList == null) return map;

        dtoList.forEach((item) -> {
            if(item.getPropertyId() != null) {
                map.put(item.getPropertyId().toString(), item.getPropertyValue());
            }
        });
        return map;
    }

    /**
     * Convert Cv db entities to DTO
     * @param cvList
     * @return
     */
    public static List<CvPropertyDTO> convert(List<Cv> cvList) {
        List<CvPropertyDTO> dtoList = new java.util.ArrayList<>();
        try {
            for(Cv cv : cvList) {
                CvPropertyDTO cvPropertyDTO = new CvPropertyDTO();
                ModelMapper.mapEntityToDto(cv, cvPropertyDTO);
                dtoList.add(cvPropertyDTO);
            }
        }
        catch(Exception e) {

            throw new GobiiException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.UNKNOWN,
                    e.getMessage());

        }
        return dtoList;
    }
}
