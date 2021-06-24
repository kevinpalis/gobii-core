package org.gobiiproject.gobiisampletrackingdao;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DaoUtils {
    public static Map<String, Object> getHints(EntityManager em, String graphName) {
        EntityGraph<?> graph = em.getEntityGraph(graphName);
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);
        return hints;
    }

    public static String getTableName(Class<?> entity) throws NullPointerException {
        return entity.getDeclaredAnnotation(Table.class).name();
    }
}
