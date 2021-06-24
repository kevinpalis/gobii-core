package org.gobiiproject.gobiimodel.dto.noaudit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.gobiiproject.gobiimodel.dto.auditable.sampletracking.GermplasmDTO;

import java.util.List;

/**
 * Created by VCalaminos on 5/3/2019.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GermplasmListDTO {


    @JsonUnwrapped
    public List<GermplasmDTO> germplasms;

    public List<GermplasmDTO> getGermplasms() {
        return this.germplasms;
    }

    public void setGermplasms(List<GermplasmDTO> germplasms) {
        this.germplasms = germplasms;
    }

}
