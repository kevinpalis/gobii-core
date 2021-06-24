package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobiimodel.validators.CheckAtLeastOneNotNullOrEmpty;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@CheckAtLeastOneNotNullOrEmpty(
        fieldNames = {
            "callSetDbIds", "callSetNames", "sampleDbIds", "sampleNames",
            "samplePUIs", "variantSetDbIds", "germplasmPUIs", "germplasmDbIds",
            "germplasmNames"
        })
@ToString
public class CallSetsSearchQueryDTO {

    @Size(max = 1000, message = "Only 1000 callSetIds allowed per query")
    private Set<Integer> callSetDbIds = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 callSetNames allowed per query")
    private Set<String> callSetNames = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 sampleDbIds allowed per query")
    private Set<Integer> sampleDbIds = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 sampleDbIds allowed per query")
    private Set<String> sampleNames = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 sampleDbIds allowed per query")
    private Set<String> samplePUIs = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 variantSetDbIds allowed per query")
    private Set<String> variantSetDbIds = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 germplasmPUIs allowed per query")
    private Set<String> germplasmPUIs = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 germplasmDbIds allowed per query")
    private Set<Integer> germplasmDbIds = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 germplasmNames allowed per query")
    private Set<String> germplasmNames = new HashSet<>();

    public Set<Integer> getCallSetDbIds() {
        return callSetDbIds;
    }

    public void setCallSetDbIds(Set<Integer> callSetDbIds) {
        this.callSetDbIds = callSetDbIds;
    }

    public Set<String> getCallSetNames() {
        return callSetNames;
    }

    public void setCallSetNames(Set<String> callSetNames) {
        this.callSetNames = callSetNames;
    }

    public Set<String> getGermplasmPUIs() {
        return germplasmPUIs;
    }

    public void setGermplasmPUIs(Set<String> germplasmPUIs) {
        this.germplasmPUIs = germplasmPUIs;
    }

    public Set<Integer> getSampleDbIds() {
        return sampleDbIds;
    }

    public void setSampleDbIds(Set<Integer> sampleDbIds) {
        this.sampleDbIds = sampleDbIds;
    }

    public Set<String> getSampleNames() {
        return sampleNames;
    }

    public void setSampleNames(Set<String> sampleNames) {
        this.sampleNames = sampleNames;
    }

    public Set<String> getSamplePUIs() {
        return samplePUIs;
    }

    public void setSamplePUIs(Set<String> samplePUIs) {
        this.samplePUIs = samplePUIs;
    }

    public Set<Integer> getGermplasmDbIds() {
        return germplasmDbIds;
    }

    public Set<String> getVariantSetDbIds() {
        return variantSetDbIds;
    }

    public void setVariantSetDbIds(Set<String> variantSetDbIds) {
        this.variantSetDbIds = variantSetDbIds;
    }

    public void setGermplasmDbIds(Set<Integer> germplasmDbIds) {
        this.germplasmDbIds = germplasmDbIds;
    }

    public Set<String> getGermplasmNames() {
        return germplasmNames;
    }

    public void setGermplasmNames(Set<String> germplasmNames) {
        this.germplasmNames = germplasmNames;
    }

    @JsonIgnore
    public boolean isCallSetsQueriesEmpty() {
        return CollectionUtils.isEmpty(getCallSetDbIds()) &&
            CollectionUtils.isEmpty(getCallSetNames()) &&
            CollectionUtils.isEmpty(getSampleDbIds()) &&
            CollectionUtils.isEmpty(getSampleNames()) &&
            CollectionUtils.isEmpty(getSamplePUIs()) &&
            CollectionUtils.isEmpty(getGermplasmPUIs());

    }

}
