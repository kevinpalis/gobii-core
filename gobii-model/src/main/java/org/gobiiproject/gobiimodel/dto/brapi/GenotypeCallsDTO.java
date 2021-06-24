package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityMap;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Marker;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true, value={
        "id", "allowedProcessTypes", "hdf5MarkerIndex", "hdf5SampleIndex"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenotypeCallsDTO extends DTOBase{

    @JsonSerialize(using = ToStringSerializer.class)
    private Integer variantSetDbId;

    @GobiiEntityMap(paramName="dnaRunId", entity = DnaRun.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer callSetDbId;

    @GobiiEntityMap(paramName="dnaRunName", entity = DnaRun.class)
    private String callSetName;

    @GobiiEntityMap(paramName="markerId", entity = Marker.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer variantDbId;

    @GobiiEntityMap(paramName="markerName", entity = Marker.class)
    private String variantName;

    private Map<String, Object> genotype;

    private String genotypeLiklihood;

    private String phaseSet;

    private String sepPhased;

    private String sepUnphased;

    private String unknownString;

    @Override
    public Integer getId() { return null; }

    @Override
    public void setId(Integer id) { this.callSetDbId = null; }

    public Integer getVariantSetDbId() { return this.variantSetDbId; }

    public void setVariantSetDbId(Integer variantSetDbId) { this.variantSetDbId = variantSetDbId; }

    public Integer getCallSetDbId() { return this.callSetDbId; }

    public void setCallSetDbId(Integer id) { this.callSetDbId = id; }

    public String getCallSetName() { return this.callSetName; }

    public void setCallSetName(String callSetName) { this.callSetName = callSetName; }

    public Integer getVariantDbId() { return this.variantDbId; }

    public void setVariantDbId(Integer variantDbId) { this.variantDbId = variantDbId; }

    public String getVariantName() { return this.variantName; }

    public void setVariantName(String variantName) { this.variantName = variantName; }

    public Map<String, Object> getGenotype() {
        return genotype;
    }

    public void setGenotype(Map<String, Object> genotype) {
        this.genotype = genotype;
    }

    public String getGenotypeLiklihood() { return this.genotypeLiklihood; }

    public void setGenotypeLiklihood(String genotypeLiklihood) { this.genotypeLiklihood = genotypeLiklihood; }

    public String getPhaseSet() { return this.phaseSet; }

    public void setPhaseSet(String phaseSet) { this.phaseSet = phaseSet; }

    public String getSepPhased() {
        return sepPhased;
    }

    public void setSepPhased(String sepPhased) {
        this.sepPhased = sepPhased;
    }

    public String getSepUnphased() {
        return sepUnphased;
    }

    public void setSepUnphased(String sepUnphased) {
        this.sepUnphased = sepUnphased;
    }

    public String getUnknownString() {
        return unknownString;
    }

    public void setUnknownString(String unknownString) {
        this.unknownString = unknownString;
    }
}
