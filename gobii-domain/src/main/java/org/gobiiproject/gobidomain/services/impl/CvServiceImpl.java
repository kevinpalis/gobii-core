package org.gobiiproject.gobidomain.services.impl;

import org.gobiiproject.gobidomain.services.CvService;
import org.gobiiproject.gobidomain.services.DisplayService;
import org.gobiiproject.gobidomain.services.NameIdListService;
import org.gobiiproject.gobiidtomapping.*;
import org.gobiiproject.gobiimodel.dto.container.CvDTO;
import org.gobiiproject.gobiimodel.dto.container.DisplayDTO;
import org.gobiiproject.gobiimodel.dto.container.NameIdListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Angel on 4/29/2016.
 */
public class CvServiceImpl implements CvService {
	Logger LOGGER = LoggerFactory.getLogger(CvServiceImpl.class);

    @Autowired
    DtoMapCv dtoMapCv = null;


	@Override
	public CvDTO procesCv(CvDTO cvDTO) {

		CvDTO returnVal = new CvDTO();

		try {
			switch (cvDTO.getProcessType()) {
				case READ:
					returnVal = dtoMapCv.getCvDetails(cvDTO);
					break;

				default:
					throw new GobiiDtoMappingException("Unsupported procesCv type " + cvDTO.getProcessType().toString());

			}

		} catch (GobiiDtoMappingException e) {

			returnVal.getDtoHeaderResponse().addException(e);
			LOGGER.error(e.getMessage());
		}

		return returnVal;
	}
}