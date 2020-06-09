package org.gobiiproject.gobiimodel.utils;

import com.fasterxml.jackson.databind.JsonNode;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class JsonNodeUtils {

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

    public static boolean isEmpty(JsonNode jsonNode) {
        return jsonNode == null || jsonNode.size() == 0;
    }
}
