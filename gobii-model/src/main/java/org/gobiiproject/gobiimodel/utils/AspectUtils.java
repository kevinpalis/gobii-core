package org.gobiiproject.gobiimodel.utils;

import java.lang.reflect.Field;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectTable;

public class AspectUtils {
    public static String getTableName(Class<?> entity) throws NullPointerException {
        return entity.getDeclaredAnnotation(GobiiAspectTable.class).name();
    }
    
    public static void setField(Object instance, 
                         String fieldName, 
                         Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);

    }

}
