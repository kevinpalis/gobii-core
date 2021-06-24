package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.gobiiproject.gobiimodel.validators.CheckAtLeastOneNotNullOrEmpty;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@CheckAtLeastOneNotNullOrEmpty(
    fieldNames = {
        "sampleDbIds", "sampleNames", "samplePUIs",
        "germplasmDbIds", "germplasmNames", "germplasmPUIs",
        "sampleGroupDbIds"
    })
public class SamplesSearchQueryDTO {


    @Size(max = 1000, message = "Only 1000 sampleDbIds allowed per query")
    private Set<Integer> sampleDbIds = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 sampleDbIds allowed per query")
    private Set<String> sampleNames = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 sampleDbIds allowed per query")
    private Set<String> samplePUIs = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 germplasmDbIds allowed per query")
    private Set<Integer> germplasmDbIds = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 germplasmNames allowed per query")
    private Set<String> germplasmNames = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 germplasmPUIs allowed per query")
    private Set<String> germplasmPUIs = new HashSet<>();

    @Size(max = 1000, message = "Only 1000 sampleGroupDbIds allowed per query")
    private Set<Integer> sampleGroupDbIds = new HashSet<>();

    @JsonIgnore
    public boolean isSamplesQueriesAllEmpty() {
        return CollectionUtils.isEmpty(this.sampleDbIds) &&
            CollectionUtils.isEmpty(this.sampleNames) &&
            CollectionUtils.isEmpty(this.samplePUIs) &&
            CollectionUtils.isEmpty(this.germplasmDbIds) &&
            CollectionUtils.isEmpty(this.germplasmNames) &&
            CollectionUtils.isEmpty(this.germplasmPUIs) &&
            CollectionUtils.isEmpty(this.sampleGroupDbIds);

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

    public void setGermplasmDbIds(Set<Integer> germplasmDbIds) {
        this.germplasmDbIds = germplasmDbIds;
    }

    public void setSampleGroupDbIds(Set<Integer> sampleGroupDbIds) {
        this.sampleGroupDbIds = sampleGroupDbIds;
    }

    public Set<String> getGermplasmNames() {
        return germplasmNames;
    }

    public void setGermplasmNames(Set<String> germplasmNames) {
        this.germplasmNames = germplasmNames;
    }

    public Set<String> getGermplasmPUIs() {
        return germplasmPUIs;
    }

    public void setGermplasmPUIs(Set<String> germplasmPUIs) {
        this.germplasmPUIs = germplasmPUIs;
    }

    public Set<Integer> getSampleGroupDbIds() {
        return sampleGroupDbIds;
    }
}
