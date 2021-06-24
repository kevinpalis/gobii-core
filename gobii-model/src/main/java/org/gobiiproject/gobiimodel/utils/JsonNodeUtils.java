package org.gobiiproject.gobiimodel.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonNodeUtils {

    static Logger LOGGER = LoggerFactory.getLogger(JsonNodeUtils.class);

    /**
     * https://www.geeksforgeeks.org/convert-an-iterator-to-a-list-in-java/
     * @param <T>
     * @return
     */
    public static <T> List<T> getListFromIterator(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        List<T> list = StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        return list;
    }

    public static List<String> getKeysFromJsonObject(JsonNode jsonNode) {
        List<String> datasetIds = new ArrayList<>();
        Iterator<String> datasetIdsIter = jsonNode.fieldNames();
        while (datasetIdsIter.hasNext()) {
            datasetIds.add(datasetIdsIter.next());
        }
        return datasetIds;
    }

    public static boolean isEmpty(JsonNode jsonNode) {
        return jsonNode == null || jsonNode.size() == 0;
    }
}
