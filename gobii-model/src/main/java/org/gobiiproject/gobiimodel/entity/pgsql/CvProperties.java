/**
 * ProjectProperties.java
 * 
 * Project Properties attribute type in Project class
 * @author Rodolfo N. Duldulao, Jr.
 * @since 2020-03-13
 */
package org.gobiiproject.gobiimodel.entity.pgsql;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
@Deprecated
public class CvProperties implements java.io.Serializable, Map<String,String> {

    private Map<String, String> values = new java.util.HashMap<String, String>();
    
    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return values.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null) return null;
        return values.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(Map m) {
        values.putAll(m);

    }

    @Override
    public void clear() {
        values.clear();

    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<String> values() {
        return values.values();
    }

    @Override
    public Set<Map.Entry<String,String>> entrySet() {
        return values.entrySet();
    }
    
    
}