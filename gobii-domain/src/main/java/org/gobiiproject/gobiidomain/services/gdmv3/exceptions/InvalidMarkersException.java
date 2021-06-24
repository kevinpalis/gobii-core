package org.gobiiproject.gobiidomain.services.gdmv3.exceptions;

import java.util.List;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;

import lombok.Data;

@Data
public class InvalidMarkersException extends GobiiException {

	/**
     *
     */
    private static final long serialVersionUID = -6345477148156538354L;
    private List<MarkerStatus> statusList;

    public InvalidMarkersException(List<MarkerStatus> statusList) {
        super(GobiiStatusLevel.ERROR, 
              GobiiValidationStatusType.BAD_REQUEST,
              "" 
        );
        this.statusList = statusList;
	}
    
}