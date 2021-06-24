package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
public class JsonAspect {

    private final String aspectType = "JSON";

    private Map<String, ColumnAspect> jsonMap = new HashMap<>();

    public String getAspectType() {
        return aspectType;
    }

    public Map<String, ColumnAspect> getJsonMap() {
        return jsonMap;
    }

    public void setJsonMap(Map<String, ColumnAspect> jsonMap) {
        this.jsonMap = jsonMap;
    }
}
