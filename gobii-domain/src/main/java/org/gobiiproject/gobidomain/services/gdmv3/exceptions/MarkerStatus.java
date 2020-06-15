package org.gobiiproject.gobidomain.services.gdmv3.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarkerStatus {
    
    private boolean isValid;

    private String error;


    public MarkerStatus(boolean isValid) {
        this(isValid, null);
    }
 
    public MarkerStatus(boolean isValid, String error) {
        this.isValid = isValid;
        this.error = error;
    }


}