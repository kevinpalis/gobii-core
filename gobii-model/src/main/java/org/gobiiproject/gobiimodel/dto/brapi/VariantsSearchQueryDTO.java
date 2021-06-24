package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobiimodel.validators.CheckAtLeastOneNotNullOrEmpty;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@CheckAtLeastOneNotNullOrEmpty(fieldNames = {"variantDbIds", "variantNames", "variantSetDbIds"})
public class VariantsSearchQueryDTO {

    @Size(max = 1000, message = "Only 1000 variantDbIds allowed per query")
    private Set<Integer> variantDbIds = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 variantNames allowed per query")
    private Set<String> variantNames = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 variantSetDbIds allowed per query")
    private Set<String> variantSetDbIds = new HashSet<>();

    public Set<Integer> getVariantDbIds() {
        return variantDbIds;
    }

    public void setVariantDbIds(Set<Integer> variantDbIds) {
        this.variantDbIds = variantDbIds;
    }

    public Set<String> getVariantNames() {
        return variantNames;
    }

    public void setVariantNames(Set<String> variantNames) {
        this.variantNames = variantNames;
    }

    public Set<String> getVariantSetDbIds() {
        return variantSetDbIds;
    }

    public void setVariantSetDbIds(Set<String> variantSetDbIds) {
        this.variantSetDbIds = variantSetDbIds;
    }

    @JsonIgnore
    public boolean isVariantsQueriesEmpty() {
        return CollectionUtils.isEmpty(variantDbIds) && CollectionUtils.isEmpty(variantNames);
    }
}
