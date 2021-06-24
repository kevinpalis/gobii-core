package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import java.util.List;
import java.util.Map;

public class IflConfig {
    private List<String> loadOrder;

    private Map<String, String> requiredFields;

    public List<String> getLoadOrder() {
        return loadOrder;
    }

    public void setLoadOrder(List<String> loadOrder) {
        this.loadOrder = loadOrder;
    }

    public Map<String, String> getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(Map<String, String> requiredFields) {
        this.requiredFields = requiredFields;
    }
}
