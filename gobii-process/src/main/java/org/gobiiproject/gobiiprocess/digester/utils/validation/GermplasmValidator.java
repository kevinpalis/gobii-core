package org.gobiiproject.gobiiprocess.digester.utils.validation;

import org.gobiiproject.gobiimodel.dto.entity.children.NameIdDTO;

import java.util.*;

import static org.gobiiproject.gobiiprocess.digester.utils.validation.ValidationUtil.addMessageToList;
import static org.gobiiproject.gobiiprocess.digester.utils.validation.ValidationUtil.isNullAndEmpty;
import static org.gobiiproject.gobiiprocess.digester.utils.validation.ValidationUtil.printMissingFieldError;

class GermplasmValidator extends BaseValidator {
    @Override
    void validate(ValidationUnit validationUnit, String dir, List<String> errorList) throws MaximumErrorsValidationException {
        List<String> digestGermplasm = new ArrayList<>();
        if (checkForSingleFileExistence(dir, validationUnit.getDigestFileName(), digestGermplasm, errorList)) {
            String fileName = dir + "/" + validationUnit.getDigestFileName();
            validateRequiredColumns(fileName, validationUnit.getConditions(), errorList);
            validateRequiredUniqueColumns(fileName, validationUnit.getConditions(), errorList);
            for (ConditionUnit condition : validationUnit.getConditions()) {
                if (condition.type != null && condition.type.equalsIgnoreCase(ValidationConstants.DB)) {
                    if (condition.typeName != null) {
                        if (condition.typeName.equalsIgnoreCase(ValidationConstants.CV))
                            if (condition.fieldToCompare != null) {
                                if (checkForHeaderExistence(fileName, condition, errorList))
                                    validateCV(fileName, condition.fieldToCompare, errorList);
                            } else
                                printMissingFieldError("DB", "fieldToCompare", errorList);
                    } else
                        printMissingFieldError("DB", "typeName", errorList);
                }
            }
        }
    }




    /**
     * Validate terms in CV table
     *
     * @param fileName       fileName
     * @param fieldToCompare field to check
     * @param errorList      error list
     */
    private void validateCV(String fileName, String fieldToCompare, List<String> errorList) throws
            MaximumErrorsValidationException {
        List<String[]> collect = readFileIntoMemory(fileName, errorList);
        List<String> headers = Arrays.asList(collect.get(0));
        int fieldIndex = headers.indexOf(fieldToCompare);
        collect.remove(0);
        Set<String> fieldNameList = new HashSet<>();
        for (String[] fileRow : collect) {
            List<String> fileRowList = Arrays.asList(fileRow);
            if (fileRowList.size() > fieldIndex)
                if (!isNullAndEmpty(fileRowList.get(fieldIndex)))
                    fieldNameList.add(fileRowList.get(fieldIndex));
        }
        for (String fieldName : fieldNameList) {
            NameIdDTO nameIdDTO = new NameIdDTO();
            nameIdDTO.setName(fieldName);
            List<NameIdDTO> nameIdDTOList = new ArrayList<>();
            nameIdDTOList.add(nameIdDTO);
            ValidationWebServicesUtil.validateCVName(nameIdDTOList, fieldToCompare, errorList);
        }
    }
}
