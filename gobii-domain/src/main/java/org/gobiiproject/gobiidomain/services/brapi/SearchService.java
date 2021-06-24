package org.gobiiproject.gobiidomain.services.brapi;

import org.gobiiproject.gobiimodel.dto.noaudit.SearchResultDTO;

public interface SearchService {

    SearchResultDTO createSearchQueryResource(String cropType, Object queryObject);

    Object getSearchQuery(String resourceId, String cropType, String searchQueryTypeName);
}
