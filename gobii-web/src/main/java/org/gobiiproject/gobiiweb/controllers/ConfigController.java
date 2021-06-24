package org.gobiiproject.gobiiweb.controllers;

import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.config.KeycloakConfig;
import org.gobiiproject.gobiimodel.dto.system.ExtractorConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;

@Scope(value = "request")
@Controller
@Api
public class ConfigController {

    @Autowired
    private KeycloakConfig keycloakConfig;

    @GetMapping("/config/extractor")
    @ResponseBody
    public BrApiMasterPayload<ExtractorConfigDTO> getExtractorConfig() {

       String url = keycloakConfig.getAuthServerUrl();
       String realm = keycloakConfig.getRealm();
       String client = keycloakConfig.getExtractorUIClient();

       ExtractorConfigDTO configDTO = new ExtractorConfigDTO();

       configDTO.setAuthUrl(url);
       configDTO.setRealm(realm);
       configDTO.setClient(client);


       BrApiMasterPayload<ExtractorConfigDTO> payload =  new BrApiMasterPayload<ExtractorConfigDTO>();
       payload.setMetadata(null);
       payload.setResult(configDTO);

       return payload;
        

    }
   
}
