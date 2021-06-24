package org.gobiiproject.gobiidomain.services.brapi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiidomain.services.ConfigSettingsService;
import org.gobiiproject.gobiimodel.dto.noaudit.SearchResultDTO;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@Transactional
public class SearchServiceImpl implements SearchService {

    @Autowired
    protected ConfigSettingsService configSettingsService;

    @Override
    public SearchResultDTO createSearchQueryResource(
        String cropType, Object queryObject) {

        if (cropType == null) {
            throw new GobiiDomainException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.NONE,
                    "Internal Server Error: Unable to read crop configuration");
        }

        SearchResultDTO searchResultDTO = new SearchResultDTO();

        ObjectMapper objectMapper  = new ObjectMapper();

        try {

            String resourceId = UUID.randomUUID().toString();
            String extractQueryPath = getSearchQueryResourcePath(resourceId, cropType);

            File extractQueryFile = new File(extractQueryPath);
            if (!extractQueryFile.getParentFile().mkdirs()) {
                throw new GobiiDomainException(
                    GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.NONE,
                    "Internal Server Error");
            }

            objectMapper.writeValue(extractQueryFile, queryObject);
            searchResultDTO.setSearchResultDbId(resourceId);
            return searchResultDTO;

        } catch (IOException e) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Internal Server Error");
        }
    }

    private String getSearchQueryResourcePath(String resourceId, String cropType) {
        return Paths.get(
            configSettingsService
                .getConfigSettings()
                .getServerConfigs()
                .get(cropType)
                .getFileLocations()
                .get(GobiiFileProcessDir.RAW_USER_FILES),
            resourceId,
            "searchQuery.json").toString();
    }


    @Override
    public Object getSearchQuery(String resourceId, String cropType, String searchQueryTypeName) {

        Object samplesSearchQuery;
        ObjectMapper mapper = new ObjectMapper();

        try {

            Class<?> searchQueryType = Class.forName(searchQueryTypeName);
            String extractQueryPath = getSearchQueryResourcePath(resourceId, cropType);

            File extractQueryFile = new File(extractQueryPath);

            samplesSearchQuery =
                searchQueryType.cast(mapper.readValue(extractQueryFile, searchQueryType));

            return samplesSearchQuery;

        } catch (Exception e) {
            throw new GobiiDomainException(
                GobiiStatusLevel.ERROR,
                GobiiValidationStatusType.NONE,
                "Internal Server Error");

        }
    }
}
